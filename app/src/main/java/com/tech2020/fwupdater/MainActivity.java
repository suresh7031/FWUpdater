package com.tech2020.fwupdater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tech2020.fwupdater.model.FirmwareDownloadClient;
import com.tech2020.fwupdater.viewmodel.GuideViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.lifecycle.ViewModelProviders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public final String TAG="main";

    Button btn_download;
    Button btn_wifi_setting;
    Button btn_proceed;
    ProgressBar pb;
    TextView info_text;
    private GuideViewModel mViewModel;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel= ViewModelProviders.of(this).get(GuideViewModel.class);

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
                downloadFile();

                // on some click or some loading we need to wait for...
                pb = (ProgressBar) findViewById(R.id.pbLoading);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pb.setVisibility(ProgressBar.VISIBLE);

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

    private void downloadFile(String device_type, int bin) {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.0.108/homeautomation/esp_ota/firmwares/")
                .build();

        FirmwareDownloadClient firmwareDownloadClient=retrofit.create(FirmwareDownloadClient.class);

        firmwareDownloadClient.downloadFirmWare(device_type, bin).enqueue(new Callback<ResponseBody>() {
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

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File firmwareFile = new File(getExternalFilesDir(null) + File.separator + "user1.bin");
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
