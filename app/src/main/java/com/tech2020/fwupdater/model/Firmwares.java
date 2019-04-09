package com.tech2020.fwupdater.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Firmwares {

        @SerializedName("firmwares")
        private List<DeviceFirmware> firmwares;

        public List<DeviceFirmware> getFirmwares() {
            return this.firmwares;
        }
}
