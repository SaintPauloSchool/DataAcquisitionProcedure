# Data Acquisition Procedure

## 📋 項目簡介

這是一個基於 Spring Boot 的 CSV 文件自動導入工具，能夠定時將 CSV 格式的課程資料導入到 MySQL 資料庫中。

### 核心功能
- 🕐 **自動定時執行** - 每週一到週五晚上 17 點 30 分自動執行
- 📂 **批量文件處理** - 自動讀取指定目錄下所有 CSV 文件
- 🔄 **智能數據解析** - 自動解析 CSV 內容並轉換為結構化數據
- 💾 **資料庫整合** - 將解析後的數據寫入 MySQL 資料表
- 📊 **完整日誌記錄** - 詳細的操作日誌和錯誤追蹤

## 🛠️ 系統需求與環境配置

### 執行環境
- **作業系統**：Windows 10/11
- **Java 版本**：Java 11 或更高版本
- **資料庫**：MySQL 8.0+
- **磁碟空間**：至少 100MB 可用空間

### 開發環境（可選）
- **Maven**：3.6+（用於重新編譯）
- **IDE**：IntelliJ IDEA / Eclipse（開發用）

## 📦 快速開始

### 1. 環境檢查
```cmd
# 檢查 Java 版本
java -version

# 檢查 Maven（如需重新編譯）
mvn -v
```

### 2. 程式打包（首次使用或修改後）
```cmd
# 清理並打包程式
mvn clean package -DskipTests
```

### 3. 程式啟動
```cmd
# 方法一：使用啟動腳本（推薦）
雙擊 run.bat

# 方法二：命令列啟動
java -jar target/DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar
```

### 4. 程式關閉
```cmd
# 方法一：在程式視窗按 Ctrl+C

# 方法二：使用工作管理員結束 Java 程序
```

## ⚙️ 系統配置

### 資料庫設定
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_handbook
    username: root
    password: root
```

### CSV 文件路徑
```yaml
csv:
  upload:
    path: \\10.96.48.253\Stu_Dailybook$
```

### 定時任務設定
```
執行時間：每週一到週五 晚上 19:30
cron 表達式：0 30 19 * * MON-FRI
```

## 📁 目錄結構

```
DataAcquisitionProcedure/
├── run.bat                           # 主啟動腳本（推薦）
├── stop.bat                          # 快速關閉腳本（推薦）
├── shutdown.bat                      # 完整關閉腳本
├── start-app.bat                     # 帶環境檢查的啟動腳本
├── src/                             # 原始碼目錄
│   └── main/
│       ├── java/                    # Java 原始碼
│       └── resources/               # 配置檔案
├── target/                          # 編譯輸出目錄
│   └── DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar  # 可執行 JAR
├── \\10.96.48.253\Stu_Dailybook$    # CSV 文件目錄
└── README.md                        # 說明文件
```

## 📋 CSV 文件格式

### 檔名規範
```
[P1A]_ClassLog-[日期]-[時間戳].csv
範例：P1A_ClassLog-20260210-105215.csv
```

### 檔案內容格式
```csv
"ID","標題","科目及老師","類別","開始時間","結束時間","內容類型","附件"
"843","學科及功課注意事項","國文 招敏詩","教學日誌","2025-09-02","2025-09-02","","False"
"844","代數式","代數 潘浩民","教學日誌","2025-09-02","2025-09-02","","False"
```

## 🔄 資料處理流程

1. **程式啟動** → 初始化資料庫連線和定時任務
2. **定時觸發** → 每週一到週五晚上 19:30 自動執行
3. **文件掃描** → 讀取 `\\10.96.48.253\Stu_Dailybook$` 目錄下所有 CSV 文件
4. **數據解析** → 解析 CSV 內容轉換為 ClassLog 對象
5. **數據寫入** → 將解析後的數據寫入資料庫
6. **日誌記錄** → 記錄處理結果和錯誤信息

### 重新編譯程式
```cmd
# 清理並編譯
mvn clean compile

# 打包成可執行 JAR
mvn clean package -DskipTests
```

### 專案結構說明
```
sp.dataacquisitionprocedure
├── DataAcquisitionProcedureApplication.java  # 主程式入口
├── entity/                                   # 實體類
│   └── ClassLog.java                        # 資料表對應實體
├── mapper/                                   # MyBatis Mapper接口
│   └── ClassLogMapper.java                  # 資料操作Mapper
├── service/                                  # 業務邏輯層
│   └── CsvImportService.java                # CSV 處理服務
└── scheduler/                                # 定時任務
    └── CsvImportScheduler.java              # 定時任務調度器
```

## 🚀 部署流程

### 開發環境部署
1. 確保安裝 Java 11+ 和 Maven
2. 克隆或下載專案原始碼
3. 修改 `application.yml` 中的資料庫配置
4. 執行 `mvn clean package -DskipTests` 編譯打包
5. 雙擊 `run.bat` 啟動程式

### 生產環境部署
1. 將 `target/DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar` 複製到目標伺服器
2. 確保目標伺服器安裝 Java 11+
3. 創建啟動腳本或使用指令直接運行
4. 設定 CSV 文件監控目錄 (`\\10.96.48.253\Stu_Dailybook$`)
5. 配置系統服務（可選）以實現開機自啟

### 代碼修改後的重新部署
```cmd
# 1. 修改程式碼後，重新編譯打包
mvn clean package -DskipTests

# 2. 用新的 JAR 文件替換舊的
copy target\DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar 部署目錄\

# 3. 重新啟動程式
java -jar 部署目錄\DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar
```

## 🛡️ 安全與維護

### 生產環境部署建議
1. **安全性配置**
   - 修改預設資料庫帳號密碼
   - 設定 CSV 目錄存取權限
   - 配置適當的防火牆規則

2. **系統維護**
   - 定期備份重要資料
   - 監控程式運行日誌
   - 定期清理過期 CSV 文件
   - 更新 Java 和資料庫版本

3. **效能最佳化**
   - 建議每次處理 CSV 文件不超過 100 個
   - 大量文件建議分批處理
   - 定期清理程式日誌檔案
   - 監控系統資源使用情況

## 📞 技術支援與聯絡

### 需要技術協助時請提供
1. **完整錯誤日誌** - 程式輸出的所有錯誤信息
2. **CSV 文件樣本** - 出現問題的 CSV 文件內容
3. **系統環境資訊** - Java 版本、作業系統、資料庫版本
4. **問題詳細描述** - 問題發生的具體情況和時間

### 問題回報渠道
- 詳細記錄問題現象和重現步驟
- 提供相關的配置文件內容
- 描述期望行為與實際行為的差異

---

**版本**：1.0  
**最後更新**：2026 年 2 月 26 日  
**開發框架**：Spring Boot 2.7.18 + Java 11