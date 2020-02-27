# Intermediate Flow Operator

- アップストリームにオペレータを適用し、ダウンストリームに流すことができる
- オペレータというのは`map` とか `filter` とかのことです。
- 例えば `map` を使えば、流れてきた値を加工することができます。

## Transform operator

- `map` や `filter` は単純なことしかできないが、 `Transform` を利用すると1つの値に複数の値を`emit`するような複雑な加工を施すことができる。

## Size-limiting operators
- `take` を利用すると、上限数が`emit`されたら Coroutine をキャンセルできる。
