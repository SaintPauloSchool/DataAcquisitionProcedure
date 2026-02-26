package sp.dataacquisitionprocedure.mapper;

import sp.dataacquisitionprocedure.entity.ClassLog;
import org.apache.ibatis.annotations.*;

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