package com.example.lgh.get_iphone_info;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView text01;
    TextView text03;
    TextView resText,project,area,iphone_number,message;
    ImageButton getBut,getBut1;
    MyHandler handler;
    ProgressBar pro,pro2,pro3;
    File file;
    Button btn_start_anjian,downapk,install_JM,uninstall;
    String apk_path,URL_STRING,packageName,currentTempFilePath,token,token_info,money,project_info,project_name,area_info,number_info,message_info,message_info_true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();//String
        URL_STRING = "http://shouji.360tpcdn.com/171226/b750fedd2b6fc179f5ef7b8c6080f6ab/com.julanling.app_5500.apk";//下载文件的地址
        //APK安装目录
        currentTempFilePath = "/sdcard/Test";
        //APK路径
        apk_path = currentTempFilePath+URL_STRING.substring(URL_STRING.lastIndexOf("/"), URL_STRING.length());
        //APK包名
        packageName = "com.julanling.app";

        text01 = (TextView)findViewById(R.id.test01);
        text01.setText("IMEI为:"+IMEI);

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
        ImageButton btn_refresh = (ImageButton)findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        resText=(TextView)findViewById(R.id.resText);
        project=(TextView)findViewById(R.id.project);
        area=(TextView)findViewById(R.id.area);
        iphone_number=(TextView)findViewById(R.id.number);
        message=(TextView)findViewById(R.id.message);

        getBut=(ImageButton)findViewById(R.id.getBut);
        getBut.setOnClickListener(this);
        getBut1=(ImageButton)findViewById(R.id.getBut1);
        getBut1.setOnClickListener(this);

        btn_start_anjian = (Button) findViewById(R.id.start_anjian);
        btn_start_anjian.setOnClickListener(this);


        pro = (ProgressBar)findViewById(R.id.progressBar);
        pro2 = (ProgressBar)findViewById(R.id.progressBar2);
        pro3 = (ProgressBar)findViewById(R.id.progressBar3);

        downapk = (Button) findViewById(R.id.downapk);
        downapk.setOnClickListener(this);

        install_JM = (Button) findViewById(R.id.install_JM);
        install_JM.setOnClickListener(this);

        uninstall = (Button) findViewById(R.id.uninstall);
        uninstall.setOnClickListener(this);

        handler=new MyHandler();

    }
    public void refresh() {
        onDestroy();
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);
        Toast_message("刷新手机信息成功");

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
    public void onClick(View v) {

        if(v==getBut) {
            new MainActivity.GetThread().start();//用get方法发送
            boolean work = true;
            while (work == true) {
                if (token != null) {
                    new MainActivity.get_project().start();
                    Log.d("qqq", token);
                    break;
                }
            }
            while (work == true) {
                if (project_info != null) {
                    Log.d("qqq", project_info);
                    new MainActivity.get_area().start();
                    new MainActivity.get_number().start();
                    break;
                }
            }
            while (work == true) {
                if (number_info != null) {
                    resText.setText("账户余额:" + money + "\n");
                    project.setText("项目:" + project_name);
//                    area.setText("区域:"+area_info);
                    iphone_number.setText("手机号:" + number_info);
                    initData(number_info);
                    Log.d("aaaaa", number_info);
                    new MainActivity.get_messages().start();
                    break;
                }
            }
        }
        if(v==getBut1) {
            get_message_info();
        }
        if(v==btn_start_anjian){
            Intent intent = getPackageManager().getLaunchIntentForPackage(
                    //这个是另外一个应用程序的包名
                    "com.cyjh.mobileanjian");
            startActivity(intent);
        }
        if(v==downapk){
            Toast_message("开始下载APK");
            pro.setVisibility(View.VISIBLE);
            new MainActivity.DownApk().start();//发送消息，启动线程运行
        }
        if(v==install_JM){
            Toast_message("开始安装");
            pro2.setVisibility(View.VISIBLE);
            new MainActivity.Install_JM().start();//发送消息，启动线程运行
        }
        if(v==uninstall){
            Toast_message("开始卸载");
            pro3.setVisibility(View.VISIBLE);
            new MainActivity.Uninstall().start();//发送消息，启动线程运行
        }
    }
    public void get_message_info(){
        boolean work = false;
        int count = 0;
        while (work == false & count<10){
            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            new MainActivity.get_messages().start();
            count += 1;
            System.out.println("正在等待："+count);
            while (message_info.indexOf("False")==-1){
                work = true;
                System.out.println("成功："+message_info);
                message_info_true = message_info;
                Pattern p = Pattern.compile("码(\\d+)");
                Matcher m = p.matcher(message_info_true);
                if(m.find()){
                    message.setText("短信验证码:"+m.group(1));
                    initcode("手机号"+number_info+"&"+"验证码："+m.group(1),m.group(1));

                }
                break;
            }
        }
    }
    //网络线程，因为不能在主线程访问Intent
    class GetThread extends Thread{
        public void run(){
            HttpURLConnection conn=null;//声明连接对象
            String urlStr="http://api.shjmpt.com:9002/pubApi/uLogin?uName=%E5%94%90%E5%9D%87&pWord=tangjun!((!";
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if(conn.getResponseCode()==200){//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData += inputLine + "\n";

                    }

                    System.out.println("账号信息："+resultData);
                    token_info = resultData;

                    String [] arr=resultData.split("&");
                    token = arr[0];
                    money = arr[1];
//                    showRes("账号信息：" + resultData);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);//将Message对象发送出去
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    //用于主线程发送消息
//    public void showRes(String res){
//        Bundle bundle=new Bundle();
//        bundle.putString("res",res);//bundle中也可以放序列化或包裹化的类对象数据
//
//        Message msg=handler.obtainMessage();//每发送一次都要重新获取
//        msg.setData(bundle);
//        handler.sendMessage(msg);//用handler向主线程发送信息
//    }

    //获取项目
    class get_project extends Thread{
        public void run(){
            HttpURLConnection conn=null;//声明连接对象
            String urlStr="http://api.shjmpt.com:9002/uGetItems?token="+token+"&tp=ut";
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if(conn.getResponseCode()==200){//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData += inputLine + "\n";

                    }
                    System.out.println("项目："+resultData);
                    String [] arr=resultData.split("&");
                    String i = arr[6];
                    String [] arr1=i.split("\\n");
                    project_info = arr1[1];
                    project_name = arr[7];
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);//将Message对象发送出去
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //获取区域
    class get_area extends Thread{
        public void run(){
            HttpURLConnection conn=null;//声明连接对象
            String urlStr="http://api.shjmpt.com:9002/uGetArea?";
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if(conn.getResponseCode()==200){//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData += inputLine + "\n";

                    }
                    System.out.println("区域："+resultData);
                    area_info = resultData;
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);//将Message对象发送出去
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //获取手机号
    class get_number extends Thread{
        public void run(){
            HttpURLConnection conn=null;//声明连接对象
            String urlStr="http://api.shjmpt.com:9002/pubApi/GetPhone?ItemId="+project_info+"&token="+token+"&Count=1";
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if(conn.getResponseCode()==200){//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData += inputLine + "\n";

                    }
                    System.out.println("手机号："+resultData);
                    String [] arr=resultData.split(";");
                    number_info = arr[0];
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);//将Message对象发送出去
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //获取短信信息
    class get_messages extends Thread{
        public void run(){
            HttpURLConnection conn=null;//声明连接对象
            String urlStr="http://api.shjmpt.com:9002/pubApi/GMessage?token="+token+"&ItemId="+project_info+"&Phone="+number_info;
            Log.d("aaaaa",urlStr);
            System.out.println("连接："+urlStr);
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if(conn.getResponseCode()==200){//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData += inputLine + "\n";

                    }
                    System.out.println("短信内容："+resultData);
                    message_info = resultData;
                    Message message = new Message();
                    message.what = 5;
                    handler.sendMessage(message);//将Message对象发送出去
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //自定义handler类
    class MyHandler extends Handler {
        @Override
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //这里可以进行UI操作
                    pro.setVisibility(View.INVISIBLE);
                    downapk.setText("下载完成");
                    downapk.setTextColor(Color.RED);
                    Toast_message("下载成功");
//                    resText.setText("获取短信验证用户信息:"+token+"\n"+"账户余额:"+money+"\n");
                    break;
                case 2:
//                    project.setText("项目编号:"+project_info);
                    pro2.setVisibility(View.INVISIBLE);
                    install_JM.setText("安装完成");
                    install_JM.setTextColor(Color.RED);
                    Toast_message("安装成功");
                    break;
                case 3:
//                    area.setText("区域:"+area_info);
                    pro3.setVisibility(View.INVISIBLE);
                    uninstall.setText("卸载完成");
                    uninstall.setTextColor(Color.RED);
                    delete_apk(apk_path);
                    Toast_message("已卸载并删除APK包");
                    break;
                case 4:
//                    number.setText("手机号:"+number_info);
                    break;
                case 5:
//                    while (message_info_true != null){
//                        message.setText("短信内容:"+message_info_true);
                    break;
//                    }

                default:
                    break;
            }

        }
    }
    private void initData(String num) {
        String filePath = "/sdcard/Test/";
        String fileName = "iphone_num.txt";
        String fileName1 = "iphone_num_one.txt";

        writeTxtToFile(num, filePath, fileName);
        writeTxtToFile_one(num, filePath, fileName1);
    }
    private void initcode(String num,String num1) {
        String filePath2 = "/sdcard/Test/";
        String fileName2 = "iphone_num_code.txt";
        String fileName3 = "iphone_num_code_one.txt";

        writeTxtToFile_code(num, filePath2, fileName2);
        writeTxtToFile_code_one(num1, filePath2, fileName3);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    // 将字符串写入到文本文件中，覆盖
    public void writeTxtToFile_one(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(strContent.getBytes());
            out.flush();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    // 将验证码写入到文本文件中
    public void writeTxtToFile_code(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    // 将验证码写入到文本文件中，覆盖
    public void writeTxtToFile_code_one(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            file.delete();
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);
            out.write(strContent.getBytes());
            out.flush();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    //网络线程，因为不能在主线程访问Intent
    //安装APK
    class Install_JM extends Thread{
        public void run(){
            Process process = null;
            OutputStream out = null;
            InputStream in = null;
            try {
                // 请求root
                process = Runtime.getRuntime().exec("su");
                out = process.getOutputStream();
                // 调用安装
                out.write(("pm install -r " + currentTempFilePath+getFilePath(URL_STRING) + "\n").getBytes());
                in = process.getInputStream();
                int len = 0;
                byte[] bs = new byte[256];
                while (-1 != (len = in.read(bs))) {
                    String state = new String(bs, 0, len);
                    if (state.equals("Success\n")) {
                        //安装成功后的操作
                        Message message = handler.obtainMessage();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
                // 往handler发送一条消息 更改button的text属性

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //卸载应用程序
    class Uninstall extends Thread {
        public void run() {
            Process process = null;
            DataOutputStream dos = null;
            StringBuilder cmd = new StringBuilder();
            cmd.append("pm uninstall " + packageName).append("\n");
            try {
                process = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(process.getOutputStream());
                dos.writeBytes(cmd + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                process.waitFor();

            } catch (Exception e) {
            } finally {
                try {
                    if (dos != null) {
                        dos.close();
                        Message message = handler.obtainMessage();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //下载应用程序
    class DownApk extends Thread {
        public void run() {
            try {
                URL url = new URL(URL_STRING);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                FileOutputStream fileOutputStream = null;
                InputStream inputStream;
                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();

                    if (inputStream != null) {
                        file = getFile(URL_STRING);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int length = 0;

                        while ((length = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.close();
                        fileOutputStream.flush();
                    }
                    inputStream.close();
                }
                // 往handler发送一条消息 更改button的text属性
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据传过来url创建文件
     */
    private File getFile(String url) {
        File files = new File("/sdcard/Test/", getFilePath(url));
        return files;
    }
    /**
     * 截取出url后面的apk的文件名
     * @param url
     * @return
     */
    private String getFilePath(String url) {
        return url.substring(url.lastIndexOf("/"), url.length());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void delete_apk(String url){
        File file = new File(url);
        System.out.print(url);
        if (file.exists()){
            file.delete();
        }
    }
    //弹窗提示
    private void Toast_message(String toast){
        Toast.makeText(getApplicationContext(), toast,
                Toast.LENGTH_SHORT).show();
    }
}