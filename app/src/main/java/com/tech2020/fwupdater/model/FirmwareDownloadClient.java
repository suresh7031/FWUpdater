package com.tech2020.fwupdater.model;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface FirmwareDownloadClient {

    //@GET("")
    //Call<ResponseBody> downloadFirmWare();

    @GET
    Call<ResponseBody> downloadFirmware(@Url String fullUrl);

    @GET("fwtool_get_updates.php")
    Call<Firmwares> getDeviceFirmwares();

    @POST("fwtool_get_updates.php")
    Call<List<DeviceFirmware>> postDeviceFirmwares(@Body DeviceFirmware deviceFirmware);

    @GET("getInfo")
    Call<DeviceInfo> getInfo();

}
