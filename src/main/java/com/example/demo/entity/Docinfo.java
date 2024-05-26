package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
@Document(indexName = "docinfo_index")
@Data
public class Docinfo {
    @Id
    private int id;

    @Field(type = FieldType.Text)
    private String docname;

    @Field(type = FieldType.Text)
    private String docuser;

    @Field(type = FieldType.Text)
    private String docgroup;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    @Field(type = FieldType.Text)
    private String docexplain;

    @Field(type = FieldType.Text)
    private String docpath;

    @Field(type = FieldType.Text)
    private String view_name;
}
