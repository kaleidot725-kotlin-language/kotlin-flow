# Flow

- ビルダー関数である`Flow`メソッドで`Flow`オブジェクトを生成する
- `Flow`メソッドの`block`は`suspend`関数になる
- `emit`メソッドで値を送信する、`collect`メソッドで値の受信を開始する

# Flow Are Cold

- `Flow`メソッドで作成される、`Flow`オブジェクトはコールドストリームである。
- なので`Flow`で`collect`するまでは、値の受信は開始されない
- `Flow`で再度`collect`すれば、また値の受信を開始できる

# Flow cancellation

- Flow のキャンセル方法は、特別なインタフェースが用意されているわけではない
- Flow では通常の Coroutine をキャンセルする方法と同じ方法でキャンセルを行う
- Coroutine では withTimeoutOrNull を利用して、一定時間経過したあとに完了していないければキャンセルができる
  これを利用して Flow をキャンセルしてみると、250msec 分の処理をして、それ以降はキャンセルされ実行されないのが確認できる。

# Flow Builder
- `flowOf` を利用すれば決められた値をエミットできる。
- `asFlow` を利用すればコレクションやシーケンスを`Flow`に変換できる

# Intermediate Flow Operator

- アップストリームにオペレータを適用し、ダウンストリームに流すことができる
- オペレータというのは`map` とか `filter` とかのことです。
- 例えば `map` を使えば、流れてきた値を加工することができます。

## Transform operator

- `map` や `filter` は単純なことしかできないが、 `Transform` を利用すると1つの値に複数の値を`emit`するような複雑な加工を施すことができる。

## Size-limiting operators
- `take` を利用すると、上限数が`emit`されたら Coroutine をキャンセルできる。

# Terminal flow operators

- `collect` は 1 つの値を単純に 1 つの値を返す `Operator` です。
- `reduce` を利用すると、
   1回目の呼び出しで１個目と2個目の値が渡される、
   2回目以降の呼び出しはその前の呼び出しで計算された値と、その次の値であるN個目が渡される

- `fold` では初期値を渡した上で実行できる、
　 1回目の呼び出しで初期値と1個目の値が渡される、
   2回目以降の呼び出しはその前の呼び出しで計算された値と、その次の値であるN個目が渡される

# Flows are sequential

- 普通に起動されば、順番にデータが流れていく.

# Flow Context

- Flow は Context を指定すれば、その Context で Flow が実行されます。
- デフォルトでは Flow のコレクターによって提供される Contenxt で実行される

## Wrong emission withContext

- CPUを利用する処理は Dispatchers.Default 、UIを更新するには Dispatchers.Main を利用する。
- 大抵は withContext は Context を変更するために使われますが、 Flow はコンテキスト予約プロパティを尊重し、異なるコンテキストから出力できない。

```
Exception in thread "main" java.lang.IllegalStateException: Flow invariant is violated:
		Flow was collected in [BlockingCoroutine{Active}@386790c2, BlockingEventLoop@432ae259],
		but emission happened in [DispatchedCoroutine{Active}@4af55fd, DefaultDispatcher].
		Please refer to 'flow' documentation or use 'flowOn' instead
```

## flowOn operator

- Flow で Context を変更したい場合は、withContext を利用せずに flowOn を利用する

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

# Composing Multi Flows

## ZIP

- ZIP は 2つの Flow を結合する Operator である
- (1,2,3)と(a,b,c)を結合させると(1-a, 2-b, 3-c)という Flow が作成される

## Combine

- ZIP で 2つの Flow のどちらが遅延すると、両方のデータが揃うまで Collect されない
- Combine では 2つの Flow のどちらかが、emit された時点でデータが collect される

# Flattening flows

- Flow では非同期なシーケンシャルな値を表現できる。
- この値を2つの文字列を出力する Flow にするには Flow<Flow<String>> にする必要がある。

```kotlin
    (1..3).asFlow().map { requestFlow(it) }.collect {
        println("Flow<String> collected $it")
        it.collect {
            println("String collected $it")
        }
    }
```

## flatMapConcat

- 最もアナログな Flow を直列化する方法です。 flatMapConcat を利用すると外部のFlowは内部のFlowの emit が完了するまで待ちます

## flatMapMerge

- flatMapMerge を利用するで、外部のFlowでemitした値をできるだけ早く出力するようになる。

## flatMapLatest

- 外部のFlowでemitし、内部のFlowでemitしている最中に、外部のFlowでemitすると、内部のFlowのemitは途中でキャンセルされる。
- つまり外部のFlow の最新値のみを必ず、内部の Flow で emit するような処理になる。

# Flow Exceptions

- Flow の例外をキャッチする方法は複数ある

## Collector try and catch

- Try and Catch で例外を取得する。
- 例外をキャッチした時点で、Flowの処理は中断される

## Everything is caught

- map を利用すれば、その値に対して、例外をスローするか決められる。

# Exeption Transparency

- Catch オペレータで例外をキャッチできる。
- catch した場合にエラー値を emit しなければならない

## Transparent catch

- Catch オペレータは上位の Flow の例外のみをキャッチする

## Catching declaratively

- onEach にて check し、 catch で例外をキャッチすることもできる。

# Flow Completion

- Flow の Collect が完了した後に、なにかアクションを実行したい。
- それを実現する方法は 2つある。

## Imperative finally block

- finally ブロックを利用することで、完了後なにか処理を実行できる。

## Declarative handling

- onCompletion オペレータを利用することで、完了後なにか処理を実行できる
- onCompletion では異常があったかどうかパラメータで受け取れる。

## Upstream exceptions only

- onCompletion がキャッチできるのは、アップストリームの例外のみです。  
  ダウンストリームで例外が発生した場合は onCompletion のパラメータは null になります。

# Imperative versus declarative

- 実行と例外をする2通りの方法があることを学んだ、どちらが良いとかではなく状況にあわせて利用すること

# Launching Now

- なにか Event が発生したら、onEach で処理するような形になる。
  しかし Collect は同期的に実行されてしまうのであまり意味がない(ブロッキングしてしまう)
## Launching flow

- launchIn を利用すると、 flow を別のコルーチンで起動できる。
- launchIn には CoroutineScope を指定する必要がある。
- この仕組みを使えば、addEventListner と同じ仕組みが使え、かつ Scope を指定しているのでその Scope が削除されたら自動的にキャンセルされる。
  つまりは removeEventListener をしなくてもよい Event 購読処理が実装できる。
  
  