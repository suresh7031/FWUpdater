package com.tech2020.fwupdater.model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;

public interface FirmwareDownloadClient {

    @GET("user1.bin")
    Call<ResponseBody> downloadFirmWare(@Field(""));
}
