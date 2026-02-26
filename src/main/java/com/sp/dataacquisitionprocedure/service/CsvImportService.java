package com.sp.dataacquisitionprocedure.service;

import com.sp.dataacquisitionprocedure.entity.ClassLog;
import com.sp.dataacquisitionprocedure.repository.ClassLogRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
    private ClassLogRepository classLogRepository;
    
    @Value("${csv.upload.path:D:/uploadPath}")
    private String uploadPath;
    
    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    
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

        // 先刪除class_log表中所有現有數據
        logger.info("開始刪除class_log表中所有現有數據");
        classLogRepository.deleteAllRecords();
        logger.info("已刪除class_log表中所有數據");
        
        try {
            Path path = Paths.get(uploadPath);
            
            // 檢查目錄是否存在
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                result.append("創建目錄: ").append(uploadPath).append("\n");
            }
            
            // 獲取所有CSV文件
            File directory = new File(uploadPath);
            File[] csvFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            
            if (csvFiles == null || csvFiles.length == 0) {
                result.append("沒有找到CSV文件\n");
                return result.toString();
            }
            
            result.append("找到 ").append(csvFiles.length).append(" 個CSV文件\n");
            
            // 處理每個CSV文件（讀完一個文件然後讀下個文件進行添加）
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
                
                // 如果不是最後一個文件，添加分隔提示
                if (i < csvFiles.length - 1) {
                    result.append("--- 準備處理下一個文件 ---\n");
                }
            }
            
            result.append("\n處理完成!\n");
            result.append("成功處理: ").append(successCount.get()).append(" 條記錄\n");
            result.append("處理失敗: ").append(errorCount.get()).append(" 條記錄\n");
            
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
        
        // 從文件名提取班級信息 (P1A_ClassLog-20260210-105215.csv -> P1A)
        String studentClass = extractStudentClass(fileName);
        if (studentClass == null) {
            throw new IllegalArgumentException("無法從文件名提取班級信息: " + fileName);
        }
        
        logger.info("開始處理文件: {}，班級: {}", fileName, studentClass);
        
        // 讀取CSV文件
        try (CSVParser csvParser = CSVParser.parse(csvFile, java.nio.charset.StandardCharsets.UTF_8, 
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            int recordCount = 0;
            // 處理每一行數據
            for (CSVRecord record : csvParser) {
                recordCount++;
                try {
                    ClassLog classLog = parseCsvRecord(record, studentClass);
                    if (classLog != null) {
                        // 讀完一條記錄立即添加到數據庫
                        classLogRepository.save(classLog);
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
    }
    
    /**
     * 從文件名提取班級信息
     */
    private String extractStudentClass(String fileName) {
        // P1A_ClassLog-20260210-105215.csv -> P1A
        int underscoreIndex = fileName.indexOf('_');
        if (underscoreIndex > 0) {
            return fileName.substring(0, underscoreIndex);
        }
        return null;
    }
    
    /**
     * 解析CSV記錄並創建ClassLog對象
     */
    private ClassLog parseCsvRecord(CSVRecord record, String studentClass) {
        try {
            // 獲取各列數據（跳過第一列，從第二列開始）
            String content = record.get(1); // 第二列作為content
            
            // 第三列格式：根據中間空格分割
            String courseTeacherStr = record.get(2);
            String[] courseTeacherParts = courseTeacherStr.split("\\s+", 2);
            String course = courseTeacherParts.length > 0 ? courseTeacherParts[0] : "";
            String teacher = courseTeacherParts.length > 1 ? courseTeacherParts[1] : "";
            
            String courseType = record.get(3); // 第四列作為courseType
            String startDateStr = record.get(4); // 第五列作為startDate
            String endDateStr = record.get(5); // 第六列作為endDate
            
            // 生成ID：P1A + 日期 + 隨機6位數
            String dateStr = getCurrentDateStr();
            String randomNum = String.format("%06d", random.nextInt(1000000));
            String id = studentClass + dateStr + randomNum;
            
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
    
    /**
     * 獲取上傳路徑
     */
    public String getUploadPath() {
        return uploadPath;
    }
}