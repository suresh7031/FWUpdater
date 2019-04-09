package com.tech2020.fwupdater.model;

public class FirmwareDownloadStatus {
    private String device_type;
    private String device_model;
    private String firmware_version;
    private Boolean firmware_downloaded;

    public String getDevice_type() {
        return device_type;
    }

    public String getDevice_model() {
        return device_model;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public Boolean getFirmware_downloaded() {
        return firmware_downloaded;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public void setFirmware_downloaded(Boolean firmware_downloaded) {
        this.firmware_downloaded = firmware_downloaded;
    }

}
