# 素材與第三方元件

## 原專題圖片

`src/main/resources/img/` 的 41 張圖片全部沿用原專題，整理時只移動位置，未改動圖片內容。

| 目錄 | 數量 | 用途 |
| --- | --- | --- |
| `scene/` | 3 | 主選單、海灘、商店 |
| `trashcan/` | 8 | 四類垃圾桶與選取狀態 |
| `trash/` | 12 | 四類垃圾，每類三張 |
| `etc/` | 12 | 數字、金幣、愛心 |
| `wrong/` | 6 | 四類說明、分類提示與金幣不足圖卡 |

原始資料夾未附作者、來源連結或素材授權文件，目前不能確認這些圖片是否全部為自行製作。
因此本專案未替原素材宣告新的開源授權；有來源紀錄時可在此補充。
`docs/images/` 是整理版實際執行時擷取的畫面，包含上述原素材。

## 第三方元件

| 元件 | 用途 | 官方資訊 |
| --- | --- | --- |
| OpenJFX | 桌面介面與 Canvas 繪圖 | [OpenJFX](https://openjfx.io/) |
| Apache Maven / Maven Wrapper | 建置與啟動 | [Maven Wrapper](https://maven.apache.org/tools/wrapper/) |
| JUnit 5 | 自動化測試 | [JUnit](https://junit.org/junit5/) |

官方產生的 `mvnw` 與 `mvnw.cmd` 保留 Apache 授權標頭。
JavaFX、Maven 與 JUnit 相依檔案由 Maven 下載，未將它們的執行檔納入原始碼儲存庫。
