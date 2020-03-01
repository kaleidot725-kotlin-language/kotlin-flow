# Buffering

- Collect する側で delay を入れると、foo で emit する処理も、それに引きづられて遅くなる
- 上記の現象を発生させないためには、buffer を利用するとよい。

