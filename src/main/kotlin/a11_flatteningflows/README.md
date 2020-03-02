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


