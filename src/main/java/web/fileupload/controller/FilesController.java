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

    @GetMapping("/files/{Module}/{FromUUID}")
    public ResponseEntity<List<FileInfo>> getFileList(@PathVariable String Module, @PathVariable String FromUUID) {
        List<FileInfo> fileInfoList=filesStorageService.loadAll(Module, FromUUID).map(path -> {
            String fileName=path.getFileName().toString();
            String UUIDName=path.getParent().getFileName().toString();
            String url=MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", UUIDName, fileName).build().toString();
            return new FileInfo(fileName, url);
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(fileInfoList);
    }

    @GetMapping("/download/{UUIDName}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String UUIDName, @PathVariable String filename) {
        Resource file=filesStorageService.load(UUIDName);
        if (file!=null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) )
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletefile/{UUIDName}")
    public ResponseEntity<?> deleteFile(@PathVariable String UUIDName) {
        HttpStatus httpStatus=HttpStatus.OK;
        String Msg=filesStorageService.deleteFile(UUIDName);
        if (Msg.contains("失敗")) {
            httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(new ResponseMessage(Msg));
    }
}
