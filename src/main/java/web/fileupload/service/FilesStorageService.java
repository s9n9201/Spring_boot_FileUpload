package web.fileupload.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface FilesStorageService {
    public void init();
    public List<String> save(String Module, String UUID, MultipartFile[] files);
    public Resource load(String filename);
    public void deleteAll();
    public Stream<Path> loadAll();
}
