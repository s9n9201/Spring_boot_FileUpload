package web.fileupload.dao;

import web.fileupload.entity.WebFile;

import java.util.List;
import java.util.Optional;

public interface FileDAO {
    public List<WebFile> saveToDatabase(List<WebFile> webFileList);
    public List<WebFile> getFileList(String Module, String UUID);
    public Optional<WebFile> getFile(String UUIDName);
    public Optional<WebFile> deleteFile(String UUIDName);
}
