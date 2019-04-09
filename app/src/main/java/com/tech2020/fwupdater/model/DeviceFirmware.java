package com.tech2020.fwupdater.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_firmware")
public class DeviceFirmware {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String DEVICE_TYPE;
    private String DEVICE_MANUFACTURER;
    private String DEVICE_MODEL;
    private String DEVICE_HW_VER;
    private String FIRMWARE_VERSION;
    private String FW_USER1_URL;
    private String FW_USER2_URL;

    public void setId(int id) {
        this.id = id;
    }

    public DeviceFirmware(String DEVICE_TYPE, String DEVICE_MANUFACTURER, String DEVICE_MODEL, String DEVICE_HW_VER, String FIRMWARE_VERSION, String FW_USER1_URL, String FW_USER2_URL) {
        this.DEVICE_TYPE = DEVICE_TYPE;
        this.DEVICE_MANUFACTURER = DEVICE_MANUFACTURER;
        this.DEVICE_MODEL = DEVICE_MODEL;
        this.DEVICE_HW_VER = DEVICE_HW_VER;
        this.FIRMWARE_VERSION = FIRMWARE_VERSION;
        this.FW_USER1_URL = FW_USER1_URL;
        this.FW_USER2_URL = FW_USER2_URL;
    }

    public int getId() {
        return id;
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

    public String getFW_USER1_URL() {
        return FW_USER1_URL;
    }

    public String getFW_USER2_URL() {
        return FW_USER2_URL;
    }
}
