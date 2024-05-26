package com.example.demo.mapper;

import com.example.demo.entity.Docinfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface DocinfoRepository extends ElasticsearchRepository<Docinfo, Integer> {
    //文件上传

}
