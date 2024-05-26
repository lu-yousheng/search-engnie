package com.example.demo.service;
import com.example.demo.entity.vo.Files_Group;
import com.example.demo.mapper.DocMapper;
import com.example.demo.mapper.Files_GroupMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

import com.example.demo.entity.Docinfo;
import com.example.demo.mapper.DocinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class FileService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private DocinfoRepository docinfoRepository;
    @Resource
    private Files_GroupMapper filesGroupMapper;
    @Resource
    private DocMapper docMapper;

    //上传文件-es
    public void uploadFile(Docinfo docinfo) {
        // 保存文件信息到Elasticsearch
        docinfoRepository.save(docinfo);//上传文件
    }

    //上传群组表mysql
    public int upload(Docinfo docinfo){
        return docMapper.uploadFile(docinfo);
    }
    public List<Docinfo> selectFiles(int group_id){
        return filesGroupMapper.selectFiles(group_id);
    }
    //上传关联表
    public int saveUpLoad(Files_Group files_group){
        return docMapper.saveUpLoad(files_group);
    }
    //从数据库拿到文件信息类
    public Docinfo getFileInfo(String fileName){
        return docMapper.getFileInfo(fileName);
    }

    //
    public String getFileDesc(int id){
        return docMapper.getFileDesc(id);
    }

    public SearchResponse searchDocumentsByKeyword(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest("new_docinfo_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 创建一个布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加关键字匹配查询
        boolQuery.must(QueryBuilders.matchQuery("docexplain", keyword));

        // 添加群组字段为“无分组”的精确匹配条件
        boolQuery.must(QueryBuilders.termQuery("docgroup", "无分组"));

        // 将布尔查询设置为源构建器的查询
        sourceBuilder.query(boolQuery);


        // 创建一个 HighlightBuilder 并指定要高亮的字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("docexplain"); // 指定要高亮的字段

        // 将 HighlightBuilder 添加到 SearchSourceBuilder 中
        sourceBuilder.highlighter(highlightBuilder);

        // 设置 SearchSourceBuilder 到 SearchRequest
        searchRequest.source(sourceBuilder);

        // 发送搜索请求并返回搜索响应
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public SearchResponse searchDocumentsByKeyword1(String keyword,String group_name) throws IOException {
        SearchRequest searchRequest = new SearchRequest("docinfo_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 创建一个布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加关键字匹配查询
        boolQuery.must(QueryBuilders.matchQuery("docexplain", keyword));

        // 添加群组字段为“无分组”的精确匹配条件
        boolQuery.must(QueryBuilders.matchQuery("docgroup", group_name));

        // 将布尔查询设置为源构建器的查询
        sourceBuilder.query(boolQuery);


        // 创建一个 HighlightBuilder 并指定要高亮的字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("docexplain"); // 指定要高亮的字段

        // 将 HighlightBuilder 添加到 SearchSourceBuilder 中
        sourceBuilder.highlighter(highlightBuilder);

        // 设置 SearchSourceBuilder 到 SearchRequest
        searchRequest.source(sourceBuilder);

        // 发送搜索请求并返回搜索响应
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    //删除es数据
    // 根据查询条件删除数据
    public void deleteDocument(String id) throws IOException {
        DeleteRequest request = new DeleteRequest("docinfo_index", "_doc", id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            System.out.println("Document not found");
        } else {
            System.out.println("Document deleted successfully");
        }
    }

    //删除群组文件doc
    public int delete(int doc_id){
        return docMapper.delet(doc_id);
    }

    //删除群组文件关联表
    public int deletrel(int group_id,int doc_id){
        return filesGroupMapper.delet(group_id,doc_id);
    }
}
