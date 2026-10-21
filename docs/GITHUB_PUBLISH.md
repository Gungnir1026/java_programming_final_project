# GitHub 作品展示

## 儲存庫建議

- 名稱：`java-trash-sorting-game`
- About：`分類小達人｜JavaFX 垃圾分類遊戲，結合 SDGs、生態教育、即時動畫、升級與存檔系統。Java 程式設計期末專題。`
- Topics：`java`、`javafx`、`game`、`maven`、`education`、`sdgs`、`student-project`
- README 已包含實際截圖、操作方法、設計說明及執行方式。

## 發布為獨立儲存庫

1. 在 GitHub 建立空白儲存庫；先不要自動新增 README、`.gitignore` 或授權檔。
2. 開啟終端機，執行下列指令。把網址中的 `YOUR_USERNAME` 換成自己的帳號；若已建立其他名稱的空白儲存庫，請直接使用該 repo 的 HTTPS 網址。

```bash
cd "/path/to/java_final-main"
git init -b main
git add .
git update-index --chmod=+x mvnw run.sh
git status --short
git commit -m "Prepare JavaFX trash sorting game for portfolio"
git remote add origin https://github.com/YOUR_USERNAME/java-trash-sorting-game.git
git push -u origin main
```

第一行的 `/path/to/java_final-main` 請換成自己電腦上的專案路徑。
`git update-index` 確保兩個 shell 腳本在 Git 中保有執行權限。
`git status --short` 用來確認要提交的內容；本專案的 `.gitignore` 會排除原始備份、建置快取、編譯產物及本機存檔。
應保留隱藏資料夾 `.github/`、`.mvn/` 及 `mvnw` 的執行權限。
上述指令用於本機尚未初始化、遠端為空的情況；遠端已有內容時，先 clone 遠端，再把整理後專案放入該工作目錄並正常提交。

若要放進既有的作品集儲存庫，可把專案放在例如 `java-trash-sorting-game/` 子目錄，並在作品集首頁連結到本專案的 README。
GitHub Actions 只有放在儲存庫根目錄的 `.github/workflows/` 才會執行；放在子目錄時需調整工作流程的 `working-directory` 與快取依據。

## 之後更新作品

在專案目錄執行：

```bash
git add .
git diff --cached --stat
git commit -m "Update game and documentation"
git push
```

如果第一次提交出現 `Author identity unknown`，先設定自己的 Git 作者姓名與 email，再重試提交。
若 HTTPS 登入要求驗證，請使用 GitHub 的瀏覽器登入流程或 personal access token。

## 發布後確認

- 首頁 README 的三張截圖可正常顯示。
- Java CI 工作流程有成功執行；若尚未 push，不能把本機通過當作遠端 CI 已通過。
- 在乾淨目錄 clone 一次，以 JDK 21 執行 `./mvnw verify` 與啟動指令。
- 設定 About 與 Topics；若有學校、組員或原始素材來源，可依實際資訊補上。

## 可用的作品介紹

> 分類小達人是一款以 SDGs 14、15 為主題的 JavaFX 桌面遊戲。玩家需移動垃圾桶並切換四種分類，接住隨機掉落的垃圾；分類錯誤時顯示學習提示，正確分類則可累積金幣升級。本專題使用物件導向設計、Canvas 繪圖與 AnimationTimer，並在整理版加入 Maven 跨平台建置、自動存檔防護及 JUnit 測試。

本次整理尚未建立或推送任何遠端 GitHub 儲存庫。
