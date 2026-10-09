# Digital Filter

Minecraft 1.20.1 (Forge) 向けの Mekanism アドオンです。Digital Miner のフィルター（採掘対象）一覧に検索機能を追加します。

A client-side Mekanism addon for Minecraft 1.20.1 (Forge) that adds search to the Digital Miner filter list.

## 機能

- **フィルター一覧の検索**：Digital Miner の設定画面のタイトル横に検索欄を追加します。入力した文字で一覧を絞り込みます。
  - アイテム名、アイテム ID（`minecraft:iron_ore` など）、タグ名、Mod ID、Mod 名で検索できます。
  - タグのフィルターは、タグに含まれるブロックの名前でも見つかります（例：「鉄鉱石」で `forge:ores/iron`）。
  - スペースで区切ると、すべての語を含むものだけに絞り込みます。
  - 検索中は並べ替えの ▲▼ ボタンを隠します。
- **検索のクリア**：検索欄の右の「×」ボタンで検索をクリアします。
- **ツールチップ**：一覧のアイテムアイコンにカーソルを合わせると、アイテム名を表示します。タグのフィルターではタグ名、Mod ID のフィルターでは Mod ID も表示します。
- **入力候補**：タグフィルターと Mod ID フィルターの編集画面で、入力中の文字を含むブロックタグ・Mod ID の候補を表示します。
  - **Tab** かクリックで確定、**↑↓** で選択、マウスホイールでスクロールします。
  - `*` や `?` などのワイルドカードを含むときは候補を出しません。

## 動作環境

- Minecraft 1.20.1
- Forge 47.x
- Mekanism 10.4.x

クライアント専用です。サーバーに入れる必要はありません。

## 導入

ビルドした `digitalfilter-1.20.1-<version>.jar` を、クライアントの `mods` フォルダに入れてください。古いバージョンの jar は外してください。

## ビルド

JDK 17 が必要です。

```
gradlew build
```

`build/libs/` に jar ができます。

## 仕組み

Mixin で Mekanism のクライアント GUI クラス（`GuiDigitalMinerConfig`、`GuiFilterHolder`、`FilterButton` など）に処理を追加しています。Mekanism 本体のデータや通信には手を加えていません。

## ライセンス

MIT
