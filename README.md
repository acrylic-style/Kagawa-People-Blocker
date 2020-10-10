# Kagawa People Blocker

## なにこれ？
1時間が経過したら香川県民を自動でブロックするプラグインです(休日かどうかにかかわらず1時間です)。
夜中の0時に自動でブロックが解除されます。

## 使い方
- プラグインを入れる
- `plugins/KagawaPeopleBlocker/config.yml`を作成して、こんな感じに書く
```yaml
api_key: # https://ipstack.com から入手したAPIキー
```

## 注意
- ネタプラグインです。

<details>
  <summary>
    old README.md
  </summary>

## WHAT IS THIS
  
Blocks kagawa peoples when they've reached the 1 hour limit. (regardless of holiday)
Auto un-ban in 0 am. (midnight)

## HOW TO USE

Create a folder `plugins/KagawaPeopleBlocker/config.yml` then write like this:
```yaml
api_key: # A provided api key from https://ipstack.com/
```
</details>
