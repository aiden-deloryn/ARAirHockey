package com.drksft.arhockeyandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends Activity implements SensorEventListener {
    String url;
    Runnable r;
    public float x;
    public float y;
    public String uuid;
    public String tablePosition;
    public boolean tableTopMode = false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ipBox = (EditText) findViewById(R.id.ipBox);
        Switch connectSwitch = (Switch)findViewById(R.id.connectSwitch);
        Switch modeSwitch = (Switch)findViewById(R.id.tableTopSwitch);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        uuid = tManager.getDeviceId();

        final Handler handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(r, 100);
                url = ipBox.getText().toString();
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
            }
        };

        connectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    handler.post(r);
                }
            }
        });

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tableTopMode = true;
                }
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if(tableTopMode){
                if(tablePosition == "Left"){
                    x = event.values[0] / 10;
                    y = event.values[1] / 10;
                }else if(tablePosition == "Right"){
                    x = event.values[0] / 10;
                    y = event.values[1] / 10;
                }else if(tablePosition == "Top"){
                    x = -event.values[0] / 10;
                    y = -event.values[1] / 10;
                }else if(tablePosition == "Bottom"){
                    x = event.values[0] / 10;
                    y = event.values[1] / 10;
                }
            }else {
                x = event.values[0] / 10;
                y = event.values[1] / 10;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sendHTTPRequest(String url) {
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            response = client.execute(request);
            String responseString = EntityUtils.toString(response.getEntity());
            Log.i("Response", responseString);
            if(responseString.equals("playerColour=Blue")){
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                tablePosition = "Left";
            }else if(responseString.equals("playerColour=Red")){
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                tablePosition = "Right";
            }else if(responseString.equals("playerColour=Yellow")){
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                tablePosition = "Top";
            }else if(responseString.equals("playerColour=Green")){
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                tablePosition = "Bottom";
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                sendHTTPRequest("http://" + url + ":8080/sendData?x=" + x + "&y=" + y + "&id=" + uuid);
                resp = "Sent";

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}
