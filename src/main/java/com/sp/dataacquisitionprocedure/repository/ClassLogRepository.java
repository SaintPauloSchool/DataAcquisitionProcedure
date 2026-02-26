package com.sp.dataacquisitionprocedure.repository;

import com.sp.dataacquisitionprocedure.entity.ClassLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClassLogRepository extends JpaRepository<ClassLog, String> {
    
    /**
     * 刪除所有class_log表中的數據
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ClassLog")
    void deleteAllRecords();
    
    // 可以添加其他自定義查詢方法
}