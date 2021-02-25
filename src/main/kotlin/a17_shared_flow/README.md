# はじめに

最近、Android 開発において StateFlow or SharedFlow が LiveData を置き換えることができるかという話題が出てくるようになりました。そろそろ StateFlow や SharedFlow で LiveData を置き換えるか検討する必要が出てきたので StateFlow や SharedFlow の動作について調べてまとめていこうと思います。今回は SharedFlow について解説をしていこうと思います。

```shell
// 動作確認には Kotlin Coroutines 1.4.2 を利用しています。
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

# emit と launchIn の動作


SharedFlow では emit を用いて値を送信できます。もちろん送信した値は collect や launchIn にて受信できます。次のコードで emit と launchIn の動作を確認してみます。

```kotlin
/**
 * SharedFlow の基本的な動作のテスト
 */
fun defaultTest() {
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>()

        println("emit:0")
        mutableSharedFlow.emit(0)

        delay(1000)

        mutableSharedFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableSharedFlow.emit(1)

        delay(1000)
    }
}
```

launchIn の前に emit された値は subscribe されずに launchIn の後で emit された値は subscribe できています。このように SharedFlow では collect または launchIn を実行した後に emit された値を受信できるようになっています。

```kotlin
fun main() = runBlocking<Unit> {
    println("start")
    defaultTest()
}
```

```
start
emit:0
emit:1
collect:1
```

# replay の動作

SharedFlow では emit された値をどれだけキャシュするか replay で設定できるようになっています。 replay でキャッシュされた値は collect または launchIn を呼び出したときに受信できます。次のコードで複数回 emit を実行した後に launchIn を実行して replay の動作を確認してみます。

```kotlin
fun replayTest(emitCount: Int, replayCount: Int){
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount)

        for (i in 1..emitCount) {
            println("emit:${i}")
            mutableSharedFlow.emit(i)
        }

        mutableSharedFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        // 値のコレクトが完了するまで待つ
        delay(1000)
    }
}
```

emit する数を 10 で replay を 0 にして動作させます。replay は 0 ですので emit した値はキャッシュされません、そのため launchIn で値を subscribe できていません。

```kotlin
fun main() = runBlocking<Unit> {
    println("start: emitCount 10 replayCount 0")
    replayTest(emitCount = 10, replayCount = 0)
}
```

```shell
emit:1
emit:2
emit:3
emit:4
emit:5
emit:6
emit:7
emit:8
emit:9
emit:10
```

emit する数を 10 で replay を 5 にして動作させます。 replay は 5 ですので emit した値の最新5件をキャッシュします、そのため launchIn では 6~10の値を subscribe できます。

```kotlin
fun main() = runBlocking<Unit> {
    println("start: emitCount 10 replayCount 5")
    replayTest(emitCount = 10, replayCount = 5)
}
```

```shell
emit:1
emit:2
emit:3
emit:4
emit:5
emit:6
emit:7
emit:8
emit:9
emit:10
collect:6
collect:7
collect:8
collect:9
collect:10
```

というように replay を設定することで emit した値をどのぐらいキャッシュするか決められ、キャッシュした値を launchIn や collect したときに受信できるようになります。またキャッシュ値は replayCache から参照できるようになっています。なのでキャッシュ値を List\<T\> で取得したいときには replayCache を参照すればよいです。

# buffer の動作

SharedFlow には buffer という機能があり、送信するデータをバッファリングしてくれます。送信するデータをバッファリングすることで、もし動作が遅い Subscriber が存在したとしても Emitter が処理を中断せずにデータを emit できるようになります。

SharedFlow では replay と extraBufferCapacity の合計値がバッファサイズとなります。例えば replay を 5 で extraBufferCapactity を 5 にすると 10個の値をバッファリングできるようになります。

次の処理が軽い Subscriber と処理が重い Subscriber を動作させるコードでバッファサイズを変更して動作を確認してみます。

```kotlin
/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
fun bufferTest(emitCount: Int, replayCount: Int, bufferOverflow: BufferOverflow) {
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount, onBufferOverflow = bufferOverflow)

        // 動作が遅い Subscriber
        mutableSharedFlow.onEach {
            delay(100)
            println("slow:collect: ${it}")
        }.launchIn(GlobalScope)

        // 動作が早い Subscriber
        mutableSharedFlow.onEach {
            println("collect: ${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        // 1 ~ n の値を emit する 
        for (i in 1..emitCount) {
            println("emit: value ${i}")
            mutableSharedFlow.emit(i)
        }

        // 値のコレクトが完了するまで待つ
        delay(1000)
    }
}
```  

まずは emit する数を 10, replay を 0, extraBufferCapactiyを 0 にして動作させてみます。するとバッファサイズは 0 となります、そのため動作が遅い Subscriber が存在する場合にはそのためバッファが足りずに Emitter の emit が止まってしまいます。

```kotlin
bufferTest(emitCount = 10, replayCount = 0, extraBufferCapacity = 0, bufferOverflow = BufferOverflow.SUSPEND)
```

```shell
emit: value 1
emit: value 2
collect: 1
collect: 2
slow:collect: 1

