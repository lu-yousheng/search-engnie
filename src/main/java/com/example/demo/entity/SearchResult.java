package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    private int took;
    private boolean timed_out;
    private Shards shards;
    private Hits hits;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Shards {
        private int total;
        private int successful;
        private int skipped;
        private int failed;

        // Getters and setters
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        private Total total;
        private List<Hit> hits;

        // Getters and setters
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Total {
        private int value;
        private String relation;

        // Getters and setters
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit {
        private int id;
        private String docname;
        private String docuser;
        private String docgroup;
        private Date createtime;
        private Date updatetime;
        private String docexplain;
        private String highlight; // 修正为正确的属性名
        private String docpath;
        // Define properties for individual hit document
    }

//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class Highlight { // 修正为正确的类名
//        private String docexplain; // 修正为正确的属性名
//    }
}
