package com.tech2020.fwupdater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tech2020.fwupdater.model.DeviceDao;
import com.tech2020.fwupdater.model.DeviceDatabase;
import com.tech2020.fwupdater.model.DeviceFirmware;
import com.tech2020.fwupdater.model.FirmwareDownloadClient;
import com.tech2020.fwupdater.model.FirmwareDownloadStatus;
import com.tech2020.fwupdater.model.Firmwares;
import com.tech2020.fwupdater.viewmodel.GuideViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public final String TAG="main";

    List<FirmwareDownloadStatus> flist= new ArrayList<FirmwareDownloadStatus>(4);

    Button btn_download;
    Button btn_wifi_setting;
    Button btn_proceed;
    ProgressBar pb;
    TextView info_text;
    private GuideViewModel mViewModel;
    AlertDialog.Builder builder;

    DeviceDatabase db;
    DeviceDao deviceDao;
    int requestcount = 0;

    List<DeviceFirmware> deviceFirmwareList;

    Boolean firmwaresDownloaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mViewModel= ViewModelProviders.of(this).get(GuideViewModel.class);



        db=DeviceDatabase.getInstance(this);
        deviceDao=db.DeviceDao();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                deviceDao.deleteAll();
                return null;
            }
        }.execute();




        getDeviceFirmwares();




        builder = new AlertDialog.Builder(this);

        info_text = findViewById(R.id.id_guide);
        info_text.setText(R.string.id_str_guide_download);
        info_text.setTextColor(getResources().getColor(R.color.colorRed));

        btn_wifi_setting=findViewById(R.id.id_btn_wifi_settings);
        btn_wifi_setting.setEnabled(false);
        btn_proceed = findViewById(R.id.id_btn_proceed);
        btn_proceed.setEnabled(false);

        btn_download = findViewById(R.id.id_btn_download_fw);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadFile();
                getDeviceFirmwares();
                // on some click or some loading we need to wait for...


