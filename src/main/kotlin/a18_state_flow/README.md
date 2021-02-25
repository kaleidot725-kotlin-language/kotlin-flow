# はじめに

最近、Android 開発において StateFlow or SharedFlow が LiveData を置き換えることができるかという話題が出てくるようになりました。そろそろ StateFlow や SharedFlow で LiveData を置き換えるか検討する必要が出てきたので StateFlow や SharedFlow の動作について調べてまとめていこうと思います。今回は StateFlow について解説をしていこうと思います。

```groovy
// 動作確認には Kotlin Coroutines 1.4.2 を利用しています。
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

# StateFlow の動作

StateFlow では emit を用いて値を送信できます。
もちろん送信した値は collect や launchIn にて受信できます。

```kotlin
fun emitTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}
```

```shell
collect:0
emit:1
collect:1
```

また StateFlow では collect または launchIn を呼び出したときに 最新値を受信できるという動作になっています。
例えば次のように初期値を設定した状態であれば collect または launchIn を呼び出すと初期値が受信できます。

```kotlin
fun initialValueTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}
```

```shell
collect:0 // launchIn する前の値が受信できている
emit:1
collect:1
```

もし特定の値を emit した状態であれば collect または launchIn を呼び出すと emit した値が受信できます。

```kotlin
fun emitTest2() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:2")
        mutableStateFlow.emit(2)

        delay(1000)
    }
}
```

```shell
emit:1
collect:1
emit:2
collect:2
```

その他にも最新値と同じ値を emit した場合に collect や launchIn で受信しないという特徴があります。
例えば次のように初期値と同じ値を emit した場合には collect や launchIn で受信しません。

```kotlin
fun distinctTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:0")
        mutableStateFlow.emit(0)

        delay(1000)
    }
}
```

```shell
collect:0
emit:0
```

もし同じ値を連続で emit した場合でも collect や launchIn で受信しません。

```kotlin
fun distinctTest2() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}
```

```
emit:1
collect:1
emit:1
```

StateFlow の特徴をまとめると以下のようになります。

- MutableStateFlow では MutableSharedFlow と同様に emit で値の送信、 collect や launchIn で値の受信ができる
- StateFlow では collect や launchIn したときに最新値を受信ができる
- StateFlow では最新値と同じ値を emit したときに collect や launchIn で同じ値を受信しない

**＊補足＊**

StateFlow は SharedFlow を継承しており、次のような動作をするように実装されているらしいです。

```kotlin
// MutableStateFlow(initialValue) is a shared flow with the following parameters:
val shared = MutableSharedFlow(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
shared.tryEmit(initialValue) // emit the initial value
val state = shared.distinctUntilChanged() // get StateFlow-like behavior
```

そのため上記のような動作をするらしいです。

- "replay = 1 で onBufferOverflow = BufferOverflow.DROP_OLDEST" であるため "collect または launchIn したときに最新値が受信できる"
- "shared.tryEmit(initialValue)" を実行しているため "suscribe したときに初期値を受信できる"
- "shared.distinctUntilChanged()" を呼び出しているため "subscribe したときに最新値と同値の値を受信しない"

# おわりに

StateFlow はかなり限定的なユースケースのために作られた SharedFlow という感じですね。
なので SharedFlow よりも使いやすく複雑になりにくかなという印象です。

# 参考文献

- [kotlinx.coroutines | StateFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)