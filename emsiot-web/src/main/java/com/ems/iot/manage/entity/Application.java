package com.ems.iot.manage.entity;

public class Application {
    private String app_name;

    private String app_version;

    private String app_url;

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name == null ? null : app_name.trim();
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version == null ? null : app_version.trim();
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url == null ? null : app_url.trim();
    }
}