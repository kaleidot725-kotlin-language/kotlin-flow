# SharedFlow

- SharedFlow はホットなストリームで値をすべてのコレクターに提供する
- すべてのコレクターは放出されたすべての値を取得できる。
- SharedFlow はホットなストリームであるのでアクティブなコレクターがなくても動作する

- 共有フローが完了することはない、共有フローで Flow.collect の呼び出しは正常に管理せず、
  Flow.launchIn 関数によって開始されたコルーチンも完了しません。 
- 共有フローのアクティブなコレクターはサブスクライバーと呼ばれます。
  
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

#