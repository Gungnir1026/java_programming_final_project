# 版本判定與整理紀錄

整理日期：2026-10-21。

## 能確認的事實

整理前目錄共有 48 個非系統檔案：三個 Java 原始檔、41 張圖片、一份存檔、Windows 啟動腳本、VS Code 設定與 `.gitignore`。
沒有其他版本目錄、壓縮版本、Git 歷史、README、提交紀錄或明確版號。
因此選用這套程式作為整理基底，不能只憑檔案時間聲稱它是當年的最終提交版。

程式彼此有完整引用：`Main` 啟動畫面、`Game` 處理遊戲、`Upgrade` 讀寫進度。
對應的主選單、海灘、升級畫面、分類提示與垃圾圖片均存在。
原始環境設定指向 JavaFX SDK 21.0.7，因此本版沿用 JavaFX 21.0.7，使用 JDK 21 作為建置基準。

## 原始檔案保存

整理前已將 48 個檔案以原相對路徑保存於本機：

- `.archive/original-project-2026-10-21.zip`
- `.archive/SHA256-original.json`：每個原檔案的 SHA-256。

備份包含原來的三個 `.java`、全部圖片、`save.txt`、`run.bat` 與 VS Code 設定。
`.DS_Store` 為作業系統資料，不納入備份。
`.archive/` 已列入 `.gitignore`，不會混進作品儲存庫；建議另存一份備份。

## 整理前後對照

| 原位置 | 整理後 |
| --- | --- |
| `src/Main.java` | `src/main/java/com/trashsorter/Main.java` |
| `src/Game.java` 中的遊戲與物件類別 | 同一 package 下的 `Game`、`GameObject`、`Trash`、`Trashcan`、`Hud`、`TrashType` |
| `src/Upgrade.java` | 同一 package 下的 `Upgrade.java` |
| `img/` | `src/main/resources/img/`，圖片內容不變 |
| `save.txt` | `examples/save.txt`，原內容不變 |
| 固定 Windows 路徑的 `run.bat` | Maven Wrapper 與 `run.sh` / `run.bat` |

## 修正與補強

1. 移除 `C:\javafx-sdk-21.0.7` 的機器限定路徑，改由 Maven 下載相依套件。
2. 圖片改從 classpath 載入，修正 `UpGrade.jpg` 與實際 `upgrade.jpg` 的大小寫差異。
3. 圖片只載入一次，畫面更新不再反覆建立圖片物件。
4. 使用單一遊戲迴圈，將規則更新與繪圖分開，移動與旋轉以經過時間計算。
5. 遊戲與商店共用進度；讀取數值不再每次開啟存檔。
6. 升級一次完成扣款與等級變更，按住按鍵不會連續購買；失去視窗焦點時清除按鍵狀態。
7. 新玩家由零開始，舊存檔保留作範例；玩家存檔移至家目錄。
8. 防護不存在、格式錯誤及數值超限的存檔；寫入使用暫存檔替換，失敗時在畫面提示。
9. 限制金幣、等級與垃圾生成頻率，避免長時間遊玩出現溢位或無限制生成。
10. 碰撞判定同時檢查水平與垂直範圍，生命耗盡後停止更新遊戲。
11. 補上操作提示、數字鍵盤支援、Enter 開始與 R 重新遊玩。
12. 新增 README、實際截圖、JUnit 測試、GitHub Actions 與上傳說明。

## 展示時的描述

建議稱為「Java 程式設計期末專題整理版」，並依實際情況說明自己的原始分工。
原主題與主要玩法來自既有程式，本次加入的建置、結構整理與防護修正則如上表所列。
不將這次整理出的 `1.0.0` 版號當作當年的提交版號，也不推測作者、組員、學校或課程成績。
