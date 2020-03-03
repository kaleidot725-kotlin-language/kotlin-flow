# Launching Now

- なにか Event が発生したら、onEach で処理するような形になる。
  しかし Collect は同期的に実行されてしまうのであまり意味がない(ブロッキングしてしまう)
  
## Launching flow

- launchIn を利用すると、 flow を別のコルーチンで起動できる。
- launchIn には CoroutineScope を指定する必要がある。
- この仕組みを使えば、addEventListner と同じ仕組みが使え、かつ Scope を指定しているのでその Scope が削除されたら自動的にキャンセルされる。
  つまりは removeEventListener をしなくてもよい Event 購読処理が実装できる。
  
  