emit: value 3
collect: 3
slow:collect: 2

emit: value 4
collect: 4
slow:collect: 3

emit: value 5
collect: 5
slow:collect: 4

emit: value 6
collect: 6
slow:collect: 5

emit: value 7
collect: 7
slow:collect: 6

emit: value 8
collect: 8
slow:collect: 7

emit: value 9
collect: 9
slow:collect: 8

emit: value 10
collect: 10
slow:collect: 9
slow:collect: 10
``` 

次に emit する数を 10, replay を 0, extraBufferCapactiy を 10 にして動作させてみます。この場合はバッファサイズは 10 になります、そのため動作が遅い Subscriber が存在したとしても Emitter は値1~値10 を emit するにあたってはバッファが不足することがないので処理を止めずに実行し続けることができます。

```kotlin
bufferTest(emitCount = 10, replayCount = 0, extraBufferCapacity = 10, bufferOverflow = BufferOverflow.SUSPEND)
```

```shell
emit: value 1
emit: value 2
emit: value 3
emit: value 4
emit: value 5
emit: value 6
emit: value 7
emit: value 8
emit: value 9
emit: value 10
collect: 1
collect: 2
collect: 3
collect: 4
collect: 5
collect: 6
collect: 7
collect: 8
collect: 9
collect: 10
slow:collect: 1
slow:collect: 2
slow:collect: 3
slow:collect: 4
slow:collect: 5
slow:collect: 6
slow:collect: 7
slow:collect: 8
slow:collect: 9
slow:collect: 10
```

次に emit する数を 10, replay を 10, extraBufferCapacity を 0 にして動作させてみます。この場合でもバッファサイズは 10 になります、そのため動作が遅い Subscriber が存在したとしても Emitter は値1~値10 を emit するにあたってはバッファが不足することがないので処理を止めずに実行し続けることができます。

```kotlin
bufferTest(emitCount = 10, replayCount = 10, extraBufferCapacity = 0, bufferOverflow = BufferOverflow.SUSPEND)
```

```shell
emit: value 1
emit: value 2
emit: value 3
emit: value 4
emit: value 5
emit: value 6
emit: value 7
emit: value 8
emit: value 9
emit: value 10
collect: 1
collect: 2
collect: 3
collect: 4
collect: 5
collect: 6
collect: 7
collect: 8
collect: 9
collect: 10
slow:collect: 1
slow:collect: 2
slow:collect: 3
slow:collect: 4
slow:collect: 5
slow:collect: 6
slow:collect: 7
slow:collect: 8
slow:collect: 9
slow:collect: 10
```

というように replay と extraBufferCapacity の合計値がバッファサイズとなります。ですが replay を設定すると suscribe したときに受信されるデータの数が変わってきます。なので単純にバッファサイズを増やしたいときにはextraBufferCapacity を変更するのがベターな方法になります。

# buffer option の動作

SharedFlow では buffer option ではバッファサイズを超える数の値をバッファリングしなければならなくなったときの動作（オーバーフローしたときの動作）を決めることができます。buffer option は BufferOverflow で指定できるようになっており、MutableSharedFlow だと onBufferOverflow の設定を変更することでオーバーフローしたときの動作を変更できます。

| 種類 | 説明 |
| --- | --- |
| BufferOverflow.SUSPEND | 特定の Subscriber の subscribe が終わっていなかったら Emitter の動作を一時停止する |
| BufferOverflow.DROP_LATEST | 特定の Subscriber の subscribe が終わっていなかったら最新の値をドロップする |
| BufferOverflow.DROP_OLDEST | 特定の Subscriber の subscribe が終わっていなかったら最古の値をドロップする |

次の処理が軽い Subscriber と処理が重い Subscriber を動作させるコードで buffer option を変更してオーバーフローしたときの動作を確認してみます。

```kotlin
/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
fun bufferTest(emitCount: Int, replayCount: Int, bufferOverflow: BufferOverflow, extraBufferCapacity: Int) {
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount, extraBufferCapacity = extraBufferCapacity, onBufferOverflow = bufferOverflow)

        mutableSharedFlow.onEach {
            delay(100)
            println("slow:collect: ${it}")
        }.launchIn(GlobalScope)

        mutableSharedFlow.onEach {
            println("collect: ${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        for (i in 1..emitCount) {
            println("emit: value ${i}")
            mutableSharedFlow.emit(i)
        }

        // 値のコレクトが完了するまで待つ
        delay(2000)
    }
}
```

まずは emit する数を 10, replay を 0, extraBufferCapactiyを 5, extraBufferFlow を BufferOverflow.SUSPEND にして動作させてみます。BufferOverflow.SUSPEND にするとオーバーフローしたときには Emitter の emit を一時停止します。なので処理の重い Subscriber が値を subscribe が完了せずオーバーフローしたときには Emitter の emit が一時停止しています。

```kotlin
bufferTest(emitCount = 10, replayCount = 0, extraBufferCapacity = 5, bufferOverflow = BufferOverflow.SUSPEND)
```

```shell
emit: value 1
emit: value 2
emit: value 3
emit: value 4
emit: value 5
emit: value 6
emit: value 7

