package com.karacasoft.apps.getirhackathonentry;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mahmutkaraca on 3/7/17.
 */

public class GetirNetworkManager {

    public static final String METHOD_GET_ELEMENTS = "getirHackathon.getElements";

    private static Handler networkHandler;
    private static HandlerThread networkHandlerThread;

    private static GetirNetworkGenericCallback callback;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public GetirNetworkManager() {

    }

    private static String doRequest(String url, String method, @Nullable String data) throws IOException {
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();


    }

    public static void getElementsAsync(final String data) {
        Runnable getElems = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = doRequest("https://getir-bitaksi-hackathon.herokuapp.com/getElements",
                            "POST", data);
                    callback.onResult(METHOD_GET_ELEMENTS, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        getHandler().post(getElems);

    }

    private static Handler getHandler() {
        if(networkHandler == null) {
            if(networkHandlerThread != null) {
                if(networkHandlerThread.isAlive()) {
                    networkHandlerThread.interrupt();
                }
            }
            networkHandlerThread = new HandlerThread("NETWORK_THREAD");
            networkHandlerThread.start();
            networkHandler = new Handler(networkHandlerThread.getLooper());
        }
        return networkHandler;
    }

    public static void setCallback(GetirNetworkGenericCallback callback) {
        GetirNetworkManager.callback = callback;
    }
}
