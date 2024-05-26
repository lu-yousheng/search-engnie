package com.example.demo.mapper;

import com.example.demo.entity.Docinfo;
import com.example.demo.entity.vo.Files_Group;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocMapper {
    public int uploadFile(Docinfo docinfo);
    public Docinfo getFileInfo(String fileName);
    public String getFileDesc(int id);
    public int saveUpLoad(Files_Group files_group);
    public int delet(int doc_id);
}
