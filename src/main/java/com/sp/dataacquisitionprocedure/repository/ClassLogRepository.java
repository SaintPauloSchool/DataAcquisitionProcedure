package com.sp.dataacquisitionprocedure.repository;

import com.sp.dataacquisitionprocedure.entity.ClassLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassLogRepository extends JpaRepository<ClassLog, String> {
    // 可以添加自定義查詢方法
}