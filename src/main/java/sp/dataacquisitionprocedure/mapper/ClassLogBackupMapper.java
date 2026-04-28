package sp.dataacquisitionprocedure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sp.dataacquisitionprocedure.entity.ClassLog;

import java.util.List;

@Mapper
public interface ClassLogBackupMapper {
    
    /**
     * 创建备份表（如果不存在）
     * @param tableName 表名，格式为 class_log_YYYYMMDD
     */
    void createBackupTable(@Param("tableName") String tableName);
    
    /**
     * 批量插入数据到备份表
     * @param tableName 表名
     * @param classLogs 要插入的数据列表
     */
    void batchInsertToBackupTable(@Param("tableName") String tableName, @Param("classLogs") List<ClassLog> classLogs);
    
    /**
     * 查询所有class_log数据
     */
    List<ClassLog> selectAllClassLogs();
}
