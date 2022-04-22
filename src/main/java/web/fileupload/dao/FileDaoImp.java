package web.fileupload.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import web.fileupload.entity.WebFile;

import java.util.*;

@Repository
public class FileDaoImp implements FileDAO {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<WebFile> saveToDatabase(List<WebFile> webFileList) {
        WebFile webFile;
        String SqlStr="";
        MapSqlParameterSource[] mapSqlParameterSources=new MapSqlParameterSource[webFileList.size()];
        SqlStr="insert into WebFile (F_Module, F_FromUUID, F_FileName, F_UUIDName, F_Size, F_RecOrg) values "
                +"(:F_Module, :F_FromUUID, :F_FileName, :F_UUIDName, :F_Size, :F_RecOrg) ";

        for (int i=0; i<webFileList.size(); i++) {
            webFile=webFileList.get(i);
            mapSqlParameterSources[i]=new MapSqlParameterSource();
            mapSqlParameterSources[i].addValue("F_Module", webFile.getFModule());
            mapSqlParameterSources[i].addValue("F_FromUUID", webFile.getFFromUUID());
            mapSqlParameterSources[i].addValue("F_FileName", webFile.getFFileName());
            mapSqlParameterSources[i].addValue("F_UUIDName", webFile.getFUUIDName());
            mapSqlParameterSources[i].addValue("F_Size", webFile.getFSize());
            mapSqlParameterSources[i].addValue("F_RecOrg", 3);
        }
        jdbcTemplate.batchUpdate(SqlStr, mapSqlParameterSources);
        return webFileList;
    }

    @Override
    public List<WebFile> getFileList(String Module, String UUID) {
        String SqlStr="";
        Map<String, Object> map=new HashMap<>();

        SqlStr="select * from WebFile where F_isDelete=0 and F_Module=:F_Module and F_FromUUID=:F_FromUUID order by F_RecId ";
        map.put("F_Module", Module);
        map.put("F_FromUUID", UUID);
        return jdbcTemplate.query(SqlStr, map, (rs, row)->{
            WebFile webFile=new WebFile();
            webFile.setFModule(rs.getString("F_Module"));
            webFile.setFFromUUID(rs.getString("F_FromUUID"));
            webFile.setFFileName(rs.getString("F_FileName"));
            webFile.setFUUIDName(rs.getString("F_UUIDName"));
            webFile.setFSize(rs.getInt("F_Size"));
            return webFile;
        });
    }

    @Override
    public Optional<WebFile> getFile(String UUIDName) {
        String SqlStr="select * from WebFile where F_isDelete=0 and F_UUIDName=N'"+UUIDName+"' ";
        return jdbcTemplate.query(SqlStr, new HashMap<>(), (rs, row)->{
            WebFile webFile=new WebFile();
            webFile.setFModule(rs.getString("F_Module"));
            webFile.setFFromUUID(rs.getString("F_FromUUID"));
            webFile.setFFileName(rs.getString("F_FileName"));
            webFile.setFUUIDName(rs.getString("F_UUIDName"));
            webFile.setFSize(rs.getInt("F_Size"));
            return webFile;
        }).stream().findFirst();
    }

    @Override
    public Optional<WebFile> deleteFile(String UUIDName) {
        Map<String, Object> map=new HashMap<>();
        String SqlStr="";
        WebFile webFile=getFile(UUIDName).orElse(null);
        if (webFile!=null) {
            SqlStr="update WebFile set F_isDelete=1, F_DeleteOrg=2, F_DeleteDate='1911-01-01' where F_UUIDName=:F_UUIDName and F_isDelete=0 ";
            map.put("F_UUIDName", UUIDName);
            jdbcTemplate.update(SqlStr, map);
        }
        return Optional.ofNullable(webFile);
    }
}
