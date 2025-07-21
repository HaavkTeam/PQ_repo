package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface fileMapper {
    @Insert("INSERT INTO filetable VALUES (#{fileId}, #{format}, #{content},#{speechId})")
    boolean insertFile(String fileId, String format, String content, String speechId);
}
