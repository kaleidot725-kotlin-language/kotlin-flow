# SharedFlow

- SharedFlow はホットなストリームで値をすべてのコレクターに提供する
- すべてのコレクターは放出されたすべての値を取得できる。
- SharedFlow はホットなストリームであるのでアクティブなコレクターがなくても動作する

- SharedFlowが完了することはない、Sharedで Flow.collect の呼び出しは正常に管理せず、
  Flow.launchIn 関数によって開始されたコルーチンも完了しません。 
- SharedFlowのアクティブなコレクターはサブスクライバーと呼ばれます。
  
- そのスコープで動作しているコルーチンがキャンセルされたときにSharedFlowのサブスクライバーはキャンセルされる。
- SharedFlowのサブスクライバーは常にキャンセル可能です。
- SharedFlow はアプリケーションの内部で行き来するイベントをブロードキャストするのに役立つ

# Replay cache and buffer

- ShareFlows は最近の値を特定の数分だけキャッシュする
- キャッシュされたあたいは古い値からリプレイされるらしい
- キャッシュは遅いSubscriberがEmitterを一時停止しないためにも使われる
- SharedFlows では extraBufferCapacity を利用して replay 以外の追加のバッファー容量を予約できる。

# Unbuffered shared flow

- MutableSharedFlow を作るときにはデフォルトだと replay キャッシュも extraBufferCapacity のバッファもなしになる

# SharedFlow vs BroadcastChannel

- SharedFlow は BroadcastChannel の代替として使える


# SharedFlow の Replay の動作について

```kotlin
/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
fun replayTest(emitCount: Int, replayCount: Int){
  runBlocking {
    // MutableSharedFlow を生成する
    val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount)

    // MutableSharedFlow で値をエミットする
    for (i in 1..emitCount) {
      println("emit:${i}")
      mutableSharedFlow.emit(i)
    }

    // MutableSharedFlow で値をコレクトする
    mutableSharedFlow.onEach {
      println("collect:${it}")
    }.launchIn(GlobalScope)

    // 値のコレクトが完了するまで待つ
    delay(1000)
  }
}
```

- Replay を設定すると古い値をキャッシュすることができる
- Collect したときにキャッシュされた値があれば受信される
- Replay を 0 に設定したときは Collect される前の値は受信できない
- Replay を 5 に設定したときは Collect される前の値を5つ受信できる

```
start: emitCount 10 replayCount 0
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

start: emitCount 10 replayCount 1
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
collect:10
start: emitCount 10 replayCount 5
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
start: emitCount 10 replayCount 10
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
collect:1
collect:2
collect:3
collect:4
collect:5
collect:6
collect:7
collect:8
collect:9
```