package web.fileupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import web.fileupload.dao.FileDAO;
import web.fileupload.entity.WebFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    @Autowired
    private FileDAO fileDAO;
    private final Path root=Paths.get("Storage");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public List<String> save(String Module, String UUID, MultipartFile[] files) {
        boolean hasPath=true;
        List<WebFile> webFileList=new ArrayList<>();
        List<String> resultList=new ArrayList<>();
        Path filepath;
        try {
            filepath=this.root.resolve(Paths.get(Module, UUID));
            Files.createDirectories(filepath);
        } catch (Exception e) {
            hasPath=false;
            throw new RuntimeException("建立路徑異常");
        }

        if (hasPath) {
            resultList=Arrays.asList(files).stream().map(file -> {
                String Fileuid=java.util.UUID.randomUUID().toString();
                try {
                    WebFile webFile=new WebFile();
                    Files.copy(file.getInputStream(), filepath.resolve(Fileuid));
                    webFile.setFModule(Module);
                    webFile.setFFileName(file.getOriginalFilename());
                    webFile.setFFromUUID(UUID);
                    webFile.setFFileUUID(Fileuid);
                    webFile.setFSize(Integer.parseInt(file.getSize()+""));
                    webFileList.add(webFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return file.getOriginalFilename();
            }).collect(Collectors.toList());
            if (webFileList.size()>0) {
                fileDAO.saveToDatabase(webFileList);
            }
        }
        return resultList;
    }

    @Override
    public Resource load(String filename) {
        try {
            Path tmp=Paths.get("Storage//aaa//bbb");
            Path file=tmp.resolve(filename);    //兜成一個有檔案名稱的完整路徑
            //Path file=root.resolve(filename);
            System.out.println("檔案路徑 > "+file);
            Resource resource=new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        //FileSystemUtils.deleteRecursively(root.toFile());
        FileSystemUtils.deleteRecursively(Paths.get("uploads").toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(Paths.get("Storage//aaa//bbb"), 1)
                    .filter(path -> !path.equals(Paths.get("Storage//aaa//bbb")) )
                    .map(this.root::relativize);    //這邊會把Storage這個根目錄給去掉，但目前看來，不需要去掉也沒關係，因為單純只需要檔名而已
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
