package sp.dataacquisitionprocedure.scheduler;

import sp.dataacquisitionprocedure.service.CsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CsvImportScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvImportScheduler.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private CsvImportService csvImportService;
    
    /**
     * 定時任務：週一到週五晚上17點30分執行
     * cron表達式：秒
     * 分 時 日 月 週
     * 0 30 17 * * MON-FRI 表示週一到週五的17:30:00執行
     */
    @Scheduled(cron = "0 30 17 * * MON-FRI")
    public void scheduledCsvImport() {
        try {
            String currentTime = LocalDateTime.now().format(TIME_FORMATTER);
            logger.info("開始執行定時CSV文件導入任務 - {}", currentTime);
            
            String result = csvImportService.importCsvFilesScheduled();
            
            logger.info("定時CSV文件導入任務執行完成 - {}\n{}", currentTime, result);
            
        } catch (Exception e) {
            logger.error("定時CSV文件導入任務執行失敗", e);
        }
    }
    
}