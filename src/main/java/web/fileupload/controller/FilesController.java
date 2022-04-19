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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:8081")
public class FilesController {
    @Autowired
    FilesStorageService filesStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message="";
        HttpStatus httpStatus=null;
        try {
            filesStorageService.save(file);
            message="檔案： "+file.getOriginalFilename()+" 上傳成功！";
            httpStatus=HttpStatus.OK;
        } catch (Exception e) {
            message="檔案: "+file.getOriginalFilename()+" 上傳失敗！";
            httpStatus=HttpStatus.EXPECTATION_FAILED;
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
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

}
