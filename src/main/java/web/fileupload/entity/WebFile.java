package web.fileupload.entity;

public class WebFile {
    private Integer FRecId;
    private String FModule;
    private String FFromUUID;
    private String FFileName;
    private String FFileUUID;
    private Integer FSize;

    public Integer getFRecId() {
        return FRecId;
    }

    public void setFRecId(Integer FRecId) {
        this.FRecId=FRecId;
    }

    public String getFModule() {
        return FModule;
    }

    public void setFModule(String FModule) {
        this.FModule=FModule;
    }

    public String getFFromUUID() {
        return FFromUUID;
    }

    public void setFFromUUID(String FFromUUID) {
        this.FFromUUID=FFromUUID;
    }

    public String getFFileName() {
        return FFileName;
    }

    public void setFFileName(String FFileName) {
        this.FFileName=FFileName;
    }

    public String getFFileUUID() {
        return FFileUUID;
    }

    public void setFFileUUID(String FFileUUID) {
        this.FFileUUID=FFileUUID;
    }

    public Integer getFSize() {
        return FSize;
    }

    public void setFSize(Integer FSize) {
        this.FSize=FSize;
    }
}
