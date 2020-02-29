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
