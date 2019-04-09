package com.tech2020.fwupdater.model;

public class DeviceInfo {
    private String DEVICE_TYPE;
    private String DEVICE_MANUFACTURER;
    private String DEVICE_MODEL;
    private String DEVICE_HW_VER;
    private String FIRMWARE_VERSION;
    private String STATION_MAC_ADDRESS;
    private String SOFTAP_MAC_ADDRESS;
    private String CONTROLLER_IP;


    public DeviceInfo(String DEVICE_TYPE, String DEVICE_MANUFACTURER, String DEVICE_MODEL, String DEVICE_HW_VER, String FIRMWARE_VERSION, String STATION_MAC_ADDRESS, String SOFTAP_MAC_ADDRESS, String CONTROLLER_IP) {
        this.DEVICE_TYPE = DEVICE_TYPE;
        this.DEVICE_MANUFACTURER = DEVICE_MANUFACTURER;
        this.DEVICE_MODEL = DEVICE_MODEL;
        this.DEVICE_HW_VER = DEVICE_HW_VER;
        this.FIRMWARE_VERSION = FIRMWARE_VERSION;
        this.STATION_MAC_ADDRESS = STATION_MAC_ADDRESS;
        this.SOFTAP_MAC_ADDRESS = SOFTAP_MAC_ADDRESS;
        this.CONTROLLER_IP = CONTROLLER_IP;
    }

    public String getDEVICE_TYPE() {
        return DEVICE_TYPE;
    }

    public String getDEVICE_MANUFACTURER() {
        return DEVICE_MANUFACTURER;
    }

    public String getDEVICE_MODEL() {
        return DEVICE_MODEL;
    }

    public String getDEVICE_HW_VER() {
        return DEVICE_HW_VER;
    }

    public String getFIRMWARE_VERSION() {
        return FIRMWARE_VERSION;
    }

    public String getSTATION_MAC_ADDRESS() {
        return STATION_MAC_ADDRESS;
    }

    public String getSOFTAP_MAC_ADDRESS() {
        return SOFTAP_MAC_ADDRESS;
    }

    public String getCONTROLLER_IP() {
        return CONTROLLER_IP;
    }
}
