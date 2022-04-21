package web.fileupload.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import web.fileupload.entity.WebFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            mapSqlParameterSources[i].addValue("F_UUIDName", webFile.getFFileUUID());
            mapSqlParameterSources[i].addValue("F_Size", webFile.getFSize());
            mapSqlParameterSources[i].addValue("F_RecOrg", 3);
        }
        jdbcTemplate.batchUpdate(SqlStr, mapSqlParameterSources);
        return webFileList;
    }
}
