package com.example.demo.entity.vo;

import lombok.Data;

import java.util.Date;
@Data
public class Files_Group {
    private int id;
    private int group_id;
    private int doc_id;
    private String access;
    private Date shared_at;
}
