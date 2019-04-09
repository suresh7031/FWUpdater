package com.tech2020.fwupdater;

import androidx.appcompat.app.AppCompatActivity;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tech2020.fwupdater.model.DeviceInfo;
import com.tech2020.fwupdater.model.FirmwareDownloadClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FirmwareUpgradeActivity extends AppCompatActivity {
    public final String TAG="FW";

    public class AppServer extends NanoHTTPD{
        public AppServer() throws IOException {
            super(8080);
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.i(TAG, "AppServer: ");
        }

        @Override
        public Response serve(IHTTPSession session) {
            String device_type=null;
            String firmware_version=null;
            String device_model=null;

            String queryParameterString= session.getQueryParameterString();
            Log.i(TAG, "serve: gueryparamterString"+queryParameterString);
            String r_ipaddr=session.getRemoteIpAddress();
            String uri=session.getUri();
            Log.i(TAG, "serve: ipaddr:"+r_ipaddr+" uri:"+uri);

            //parse querystring below




            File file=null;
            FileInputStream fileInputStream=null;
            if(device_type.equals("WIFI_SOCKET")) {
                file = new File(getExternalFilesDir(null) + File.separator +"wifi_socket"+File.separator+ "user1.bin");
            }else if(device_model.equals("WIFI_DIMMER")){
                file = new File(getExternalFilesDir(null) + File.separator +"wifi_dimmer"+File.separator+ "user1.bin");
            }else if(device_model.equals("WIFI_PIR")){

            }
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Response res = newChunkedResponse(NanoHTTPD.Response.Status.OK,"application/octet-stream", fileInputStream);
            res.addHeader("Content-Disposition", "attachment; filename=\""+file.getName()+"\"");
            return res;
           // return super.serve(session);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_upgrade);


        TextView device_type=findViewById(R.id.devicetype_value);
        TextView device_manufacturer=findViewById(R.id.devicemanufacturer_value);
        TextView device_model=findViewById(R.id.devicemodel_value);
        TextView device_fw_version=findViewById(R.id.devicefw_value);


        try {
            new AppServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //getSupportActionBar().setTitle("Device Info");

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.4.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FirmwareDownloadClient firmwareDownloadClient=retrofit.create(FirmwareDownloadClient.class);
        firmwareDownloadClient.getInfo().enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, retrofit2.Response<DeviceInfo> response) {
                 if(response.isSuccessful()){
                     DeviceInfo deviceInfo=response.body();
                     Log.i(TAG, "onResponse: deviceinfo: "+deviceInfo.getDEVICE_TYPE());
                    device_type.setText(deviceInfo.getDEVICE_TYPE());
                    device_manufacturer.setText(deviceInfo.getDEVICE_MANUFACTURER());
                    device_model.setText(deviceInfo.getDEVICE_MODEL());
                    device_fw_version.setText(deviceInfo.getFIRMWARE_VERSION());
                 }
            }

            @Override
            public void onFailure(Call<DeviceInfo> call, Throwable t) {
                Log.i(TAG, "onResponse: deviceinfo: "+t.getMessage());

            }
        });



    }

}
