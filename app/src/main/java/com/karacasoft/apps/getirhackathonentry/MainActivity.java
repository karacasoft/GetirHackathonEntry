package com.karacasoft.apps.getirhackathonentry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
    GetirNetworkGenericCallback{

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private int width;
    private int height;

    private ArrayList<Element> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        int error = 0;

        GetirNetworkManager.setCallback(this);

        JSONObject object = new JSONObject();
        try {
            object.put("email", "mahmutkaraca95@gmail.com");
            object.put("name", "Mahmut Karaca");
            object.put("gsm", "+905415364748");
        } catch (JSONException e) {
            e.printStackTrace();
            error--;
        }


        if(error >= 0) {
            GetirNetworkManager.getElementsAsync(object.toString());
        }

    }

    public void viewElements() {
        Canvas c = surfaceHolder.lockCanvas();
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        for (Element e : elements) {
            p.setColor(Color.parseColor("#" + e.getColor()));
            if(e.getType().equals("circle")) {
                c.drawCircle(e.getxPos(), e.getyPos(), e.getRadius(), p);
            } else if(e.getType().equals("rectangle")) {
                c.drawRect(e.getxPos(), e.getyPos(), e.getxPos() + e.getWidth(), e.getyPos() + e.getHeight(), p);
            }
        }
        surfaceHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
    }

    @Override
    public void onResult(String method, String result) {
        Log.d("Getir Network", "Returning result : " + result);
        if(method.equals(GetirNetworkManager.METHOD_GET_ELEMENTS)) {
            try {
                JSONObject response = new JSONObject(result);
                if(response.getInt("code") != 0) {
                    String message = response.getString("msg");
                    Log.e("Getir Network", "ERROR:");
                    Log.e("Getir Network", message);
                } else {
                    elements.clear();
                    JSONArray elemsJSONArray = response.getJSONArray("elements");

                    for (int i = 0; i < elemsJSONArray.length(); i++) {
                        JSONObject elemObject = elemsJSONArray.getJSONObject(i);
                        Element e = new Element();
                        String type = elemObject.getString("type");
                        int xPos = elemObject.getInt("xPosition");
                        int yPos = elemObject.getInt("yPosition");
                        String color = elemObject.getString("color");
                        if(type.equals("circle")) {
                            int r = elemObject.getInt("r");
                            e.setRadius(r);
                        }
                        if(type.equals("rectangle")) {
                            int width = elemObject.getInt("width");
                            int height  = elemObject.getInt("height");
                            e.setWidth(width);
                            e.setHeight(height);
                        }
                        e.setType(type);
                        e.setxPos(xPos);
                        e.setyPos(yPos);
                        e.setColor(color);

                        elements.add(e);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewElements();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
