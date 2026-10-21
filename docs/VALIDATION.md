# 本機驗證紀錄

驗證日期：2026-10-21。

同日再次檢查並重新執行完整測試：13 項通過，0 失敗、0 錯誤、0 略過。
將根目錄及圖片目錄的 `.DS_Store` 移至 `.archive/system-metadata/`，保留於本機、不納入上傳。

## 環境與範圍

- macOS 15.7.9、Apple Silicon（arm64）。
- 本機 Java 為 Eclipse Temurin 26.0.1，使用 `--release 21` 編譯；專案文件與 CI 以 JDK 21 為基準。
- Maven 3.9.9、Maven Wrapper 3.3.4、JavaFX 21.0.7。
- Windows 與 Linux 工作流程已設定，尚未在遠端 GitHub Actions 實際執行。

## 結果

| 驗證項目 | 結果 |
| --- | --- |
| Maven 編譯與 JAR 打包 | 通過 |
| 存檔與升級測試 | 9 項通過 |
| 移動、掉落與碰撞測試 | 3 項通過 |
| JavaFX 畫面與操作測試 | 1 項通過；共 13 項，無失敗 |
| Maven Wrapper 首次下載與 SHA-256 校驗 | 通過 |
| `javafx:run` 實際啟動 | 成功顯示遊戲視窗，驗證後關閉 |
| 41 張原圖片與原存檔的 SHA-256 | 全部與整理前一致 |
| JAR 內的圖片 | 41 張均已打包，無 `.DS_Store` |
| README 本機連結與三張截圖 | 檔案均存在，已檢視實際畫面 |
| Git 待上傳檔案預覽 | 排除 `.archive/`、`.build-cache/`、`target/` 與系統檔 |

畫面測試實際載入全部 41 張圖片，驗證主選單 → 分類提示 → 遊戲 → 商店 → 主選單、
商店不會在進入時直接扣款、按住按鍵只購買一次、重複購買與金幣不足、寫入後重新載入進度，
並模擬漏接至遊戲結束。測試使用暫存存檔，不接觸使用者的遊戲進度。

本機 JDK 26 會顯示 JavaFX／Maven 的 native access 與舊 API 警告，但未造成測試或啟動失敗。
本紀錄不代表已在所有平台完成執行測試。

## 重現

```bash
./mvnw verify
./mvnw verify -Dui.tests=true
./mvnw javafx:run
```

一般 `verify` 會略過需要圖形環境的畫面測試；完整測試需加上 `-Dui.tests=true`。
自動產生的報告與畫面位於 `target/surefire-reports/` 及 `target/ui-screenshots/`，不納入 Git。
