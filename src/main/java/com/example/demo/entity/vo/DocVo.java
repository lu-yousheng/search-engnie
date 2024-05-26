package com.example.demo.entity.vo;

import com.example.demo.entity.Docinfo;
import lombok.Data;

@Data
public class DocVo extends Docinfo {
    private boolean isOwner;
    private boolean isAdmin;
}