// run a background job and once complete
                //pb.setVisibility(ProgressBar.INVISIBLE);

            }
        });

        btn_wifi_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        Intent proceedIntent=new Intent(this, FirmwareUpgradeActivity.class);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(proceedIntent);

            }
        });
    }



    public void getDeviceFirmwares(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://automationwebapi.streamtoweb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

     FirmwareDownloadClient firmwareDownloadClient=retrofit.create(FirmwareDownloadClient.class);
     firmwareDownloadClient.getDeviceFirmwares().enqueue(new Callback<Firmwares>() {
         @Override
         public void onResponse(Call<Firmwares> call, Response<Firmwares> response) {
             if(response.isSuccessful()){
                 Log.i(TAG, "onResponse: "+response.code());
                 Firmwares firmwares=  response.body();
                  addToDb(firmwares);
                  downloadFirmwares();
             }
         }

         @Override
         public void onFailure(Call<Firmwares> call, Throwable t) {
             Log.i(TAG, "onFailure: "+t.getMessage());
         }
     });
    }

    public int checkFirmwaresDownloaded(Boolean flag){

        if(flag) {
            requestcount++;
        }else{
            requestcount--;
        }
        if(requestcount==0){
            return 0;
        }
        return requestcount;
    }



    private void downloadFirmwares() {


        // on some click or some loading we need to wait for...
        pb = findViewById(R.id.pbLoading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pb.setVisibility(ProgressBar.VISIBLE);


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.i(TAG, "doInBackground: getDeviceFirmwares");
                deviceFirmwareList =deviceDao.getDeviceFirmwares();
                //final int count = deviceFirmwareList.size();
                //Log.i(TAG, "doInBackground:sizeoflist "+count);
                for(DeviceFirmware deviceFirmware: deviceFirmwareList){
                    Log.i(TAG, "doInBackground: downloadfile");

                    String user1_url=deviceFirmware.getFW_USER1_URL();
                    String user2_url=deviceFirmware.getFW_USER2_URL();
                    String device_type=deviceFirmware.getDEVICE_TYPE();
                    String device_model=deviceFirmware.getDEVICE_MODEL();
                    downloadFile(user1_url,device_type,device_model,1);
                    downloadFile(user2_url,device_type,device_model,2);
                    checkFirmwaresDownloaded(true);
                    checkFirmwaresDownloaded(true);
                }
                return null;
            }
        }.execute();

    }

    private void addToDb(Firmwares firmwares) {
        db=DeviceDatabase.getInstance(this);
        deviceDao=db.DeviceDao();
        for( DeviceFirmware deviceFirmware:firmwares.getFirmwares()){
            new AsyncTask<DeviceFirmware, Void, Void>() {
                @Override
                protected Void doInBackground(DeviceFirmware... deviceFirmwares) {

                    deviceDao.insert(deviceFirmware);
                    return null;
                }
            }.execute(deviceFirmware);

            Log.i(TAG, "addToDb: "+deviceFirmware.getDEVICE_TYPE());
        }
    }

    private void downloadFile(String url, String device_type, String device_model,int bin) {
        String baseUrl=null;
        URL urll=null;
        try
        {
            urll = new URL(url);
            baseUrl = urll.getProtocol() + "://" + urll.getHost();
        }
        catch (MalformedURLException e)
        {
            // do something
        }
        Log.i(TAG, "downloadFile: "+url);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build();

        FirmwareDownloadClient firmwareDownloadClient=retrofit.create(FirmwareDownloadClient.class);

        /*
        firmwareDownloadClient.downloadFirmWare().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: ");
                boolean flag = writeResponseBodyToDisk(response.body());
                Toast.makeText(MainActivity.this, "OnResponse: success?"+flag, Toast.LENGTH_SHORT).show();
                if(flag){
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    info_text.setText(R.string.id_str_guide_downloaded);
                    info_text.setTextColor(getResources().getColor(R.color.colorGreen));
                    btn_download.setEnabled(false);
                    btn_wifi_setting.setEnabled(true);
                    btn_proceed.setEnabled(true);
                }else {
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    builder.setMessage(R.string.id_str_alert_msg)
                            .setTitle(R.string.id_srt_fw_download_err)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.create().show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: ");
                pb.setVisibility(ProgressBar.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(MainActivity.this, "OnFailure", Toast.LENGTH_SHORT).show();
                builder.setMessage(R.string.id_str_alert_msg)
                        .setTitle(R.string.id_srt_fw_download)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                builder.create().show();
            }
        });
        */
        firmwareDownloadClient.downloadFirmware(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: file response");
                if(response.isSuccessful()) {
                    Log.i(TAG, "onResponse: code"+response.code());
                    boolean flag = writeResponseBodyToDisk(response.body(), device_type,device_model ,bin);

                    if(flag){
                        Log.i(TAG, "onResponse: file Downloaded?"+flag);
                            int i=checkFirmwaresDownloaded(false);
                        Log.i(TAG, "onResponse: checkFirmwares count"+i);
                            if(i==0) {
                                pb.setVisibility(ProgressBar.INVISIBLE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                info_text.setText(R.string.id_str_guide_downloaded);
                                info_text.setTextColor(getResources().getColor(R.color.colorGreen));
                                btn_download.setEnabled(false);
                                btn_wifi_setting.setEnabled(true);
                                btn_proceed.setEnabled(true);
                            }
                    }else{
                        pb.setVisibility(ProgressBar.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        builder.setMessage(R.string.id_str_alert_msg)
                                .setTitle(R.string.id_srt_fw_download_err)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.create().show();
                    }
                }else{
                    Log.i(TAG, "onResponse: "+response.code());
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    builder.setMessage(R.string.id_str_alert_msg)
                            .setTitle(R.string.id_srt_fw_download_err)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.create().show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: file response"+t.getMessage());
                pb.setVisibility(ProgressBar.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(MainActivity.this, "OnFailure", Toast.LENGTH_SHORT).show();
                builder.setMessage(R.string.id_str_alert_msg)
                        .setTitle(R.string.id_srt_fw_download)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                builder.create().show();
            }
        });

    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String device_type, String device_model, int bin) {
        Log.i(TAG, "writeResponseBodyToDisk: "+device_type+device_model);
        try {
            // todo change the file location/name according to your needs
            String folderPath = getExternalFilesDir(null)+File.separator+device_type+File.separator+device_model;
            File tFolderPath = new File(folderPath);
            if(!tFolderPath.exists()){
                tFolderPath.mkdirs();
            }
            //tFolderPath.close();
            String Path;
            if(bin==1) {
                Path = folderPath + File.separator + "user1.bin";
            }else{
                Path = folderPath + File.separator + "user2.bin";
            }
            File firmwareFile = new File(Path);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(firmwareFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
