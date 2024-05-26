package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.TimeConversionService;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.entity.Docinfo;
import com.example.demo.entity.SearchResult;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Userinfo;
import com.example.demo.entity.vo.DocVo;
import com.example.demo.entity.vo.Files_Group;
import com.example.demo.service.FileParsingService;
import com.example.demo.service.FileService;
import com.example.demo.service.GroupService;
import com.example.demo.service.User_GroupsService;
import org.elasticsearch.action.search.CanMatchNodeRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/files")
public class FileController implements ServletContextAware {
    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    @Resource
    private User_GroupsService user_groupsService;

    @Resource
    private GroupService groupService;
    @Resource
    private FileParsingService fileParsingService;
    @Resource
    private FileService fileService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public AjaxResult uploadFile(
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam("description") String description,
                                 @RequestParam("isPublic") boolean isPublic, HttpServletRequest request) {

        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        //检查用户是否登录
        if (userinfo == null){
            return AjaxResult.fail(-1,"用户未登录");
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            return AjaxResult.fail(-1,"上传文件为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 检查文件类型是否合法
        if (!isValidFileType(originalFilename)) {
            return AjaxResult.fail(-1,"文件类型不合法");
        }
        //
        String type = getFileExtension(originalFilename);
        String explain = null;
        try {
            if (type.equals(".doc") || type.equals(".docx")){
                explain = fileParsingService.parseWord(file);
            } else if (type.equals(".xls") || type.equals(".xlsx")){
                explain = fileParsingService.parseExcel(file);
            } else if (type.equals(".pdf") || type.equals(".pfda") || type.equals(".pdfx") || type.equals(".pdft")
            || type.equals(".pdfm") || type.equals(".pdfe")){
                explain = fileParsingService.parsePDF(file);
            }
        } catch (IOException e) {
        e.printStackTrace();
    }
        // 生成一个唯一的文件名，避免文件名冲突
        String fileName = UUID.randomUUID().toString() + getFileExtension(originalFilename);

        // 创建文件保存路径
        File dest = new File(uploadDir + "/" + fileName);
        try {

            Docinfo docinfo = new Docinfo();
            docinfo.setView_name(originalFilename);
            docinfo.setDocname(fileName);
            docinfo.setDocuser(userinfo.getUsername());//将数据截断
            String explainSql = null;
            docinfo.setDocexplain(description);
            docinfo.setCreatetime(new Date());
            docinfo.setUpdatetime(new Date());
            Path relativePath = null;
            docinfo.setDocpath(Paths.get(uploadDir).relativize(Paths.get(dest.getAbsolutePath())).toString());
            //将文件信息存储到mysql
            fileService.upload(docinfo);
            // 将文件信息保存到Elasticsearch
            Docinfo docinfoSql = fileService.getFileInfo(fileName); //从数据库中拿到存好的信息
            docinfo.setDocgroup("无分组");
            docinfo.setCreatetime(new Date());
            docinfo.setUpdatetime(new Date());
            docinfo.setDocexplain(explain);
            docinfo.setDocname(originalFilename);
            docinfo.setId(docinfoSql.getId());
            fileService.uploadFile(docinfo);
            // 保存文件
            file.transferTo(dest);

            // 返回文件保存路径
            return AjaxResult.success("文件上传成功，保存路径：" + dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.fail(-2,"文件上传失败");
        }

        //
        //return AjaxResult.success("");
    }
    // 检查文件类型是否合法
    private boolean isValidFileType(String fileName) {
        String ext = getFileExtension(fileName);
        // 在这里添加合法的文件类型，如Word文档、Excel文件、HTML文件等
        return ext.equalsIgnoreCase(".doc") || ext.equalsIgnoreCase(".docx") ||
                ext.equalsIgnoreCase(".xls") || ext.equalsIgnoreCase(".xlsx") ||
                ext.equalsIgnoreCase(".html") || ext.equalsIgnoreCase(".txt") ||
                ext.equalsIgnoreCase(".pdf") || ext.equalsIgnoreCase(".pdfa") ||
                ext.equalsIgnoreCase(".pdfx") || ext.equalsIgnoreCase(".pdfm")||
                ext.equalsIgnoreCase(".pdft") || ext.equalsIgnoreCase(".pdfe");
    }

    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    @RequestMapping("/search")
    public AjaxResult searchByContent(String keyword) throws IOException {
        if (keyword == null){
            return AjaxResult.fail(-1,"搜索内容为空");
        }
        // 从搜索响应中提取出需要的数据
        SearchResponse searchResponse = fileService.searchDocumentsByKeyword(keyword);
        SearchResult searchResult = getSouceInEs(searchResponse);//格式化数据
        return AjaxResult.success(searchResult.getHits().getHits());
    }


    //将从es传来的数据转换成searchresult类方便进行操作
    public SearchResult getSouceInEs(SearchResponse searchResponse){
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        // 构建 SearchResult 对象
        SearchResult searchResult = new SearchResult();
        searchResult.setTook((int) searchResponse.getTook().getMillis());
        searchResult.setTimed_out(searchResponse.isTimedOut());

        // 构建 Shard 对象
        // 获取 shard 相关信息
        int totalShards = searchResponse.getTotalShards(); // 获取总 shard 数量
        int successfulShards = searchResponse.getSuccessfulShards(); // 获取成功 shard 数量
        int failedShards = searchResponse.getFailedShards(); // 获取失败 shard 数量
        //List<ShardSearchFailure> shardFailures = searchResponse.getShardFailures(); // 获取失败的 shard 列表

        // 构建 Shard 对象
        SearchResult.Shards shards = new SearchResult.Shards();
        shards.setTotal(totalShards);
        shards.setSuccessful(successfulShards);
        shards.setFailed(failedShards);

        // 设置 SearchResult 对象中的 shards 属性
        searchResult.setShards(shards);

        // 设置 Hits 对象
        SearchResult.Hits resultHits = new SearchResult.Hits();
        SearchResult.Total total = new SearchResult.Total();
        total.setValue((int) hits.getTotalHits().value);
        total.setRelation(hits.getTotalHits().relation.toString());
        resultHits.setTotal(total);

        // 构建 Hit 对象列表
        List<SearchResult.Hit> hitList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            SearchResult.Hit searchHit = new SearchResult.Hit();
            // 设置搜索命中的文档属性
            searchHit.setId(Integer.parseInt(hit.getId()));
            searchHit.setDocname(hit.getSourceAsMap().get("docname").toString());
            searchHit.setDocuser(hit.getSourceAsMap().get("docuser").toString());

            //设置时间格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String localDateTime1 = TimeConversionService.convertUtcToLocal(hit.getSourceAsMap().get("createtime").toString());
            String localDateTime2 = TimeConversionService.convertUtcToLocal(hit.getSourceAsMap().get("updatetime").toString());
            try {
                searchHit.setCreatetime(dateFormat.parse(localDateTime1));
                searchHit.setUpdatetime(dateFormat.parse(localDateTime2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            searchHit.setDocgroup(hit.getSourceAsMap().get("docgroup").toString());
            String docdesc = fileService.getFileDesc(Integer.parseInt(hit.getId()));
            searchHit.setDocexplain(docdesc);
            // 设置高亮字段
            String[] highlights = null;
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("docexplain")) {
                HighlightField highlightField = highlightFields.get("docexplain");
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    if (fragments != null && fragments.length > 0) {
                        StringBuilder highlightStringBuilder = new StringBuilder();
                        for (Text fragment : fragments) {
                            highlightStringBuilder.append(fragment.string());
                        }
                        searchHit.setHighlight(highlightStringBuilder.toString());
                    }
                }
            }
            searchHit.setDocpath(hit.getSourceAsMap().get("docpath").toString());

            hitList.add(searchHit);
        }

        resultHits.setHits(hitList);
        searchResult.setHits(resultHits);
        return searchResult;
    }



    //上传群组文件
    @RequestMapping("/upfilegroups")
    public AjaxResult uploadInGroup( @RequestParam("file") MultipartFile file,
                                     @RequestParam(name = "groupId", required = false) Integer group_id,
                                     @RequestParam("description") String description,HttpServletRequest request){
        if (group_id == null) {
            return AjaxResult.fail(-1,"群组id未显示");
        }
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        //检查用户是否登录
        if (userinfo == null){
            return AjaxResult.fail(-1,"用户未登录");
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            return AjaxResult.fail(-1,"上传文件为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 检查文件类型是否合法
        if (!isValidFileType(originalFilename)) {
            return AjaxResult.fail(-1,"文件类型不合法");
        }
        //
        String type = getFileExtension(originalFilename);
        String explain = null;
        try {
            if (type.equals(".doc") || type.equals(".docx")){
                explain = fileParsingService.parseWord(file);
            } else if (type.equals(".xls") || type.equals(".xlsx")){
                explain = fileParsingService.parseExcel(file);
            } else if (type.equals(".pdf") || type.equals(".pfda") || type.equals(".pdfx") || type.equals(".pdft")
                    || type.equals(".pdfm") || type.equals(".pdfe")){
                explain = fileParsingService.parsePDF(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 生成一个唯一的文件名，避免文件名冲突
        String fileName = UUID.randomUUID().toString() + getFileExtension(originalFilename);

        // 创建文件保存路径
        File dest = new File(uploadDir + "/" + fileName);
        try {


            Docinfo docinfo = new Docinfo();
            docinfo.setView_name(originalFilename);
            docinfo.setDocname(fileName);
            docinfo.setDocgroup((groupService.getNameById(group_id)));
            docinfo.setDocuser(userinfo.getUsername());//将数据截断
            String explainSql = null;
            docinfo.setDocexplain(description);
            docinfo.setCreatetime(new Date());
            docinfo.setUpdatetime(new Date());
            Path relativePath = null;
            docinfo.setDocpath(Paths.get(uploadDir).relativize(Paths.get(dest.getAbsolutePath())).toString());
            //将文件信息存储到mysql
            fileService.upload(docinfo);

            // 将文件信息保存到Elasticsearch
            Docinfo docinfoSql = fileService.getFileInfo(fileName); //从数据库中拿到存好的信息
            docinfo.setCreatetime(new Date());
            docinfo.setUpdatetime(new Date());
            docinfo.setDocexplain(explain);
            docinfo.setDocname(originalFilename);
            docinfo.setId(docinfoSql.getId());

            //上传至关联表
            Files_Group files_group = new Files_Group();
            files_group.setGroup_id(group_id);
            files_group.setDoc_id(docinfoSql.getId());
            System.out.println(docinfoSql.getId());
            fileService.saveUpLoad(files_group);

            fileService.uploadFile(docinfo);//上传es
            // 保存文件
            file.transferTo(dest);

            // 返回文件保存路径
            return AjaxResult.success("文件上传成功，保存路径：" + dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.fail(-2,"文件上传失败");
        }
    }

    //群组文件列表
    @RequestMapping("/showingroup")
    public AjaxResult showGroups(HttpServletRequest request,@RequestParam("group_id") Integer group_id){
        if (group_id == null) {
            return AjaxResult.fail(-1,"群组id未设置");
        }
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        if (userinfo == null) {
            return AjaxResult.fail(-1,"用户未登录");
        }
        List<DocVo> docVoList = new ArrayList<>();
        List<Docinfo> docinfoList = fileService.selectFiles(group_id);
        if (docinfoList == null) {
            return AjaxResult.fail(-1, "当前群组无数据");
        }
        for (Docinfo docinfo : docinfoList) {
            DocVo docVo = new DocVo();
            docVo.setId(docinfo.getId());
            docVo.setDocname(docinfo.getDocname());
            docVo.setDocuser(docinfo.getDocuser());
            docVo.setDocgroup(docinfo.getDocgroup());
            docVo.setCreatetime(docinfo.getCreatetime());
            docVo.setUpdatetime(docinfo.getUpdatetime());
            docVo.setDocexplain(docinfo.getDocexplain());
            docVo.setDocpath(docinfo.getDocpath());
            if (docinfo.getView_name() != null) {
                docVo.setView_name(docinfo.getView_name());
            }
            // 这里根据实际情况设置 isOwner 和 isAdmin 的值
            // 假设 isOwner 和 isAdmin 通过其他逻辑获取，这里直接赋值为 false
            if (docinfo.getDocuser().equals(userinfo.getUsername())){
                docVo.setOwner(true);
            }else {
                docVo.setOwner(false);
            }
            if (user_groupsService.isAdmin(userinfo.getId(),group_id).equals("ADMIN")){
                docVo.setAdmin(true);
            }else {
                docVo.setAdmin(false);
            }
            docVoList.add(docVo);
        }
        return AjaxResult.success(docVoList);
    }

    @RequestMapping("/delete")
    public AjaxResult deleteInGroup(int group_id,int doc_id) throws IOException {
        fileService.deletrel(group_id,doc_id);
        fileService.delete(doc_id);
        fileService.deleteDocument(String.valueOf(doc_id));
        return AjaxResult.success("OK");
    }

    @RequestMapping("/searchingp")
    public AjaxResult searchInGroup(String keyword,int group_id) throws IOException {
        if (keyword == null){
            return AjaxResult.fail(-1,"搜索内容为空");
        }
        // 从搜索响应中提取出需要的数据
        String group_name = groupService.getNameById(group_id);
        SearchResponse searchResponse = fileService.searchDocumentsByKeyword1(keyword,group_name);
        SearchResult searchResult = getSouceInEs(searchResponse);//格式化数据
        return AjaxResult.success(searchResult.getHits().getHits());
    }

    @RequestMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(int doc_id, String doc_path) {
        try {
            // 从指定路径读取文件内容
            Path path = Paths.get("D:\\practice-code\\search-engine\\src\\main\\resources\\static\\fileDownload\\" + doc_path);
            byte[] data = Files.readAllBytes(path);

            // 创建字节数组资源对象
            ByteArrayResource resource = new ByteArrayResource(data);

            // 设置响应头，指示文件下载
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", doc_path);

            // 返回文件资源作为响应实体
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            // 处理文件读取异常
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}
}
