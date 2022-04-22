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
import java.util.*;
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
                    webFile.setFUUIDName(Fileuid);
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
    public void deleteAll() {
        //FileSystemUtils.deleteRecursively(root.toFile());
        FileSystemUtils.deleteRecursively(Paths.get("uploads").toFile());
    }

    @Override
    public String deleteFile(String UUIDName) {
        String Msg="檔案刪除失敗，請重新操作！";
        WebFile webFile=fileDAO.deleteFile(UUIDName).orElse(null);
        if (webFile!=null) {
            Path filePath=Paths.get(this.root.toString(), webFile.getFModule(), webFile.getFFromUUID(), webFile.getFUUIDName());
            if (FileSystemUtils.deleteRecursively(filePath.toFile())) { //刪除該路徑下的檔案
                Msg="檔案刪除成功";
            }
        }
        return Msg;
    }

    @Override
    public Stream<Path> loadAll(String Module, String UUID) {
        Map<Path, Path> map=new HashMap<>();
        Path FilePath=Paths.get(this.root.toString(), Module, UUID);
        try {
            List<WebFile> webFileList=fileDAO.getFileList(Module, UUID);
            if (webFileList.size()>0) {
                webFileList.forEach(webFile -> {
                    map.put(Paths.get(webFile.getFUUIDName()), Paths.get(webFile.getFFileName()));
                });
                return Files.walk(FilePath, 1)
                        .filter(path -> map.containsKey(path.getFileName()) )
                        .map(path -> path.resolve(map.get(path.getFileName())) );
            }
            return Stream.empty();
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public Resource load(String UUIDName) {
        try {
            WebFile webFile=fileDAO.getFile(UUIDName).orElse(null);
            if (webFile!=null) {
                Path file=Paths.get(this.root.toString(), webFile.getFModule(), webFile.getFFromUUID(), webFile.getFUUIDName());
                //Path file=root.resolve(filename);
                Resource resource=new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            }
            return null;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }
}
