# Flow Are Cold

- `Flow`メソッドで作成される、`Flow`オブジェクトはコールドストリームである。
- なので`Flow`で`collect`するまでは、値の受信は開始されない
- `Flow`で再度`collect`すれば、また値の受信を開始できる
