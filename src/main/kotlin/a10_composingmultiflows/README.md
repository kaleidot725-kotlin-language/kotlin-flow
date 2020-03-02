# Composing Multi Flows

## ZIP

- ZIP は 2つの Flow を結合する Operator である
- (1,2,3)と(a,b,c)を結合させると(1-a, 2-b, 3-c)という Flow が作成される

## Combine

- ZIP で 2つの Flow のどちらが遅延すると、両方のデータが揃うまで Collect されない
- Combine では 2つの Flow のどちらかが、emit された時点でデータが collect される