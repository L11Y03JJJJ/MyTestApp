package com.example.upton_app.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.upton_app.Login_MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SocketService extends Service {
    //socket
    private Socket socket;

    //字符编码
    private String charset = "utf-8";
    private int bytesize = 1024;

    //连接线程
    private Thread connectThread;
    private Timer timer = new Timer();
    private OutputStream outputStream;
    private String ip = "192.168.3.20"; //ip
    private Integer port = 4531; //端口
    private TimerTask task;

    //默认重连
    private Boolean isReConnect = true;
    // 心跳间隔时间
    private int socketinterval = 15;
//    private String ssssssss;

    //回调函数 用来给mainactivity回复参数
    private Callback callback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SocketBinder();
    }

    public class SocketBinder extends Binder {
        //返回SocketService  在需要的地方可以通过serviceconnection获取到SocketService
        public SocketService getService() {
            return SocketService.this;
        }
//        public String  rec() {
//            return ssssssss;
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.e("LYJ", "onCreate: ");
    }


    //回调方法的使用
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback {
        void onDataChange(String data);
    }


    //    @Override
//    public void onStart(Intent intent, int startId) {//过期的
//        super.onStart(intent, startId);
//        Log.d(TAG,"onStart");
//
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化socket
        initSocket();
        //接收socket数据
        receiveSocketData();
        //Log.e("LYJ", "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSocket() { //初始化
        if (socket == null && connectThread == null) {
            List<Object> li = Login_MainActivity.GetMyIPAndPort();
            ip = li.get(0).toString();
            port =Integer.parseInt( li.get(1).toString());

            Log.e("L11Y03JJJJ", "获取到设置："+ip);
            Log.e("L11Y03JJJJ", "获取到设置："+port);

            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    socket = new Socket();
                    try {
                        //超时时间设置 5s
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                        sendBeatData(); //连接成功的话  ==发送心跳
                        Log.e("L11Y03JJJJ", "Socket连接成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            callback.onDataChange("连接超时  正在重连");
//                            Log.e("L11Y03JJJJ", "连接超时  正在重连");
                            //连接超时  正在重连
                              releaseSocket();
                        } else if (e instanceof NoRouteToHostException) {
                            //地址不存在
                            stopSelf();
                        } else if (e instanceof ConnectException) {
                            //连接异常或被拒绝
                            callback.onDataChange("连接异常或被拒绝");
                            stopSelf();
                        }
                    }
                }
            });
            connectThread.start(); //线程启动
        }
    }

    private void receiveSocketData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStreamReader inputStreamReader = null;
                BufferedInputStream bufferedInputStream = null;
                try {
                    if (socket != null && socket.isConnected()) {
//                            shutdownOutput();
//                            socket.shutdownOutput();
                    }
                    while (true) {
                        if (socket != null && socket.isConnected()) {
                            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                            inputStreamReader = new InputStreamReader(dataInputStream, "utf-8");
                            if (inputStreamReader == null) {
                                continue;
                            }
                            StringBuffer sb = new StringBuffer();
                            int count = 0;
                            char[] buf = new char[1024];
                            count = inputStreamReader.read(buf, 0, buf.length);
                            //一次性接收到所有数据 每个字节拼接到一起 统一转换
                            while (count > -1) {
                                sb.append(buf, 0, count);
                                if (count < 1024) {
                                    break;
                                }
                            }
                            String data = sb.toString();
                            if (data.isEmpty()) {
                                return;
                            }
                            Log.e("L11Y03JJJJ", "接收数据" + data);
//                            BeanTest.recdata=data;
//                            ssssssss=data;
                            callback.onDataChange(data);
                        }
                    }
                } catch (IOException e) {
                    try {
                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                        }
                        if (inputStreamReader != null) {
                            inputStreamReader.close();
                        }
                    } catch (IOException ex) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //发送数据
    public void sendData(final String data) {
        if (socket != null && socket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    BufferedReader bufferedReader = null;
                    try {
                        // 1.
                        if (!socketConnected(socket)){
                            return;
                        }
                        //发送数据到服务端
                        outputStream = socket.getOutputStream();
                        outputStream.write(data.getBytes(charset));
                        outputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        try {
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    //发送心跳
    private void sendBeatData() {
        if (timer==null){
            timer=new Timer();
        }
        if (task==null){
            task=new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (!socketConnected(socket)){
                            return;
                        }
                        //发送数据到服务端
                        outputStream = socket.getOutputStream();
                        outputStream.write("beat".getBytes(charset));
                        outputStream.flush();
                    }catch (IOException e){
                        releaseSocket();
                        e.printStackTrace();
                    }
                }
            };
        }
        timer.schedule(task,0,socketinterval*1000);
    }

    //判断socket是否联通
    private Boolean socketConnected(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //释放资源
    private void releaseSocket() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        if (connectThread != null) {
            connectThread = null;
        }
        if (isReConnect) {
            initSocket();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
