package com.example.lgh.get_iphone_info;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {
//    TextView text01 = (TextView)findViewById(R.id.test01);
//    TextView text02 = (TextView)findViewById(R.id.test02);
//    TextView text03 = (TextView)findViewById(R.id.test03);
    TextView text01;
    TextView text02;
    TextView text03;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();//String

        text01 = (TextView)findViewById(R.id.test01);
        text01.setText("IMEI为:"+IMEI);
        String number = tm.getLine1Number();//String
        text02 = (TextView)findViewById(R.id.test02);
        text02.setText("手机号为:"+number);

        String NativePhoneNumber=null;
        NativePhoneNumber=tm.getLine1Number();
        text03 = (TextView)findViewById(R.id.test03);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取外网ip
                final String ip = getNetIp();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text03.setText("ip为："+ip);
                    }
                });
            }
        }).start();

        Log.d("dd",IMEI);
        Log.d("dd1",NativePhoneNumber);
        Button btn_refresh = (Button)findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        Button btn_message = (Button)findViewById(R.id.btn_message);
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,get_message.class);
                startActivity(intent);
            }
        });

    }
    public void refresh() {
        onDestroy();
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);

    }

    public static String getNetIp() {
        String ip = "";
        InputStream inStream = null;
        try {
            URL infoUrl = new URL("http://2017.ip138.com/ic.asp");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "gb2312"));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                inStream.close();
                int start = builder.indexOf("[");
                int end = builder.indexOf("]");
                ip = builder.substring(start + 1, end);
                return ip;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}