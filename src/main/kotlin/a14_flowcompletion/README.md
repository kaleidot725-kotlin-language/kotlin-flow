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
  
