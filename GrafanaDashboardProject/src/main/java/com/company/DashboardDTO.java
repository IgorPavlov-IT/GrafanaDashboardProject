package com.company;

public class DashboardDTO {
    String uid;
    String title;
    String url;
    String folderName;
    StringBuffer DataSource;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public StringBuffer getDataSource() {
        return DataSource;
    }

    public void setDataSource(StringBuffer dataSource) {
        DataSource = dataSource;
    }
}
