package com.xcommerce.bigdata.clickevents.api.model;

public class ClickRequest {
    private String deviceId;
    private String clickItem;
    private String session;
    private String current_ts;
    private String region;

    public ClickRequest(String deviceId, String clickItem, String session, String current_ts,String region) {
        this.deviceId = deviceId;
        this.clickItem = clickItem;
        this.session = session;
        this.current_ts = current_ts;
        this.region=region;
    }

    public ClickRequest() {}

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClickItem() {
        return clickItem;
    }

    public void setClickItem(String clickItem) {
        this.clickItem = clickItem;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCurrent_ts() {
        return current_ts;
    }

    public void setCurrent_ts(String current_ts) {
        this.current_ts = current_ts;
    }
    public String getRegion() {return region;}
    public void setRegion(String region) {this.region = region;}
}