collect: 1
collect: 2
collect: 3
collect: 4
collect: 5
collect: 6

slow:collect: 1
emit: value 8
collect: 7

slow:collect: 2
emit: value 9
collect: 8

slow:collect: 3
emit: value 10
collect: 9

slow:collect: 4
collect: 10

slow:collect: 5
slow:collect: 6
slow:collect: 7
slow:collect: 8
slow:collect: 9
slow:collect: 10
```

まずは emit する数を 10, replay を 0, extraBufferCapactiyを 5, extraBufferFlow を BufferOverflow.DROP_LATEST にして動作させてみます。BufferOverflow.DROP_LATEST にするとオーバーフローしたときには最新の値をドロップします。なので処理の重い Subscriber が値を subscribe が完了せずオーバーフローしたときには最新の値からドロップするので値6 〜 値10 をドロップしています。そのため Subscriber は値6 〜 値10 を suscribe できていません。

```kotlin
bufferTest(emitCount = 10, replayCount = 0, extraBufferCapacity = 5, bufferOverflow = BufferOverflow.DROP_LATEST)
```

```shell
emit: value 1
emit: value 2
emit: value 3
emit: value 4
emit: value 5
emit: value 6
emit: value 7
emit: value 8
emit: value 9
emit: value 10

collect: 1
collect: 2
collect: 3
collect: 4
collect: 5

slow:collect: 1
slow:collect: 2
slow:collect: 3
slow:collect: 4
slow:collect: 5
```

まずは emit する数を 10, replay を 0, extraBufferCapactiyを 5, extraBufferFlow を BufferOverflow.DROP_OLDEST にして動作させてみます。BufferOverflow.DROP_OLDEST にするとオーバーフローしたときには最古の値をドロップします。なので処理の重い Subscriber が値を subscribe が完了せずオーバーフローしたときには最古の値からドロップするので値1 〜 値5 をドロップしています。そのため Subscriber は値1 〜 値5 を suscribe できていません。

```kotlin
bufferTest(emitCount = 10, replayCount = 0, extraBufferCapacity = 5, bufferOverflow = BufferOverflow.DROP_OLDEST)
```

```shell
emit: value 1
emit: value 2
emit: value 3
emit: value 4
emit: value 5
emit: value 6
emit: value 7
emit: value 8
emit: value 9
emit: value 10

collect: 6
collect: 7
collect: 8
collect: 9
collect: 10

slow:collect: 6
slow:collect: 7
slow:collect: 8
slow:collect: 9
slow:collect: 10
```

というように buffer option を変更することでオーバーフローしたときの動作を変更できます。onBufferOverflow はデフォルトで BufferOverflow.SUSPEND が設定されております。 onBufferOverflow を変更することは少ないのかなと思うのですが特殊なケースで利用することがあると思うので覚えておくと良さそうです。

# おわりに

今回は SharedFlow の emit, launchIn, reply, extraBufferCapacity, onBufferFlow について解説をしました。これらの機能の解説をしましたが SharedFlow は多機能だなと思いました。多機能ですのでいろいろな場面で活躍するクラスであるのかなと思う反面、ルールを設けないと保守しづらいコードを生み出すきっかけになるクラスでもあるのかなと感じました。SharedFlow を利用する際には動作をしっかりと把握して保守しやすいコードとなるような工夫が必要かなと思います。
# 参考文献

- 📚 [kotlinx.coroutines 1.4.0: Introducing StateFlow and SharedFlow](https://blog.jetbrains.com/kotlin/2020/10/kotlinx-coroutines-1-4-0-introducing-stateflow-and-sharedflow/)
- 📚 [kotlinx.coroutines | SharedFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/)
- 📚 [kotlin coroutinesのFlow, SharedFlow, StateFlowを整理する](https://at-sushi.work/blog/24)
- 📚 [SharedFlowの深堀り、replay, bufferって何【kotlin coroutines flow】](https://at-sushi.work/blog/25)