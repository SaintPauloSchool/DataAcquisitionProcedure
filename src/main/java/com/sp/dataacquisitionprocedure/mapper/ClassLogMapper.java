package com.sp.dataacquisitionprocedure.mapper;

import com.sp.dataacquisitionprocedure.entity.ClassLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassLogMapper {
    
    /**
     * 插入一條記錄
     */
    int insert(ClassLog classLog);

    /**
     * 刪除所有記錄
     */
    int deleteAll();

}