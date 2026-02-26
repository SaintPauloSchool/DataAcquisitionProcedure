package sp.dataacquisitionprocedure.service;

import sp.dataacquisitionprocedure.entity.ClassLog;
import sp.dataacquisitionprocedure.mapper.ClassLogMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CsvImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);
    
    @Autowired
    private ClassLogMapper classLogMapper;
    
    @Value("${csv.upload.path}")
    private String uploadPath;
    
    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CSV_EXTENSION = ".csv";
    private static final String CLASS_SEPARATOR = "_";
    
    /**
     * 手動導入CSV文件
     */
    public String importCsvFilesManually() {
        return processCsvFiles(false);
    }
    
    /**
     * 定時任務導入CSV文件
     */
    public String importCsvFilesScheduled() {
        return processCsvFiles(true);
    }
    
    /**
     * 處理CSV文件的核心方法
     */
    private String processCsvFiles(boolean isScheduled) {
        StringBuilder result = new StringBuilder();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // 清空現有數據
        clearExistingData();
        
        try {
            Path path = Paths.get(uploadPath);
            
            // 確保上傳目錄存在
            ensureUploadDirectoryExists(path, result);
            
            // 獲取所有CSV文件
            File[] csvFiles = getCsvFiles(uploadPath);
            
            if (csvFiles == null || csvFiles.length == 0) {
                return result.append("沒有找到CSV文件\n").toString();
            }
            
            result.append(String.format("找到 %d 個CSV文件\n", csvFiles.length));
            
            // 逐個處理CSV文件
            processCsvFileList(csvFiles, result, successCount, errorCount);
            
            appendProcessingSummary(result, successCount.get(), errorCount.get());
            
        } catch (Exception e) {
            result.append("處理過程中發生錯誤: ").append(e.getMessage());
            logger.error("CSV文件處理失敗", e);
        }
        
        return result.toString();
    }
    
    /**
     * 處理單個CSV文件
     */
    private void processSingleCsvFile(File csvFile, AtomicInteger successCount, AtomicInteger errorCount) throws IOException {
        String fileName = csvFile.getName();
        
        // 從文件名提取班級信息
        String studentClass = extractStudentClass(fileName);
        if (studentClass == null) {
            throw new IllegalArgumentException("無法從文件名提取班級信息: " + fileName);
        }
        
        logger.info("開始處理文件: {}，班級: {}", fileName, studentClass);
        
        // 讀取CSV文件
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(csvFile), StandardCharsets.UTF_8);
             CSVParser csvParser = createCsvParser(reader)) {
            
            // 處理每一行數據
            processCsvRecords(csvParser, fileName, studentClass, successCount, errorCount);
        }
    }
    
    /**
     * 從文件名提取班級信息
     */
    private String extractStudentClass(String fileName) {
        int separatorIndex = fileName.indexOf(CLASS_SEPARATOR);
        return separatorIndex > 0 ? fileName.substring(0, separatorIndex) : null;
    }
    
    /**
     * 解析CSV記錄並創建ClassLog對象
     */
    private ClassLog parseCsvRecord(CSVRecord record, String studentClass) {
        try {
            // CSV列索引對應關係（從0開始計數）：
            // 0: ID (不需要)
            // 1: 標題 (content)
            // 2: 科目及老師 (需要分割)
            // 3: 類別 (courseType)
            // 4: 開始時間 (startDate)
            // 5: 結束時間 (endDate)
            // 6: 內容類型 (不需要)
            // 7: 附件 (不需要)
            
            // 獲取標題作為content
            String content = record.get(1); // 第二列（索引1）作為content
            
            // 第三列格式：根據中間空格分割科目和老師
            String courseTeacherStr = record.get(2);
            String[] courseTeacherParts = courseTeacherStr.split("\\s+", 2);
            String course = courseTeacherParts.length > 0 ? courseTeacherParts[0] : "";
            String teacher = courseTeacherParts.length > 1 ? courseTeacherParts[1] : "";
            
            String courseType = record.get(3); // 第四列（索引3）作為courseType
            String startDateStr = record.get(4); // 第五列（索引4）作為startDate
            String endDateStr = record.get(5); // 第六列（索引5）作為endDate
            
            // 生成ID：班級 + 日期 + 隨機6位數
            String id = generateRecordId(studentClass);
            
            // 保持日期字符串格式（不再轉換為LocalDateTime）
            return new ClassLog(id, studentClass, teacher, course, courseType, content, startDateStr, endDateStr);
            
        } catch (Exception e) {
            logger.error("解析CSV記錄失敗: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 獲取當前日期字符串
     */
    private String getCurrentDateStr() {
        return LocalDateTime.now().format(dateFormatter);
    }
    
    // === 私有輔助方法 ===
    
    /**
     * 清空現有數據
     */
    private void clearExistingData() {
        logger.info("開始清空class_log表中所有現有數據");
        classLogMapper.deleteAll();
        // TODO: 需要修復 MyBatis XML 映射問題
    }
    
    /**
     * 確保上傳目錄存在
     */
    private void ensureUploadDirectoryExists(Path path, StringBuilder result) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            result.append("創建目錄: ").append(uploadPath).append("\n");
        }
    }
    
    /**
     * 獲取CSV文件列表
     */
    private File[] getCsvFiles(String uploadPath) {
        File directory = new File(uploadPath);
        return directory.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(CSV_EXTENSION));
    }
    
    /**
     * 處理CSV文件列表
     */
    private void processCsvFileList(File[] csvFiles, StringBuilder result, 
                                   AtomicInteger successCount, AtomicInteger errorCount) {
        for (int i = 0; i < csvFiles.length; i++) {
            File csvFile = csvFiles[i];
            logger.info("開始處理第 {}/{} 個文件: {}", i + 1, csvFiles.length, csvFile.getName());
            
            try {
                processSingleCsvFile(csvFile, successCount, errorCount);
                result.append("✓ 處理文件完成: ").append(csvFile.getName()).append("\n");
                logger.info("第 {}/{} 個文件處理完成: {}", i + 1, csvFiles.length, csvFile.getName());
            } catch (Exception e) {
                errorCount.incrementAndGet();
                result.append("✗ 處理文件失敗: ").append(csvFile.getName())
                      .append(" - 錯誤: ").append(e.getMessage()).append("\n");
                logger.error("處理文件 {} 時發生錯誤", csvFile.getName(), e);
            }
            
            // 添加文件間隔提示
            if (i < csvFiles.length - 1) {
                result.append("--- 準備處理下一個文件 ---\n");
            }
        }
    }
    
    /**
     * 附加處理摘要
     */
    private void appendProcessingSummary(StringBuilder result, int successCount, int errorCount) {
        result.append("\n處理完成!\n")
              .append(String.format("成功處理: %d 條記錄\n", successCount))
              .append(String.format("處理失敗: %d 條記錄\n", errorCount));
    }
    
    /**
     * 創建CSV解析器
     */
    private CSVParser createCsvParser(InputStreamReader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)  // 跳過標題行，從第二行開始讀取數據
                .build()
                .parse(reader);
    }
    
    /**
     * 處理CSV記錄
     */
    private void processCsvRecords(CSVParser csvParser, String fileName, String studentClass,
                                  AtomicInteger successCount, AtomicInteger errorCount) throws IOException {
        int recordCount = 0;

        for (CSVRecord record : csvParser) {

            recordCount++;
            try {
                ClassLog classLog = parseCsvRecord(record, studentClass);
                if (classLog != null) {
                    classLogMapper.insert(classLog);
                    successCount.incrementAndGet();
                    logger.debug("已添加記錄 - 文件: {}, 班級: {}, 記錄編號: {}, ID: {}", 
                               fileName, studentClass, recordCount, classLog.getId());
                } else {
                    errorCount.incrementAndGet();
                    logger.warn("解析記錄失敗 - 文件: {}, 班級: {}, 記錄編號: {}", 
                              fileName, studentClass, recordCount);
                }
            } catch (Exception e) {
                errorCount.incrementAndGet();
                logger.error("處理記錄時發生錯誤 - 文件: {}, 班級: {}, 記錄編號: {}, 錯誤: {}", 
                           fileName, studentClass, recordCount, e.getMessage());
            }
        }
        logger.info("文件 {} 處理完成，總記錄數: {}，成功: {}，失敗: {}", 
                   fileName, recordCount, successCount.get(), errorCount.get());
    }
    
    /**
     * 生成記錄ID
     */
    private String generateRecordId(String studentClass) {
        String dateStr = getCurrentDateStr();
        String randomNum = String.format("%06d", random.nextInt(1000000));
        return studentClass + dateStr + randomNum;
    }
}