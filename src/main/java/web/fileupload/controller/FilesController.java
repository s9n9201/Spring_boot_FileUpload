package web.fileupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import web.fileupload.message.ResponseMessage;
import web.fileupload.model.FileInfo;
import web.fileupload.service.FilesStorageService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:8081")
public class FilesController {
    @Autowired
    FilesStorageService filesStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam String module, @RequestParam String fromuuid, @RequestParam MultipartFile[] files) {
        boolean NoFile=false;
        String message="";
        HttpStatus httpStatus=null;
        NoFile=Arrays.stream(files).map(file -> file.isEmpty()).collect(Collectors.toList()).contains(true);    //檢查有無檔案，其中一個沒有檔案就return 400
        if (module==null || module.equals("") || fromuuid==null || fromuuid.equals("")) {
            message="上傳資料驗證失敗！";
            httpStatus=HttpStatus.BAD_REQUEST;
        } else if (NoFile) {
            message="請選擇一個檔案！";
            httpStatus=HttpStatus.BAD_REQUEST;
        } else {
            try {
                List<String> result=filesStorageService.save(module, fromuuid, files);
                message=result.size()+" 個檔案上傳成功！";
                httpStatus=HttpStatus.OK;
            } catch (Exception e) {
                message="檔案上傳失敗！";
                httpStatus=HttpStatus.EXPECTATION_FAILED;
            }
        }
        return ResponseEntity.status(httpStatus).body(new ResponseMessage(message));
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfoList=filesStorageService.loadAll().map(path -> {
            String fileName=path.getFileName().toString();
            String url=MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfo(fileName, url);
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(fileInfoList);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file=filesStorageService.load(filename);
        System.out.println("download > "+file);
        System.out.println("download > "+file.getFilename());
        System.out.println("download > "+new String(file.getFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+new String(file.getFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .body(file);
    }

    @GetMapping("/test")
    public String getTest() {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        List<String> filtered = strings.stream().filter(string -> {
            System.out.println("過濾前 > "+string);
            return false;
        }).collect(Collectors.toList());
        System.out.println("長度 > "+filtered.size());
        filtered.stream().forEach(str->{
            System.out.println("過濾後 > "+str);
        });
        return "";
    }
}
