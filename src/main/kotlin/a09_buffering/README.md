# Buffering

- 順番に実行するので、Collect する側で delay を入れると、foo で emit する処理も遅くなる
- buffer を利用すると Collect 側の処理で遅延しても、foo側が遅延することがない
- flowOn Operator を利用すると Buffering と同じメカニズムに成る(CorotuineDispatcherを変更するため)

## Conflation

- Conflate を利用すると、foo が遅く、collect されない値があって、そのとき emit すると、 collect されない値をスキップする

## Processing the latest value

- Confalation は emitter と collecter 両方が遅いときに速度を向上させる方法です。
- collectLatest は collector が遅く、emitter が emit する前に処理が終わらない場合に collector の処理をキャンセルする。
  (つまりは古くなった値の処理が途中であればキャンセルする、つまり最新の値のみを処理する Operatorである)