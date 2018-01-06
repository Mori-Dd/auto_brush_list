package com.example.lgh.get_iphone_info;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class get_message extends AppCompatActivity implements View.OnClickListener{
    TextView resText,project,area,number,message;
    Button getBut,getBut1;
    MyHandler handler;
    String token,token_info,money,project_info,project_name,area_info,number_info,message_info,message_info_true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_message);

        resText=(TextView)findViewById(R.id.resText);
        project=(TextView)findViewById(R.id.project);
        area=(TextView)findViewById(R.id.area);
        number=(TextView)findViewById(R.id.number);
        message=(TextView)findViewById(R.id.message);

        getBut=(Button)findViewById(R.id.getBut);
        getBut.setOnClickListener(this);
        getBut1=(Button)findViewById(R.id.getBut1);
        getBut1.setOnClickListener(this);

        handler=new MyHandler();

    }
    public void onClick(View v) {

        if(v==getBut) {
            new GetThread().start();//用get方法发送
            boolean work = true;
            while (work == true) {
                if (token != null) {
                    new get_project().start();
                    Log.d("qqq", token);
                    break;
                }
            }
            while (work == true) {
                if (project_info != null) {
                    Log.d("qqq", project_info);
                    new get_area().start();
                    new get_number().start();
                    break;
                }
            }
            while (work == true) {
                if (number_info != null) {
                    resText.setText("账户余额:" + money + "\n");
                    project.setText("项目:" + project_name);
//                    area.setText("区域:"+area_info);
                    number.setText("手机号:" + number_info);
                    initData(number_info);
                    Log.d("aaaaa", number_info);
                    new get_messages().start();
                    break;
                }
            }
        }
        if(v==getBut1) {
            get_message_info();
        }
    }
    public void get_message_info(){
        boolean work = false;
        while (work == false){
            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            new get_messages().start();
            System.out.println("正在等待：");
            while (message_info.indexOf("False")==-1){
                work = true;
                System.out.println("成功："+message_info);
                message_info_true = message_info;
                Pattern p = Pattern.compile("】(\\d+)");
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
//                    resText.setText("获取短信验证用户信息:"+token+"\n"+"账户余额:"+money+"\n");
                    break;
                case 2:
//                    project.setText("项目编号:"+project_info);
                    break;
                case 3:
//                    area.setText("区域:"+area_info);
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
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }else file.delete();

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
}
