package com.example.demo.mapper;

import com.example.demo.entity.Docinfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface Files_GroupMapper {
    public List<Docinfo> selectFiles(int group_id);
    public int delet(int group_id,int doc_id);
}
