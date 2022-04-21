package web.fileupload.dao;

import web.fileupload.entity.WebFile;

import java.util.List;

public interface FileDAO {
    public List<WebFile> saveToDatabase(List<WebFile> webFileList);
}
