package com.tech2020.fwupdater;

import androidx.appcompat.app.AppCompatActivity;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

import android.os.Bundle;
import android.util.Log;

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
            String queryParameterString= session.getQueryParameterString();
            Log.i(TAG, "serve: gueryparamterString"+queryParameterString);
            String r_ipaddr=session.getRemoteIpAddress();
            String uri=session.getUri();
            Log.i(TAG, "serve: ipaddr:"+r_ipaddr+" uri:"+uri);

            File file=null;
            FileInputStream fileInputStream=null;
            file = new File(getExternalFilesDir(null)+ File.separator + "user1.bin");
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
        try {
            new AppServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
