package com.example.upton_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.upton_app.ManageMysql.MysqlConnection;
import com.example.upton_app.Service.SocketService;
import com.example.upton_app.bottom_fragment.EquipmentApplicationFragment;
import com.example.upton_app.bottom_fragment.EquipmentManagementFragment;
import com.example.upton_app.bottom_fragment.MyFragmentPageAdapter;
import com.example.upton_app.bottom_fragment.ProductFragment;
import com.example.upton_app.bottom_fragment.ReportFormsFragment;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "L11Y03JJJJ";

    // 初始页面的加载
    ViewPager2 main_viewPager_activity;//先找到viewpager
    private LinearLayout product, equipmentapplication, equipmentmanagement, reportforms;
    private ImageView imgproduct, imgequipmentapplication, imgequipmentmanagement, imgreportforms, imghuancun;

    // socket使用
    private Intent socketIntent;
    private ServiceConnection serviceConnection;
    private SocketService socketService;
    private SocketService.SocketBinder binder;

    private  MysqlConnection mysqlcontest;

    // 当不在生产页面时  消息提示使用
    private NotificationManager manager;//消息提示使用
    private Notification notification;//消息提示使用
    private boolean IsShow = false; //判断当前软件是否在前台运行中

    // 生产页面  表格加载使用
    private List<String> itemsdata;
    private RecyclerView recyclerView;

    // 生产数据库加载使用
    private MysqlConnection promysqlConnection;

    // 报表使用
    private WebView webView;
    private MysqlConnection reportformmysqlConnection;
    private List<Object> reqportformlist;
    private int oknum = 0; //临时合格数
    private int ngnum = 0;//临时不合格数


    @Override
    protected void onStart() { //2.
        super.onStart();
        //Log.d(TAG, "onStart");
        IsShow = false;
    }

    @Override
    protected void onResume() { //3.渲染完成
        super.onResume();
        //Log.d(TAG, "onResume");
        IsShow = false;
    }

    @Override
    protected void onPause() { //4.当不在前台后
        super.onPause();
        //Log.d(TAG,"onPause");
        IsShow = true;
    }

    @Override
    protected void onRestart() { //重启后
        super.onRestart();
        //Log.d(TAG,"onRestart");
        IsShow = false;
    }

    @Override
    protected void onStop() {//5.当不在前台后
        super.onStop();
        //Log.d(TAG,"onStop");
        IsShow = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG,"onDestroy");
        IsShow = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ininPager(); //初始化pageAdapter等信息  同时实现对页面切换的监听
        initTable(); //实现监听和图片的加载

        //绑定socket服务连接
        bindSocketService();
        //启动socket服务
        socketIntent = new Intent(MainActivity.this, SocketService.class);
        startService(socketIntent);

        //连接数据库操作
        promysqlConnection = new MysqlConnection();
        promysqlConnection.Connectionsql();

        reportformmysqlConnection= new MysqlConnection();
        reportformmysqlConnection.Connectionsql();
    }

    //初始化pageAdapter等信息  同时实现对页面切换的监听
    private void ininPager() {
        main_viewPager_activity = findViewById(R.id.id_viewpager_activity);
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(ProductFragment.newInstance("生产", ""));
        fragments.add(EquipmentApplicationFragment.newInstance("设备应用", ""));
        fragments.add(EquipmentManagementFragment.newInstance("设备管理", ""));
        fragments.add(ReportFormsFragment.newInstance("智能报表", ""));


        MyFragmentPageAdapter pageAdapter = new MyFragmentPageAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        main_viewPager_activity.setAdapter(pageAdapter);
        main_viewPager_activity.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ChangeFragmentTable(position); //滑动
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    //实现监听和图片的加载
    private void initTable() {
        product = findViewById(R.id.id_linearlayout_product_bottom);
        product.setOnClickListener(this);
        equipmentapplication = findViewById(R.id.id_linearlayout_equipmentapplication_bottom);
        equipmentapplication.setOnClickListener(this);
        equipmentmanagement = findViewById(R.id.id_linearlayout_equipmentmanagement_bottom);
        equipmentmanagement.setOnClickListener(this);
        reportforms = findViewById(R.id.id_linearlayout_reportforms_bottom);
        reportforms.setOnClickListener(this);

        imgproduct = findViewById(R.id.id_img_product_bottom);
        imgequipmentapplication = findViewById(R.id.id_img_equipmentapplication_bottom);
        imgequipmentmanagement = findViewById(R.id.id_img_equipmentmanagement_bottom);
        imgreportforms = findViewById(R.id.id_img_reportforms_bottom);


        //默认选择第一个fragment
        imghuancun = imgproduct;
        imgproduct.setSelected(true);
    }

    // 绑定socket服务连接
    private void bindSocketService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
                socketService.setCallback(new SocketService.Callback() { //回调函数 由service回调到这里
                    @Override
                    public void onDataChange(String data) {
                        TextView textView = findViewById(R.id.id_product_textview);

//                        Gson gson = new Gson();
//
////                        String json = gson.toJson(data);//把java对象传给gson方法--变成json数据
//                        BeanTest beanTest=gson.fromJson(data,BeanTest.class);
//                        Log.e(TAG, "gson: " + beanTest.getRecdata2());
                        ShowNotification(data);


                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent aintent = new Intent(MainActivity.this, SocketService.class);
        bindService(aintent, serviceConnection, BIND_AUTO_CREATE);
    }

    // 显示服务器发送来的消息提示框
    private void ShowNotification(String data) {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("mes", "MES", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        if (IsShow) {
            //消息提示  收到发送的信息后 提示信息
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            PendingIntent pendingintent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE);
            notification = new NotificationCompat.Builder(MainActivity.this, "mes").setContentTitle("来自服务器信息")//通知标题
                    .setContentText(data) //通知内容
                    .setSmallIcon(R.drawable.message_server) //通知小图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pic)).setColor(Color.parseColor("#ff0000")).setContentIntent(pendingintent) //点击跳转
                    .setAutoCancel(true)//点击后是否消失
                    .build();
            //            设置前三个就会有通知
            manager.notify(1, notification);//显示通知
        }
    }

    // 加载生产页面的数据库信息
    private void ProLoadMysql(){
        //生产加载信息
        //因为刚加载程序时 mysql连接慢 所以延迟一定时间后进行查询
        new  Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    promysqlConnection.selectsqluse("SELECT * FROM p701399.orgdatalist order by id desc limit 20 ");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        promysqlConnection.setMysqlCallback(new MysqlConnection.Callback() { //回调 接收到查到的mysql数据信息
            @Override
            public void onDataChange(ResultSet data) {
                try {
                    itemsdata = new ArrayList<>();
                    recyclerView = findViewById(R.id.id_product_recycleview);
                    while (data.next()) {
                        //Log.e(TAG, "onDataChange: " + data.getString("UpdateDateTime"));
                        itemsdata.add(data.getString("Barcode"));
                    }
//                            RecyclerView recyclerView = findViewById(R.id.id_product_recycleview);
                    //网格状的
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2, RecyclerView.VERTICAL, false);
                    MyRecycleViewAdapter myRecycleViewAdapter = new MyRecycleViewAdapter(itemsdata, MainActivity.this); // 必须要这样 否则点击事件无法出发

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setLayoutManager(gridLayoutManager);//设置
                            recyclerView.setAdapter(myRecycleViewAdapter);
                        }
                    });
                    myRecycleViewAdapter.setRecycleItemCLickLister(new MyRecycleViewAdapter.OnRecycleItemClick() {
                        @Override
                        public void ItemCLick(int position) {
                            Log.e(TAG, "点击到了" + position);
                            Log.e(TAG, "点击到了" + itemsdata.get(position));

                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    // 加载报表页面的数据库信息
    private void ReportLoadMysql() {
        //报表查询
        reportformmysqlConnection.selectsqluse("SELECT * FROM p701399.orgdatalist order by id desc limit 100 ");
        reportformmysqlConnection.setMysqlCallback(new MysqlConnection.Callback() { //回调 接收到查到的数据信息
            @Override
            public void onDataChange(ResultSet data) {
                reqportformlist = MysqlRowsColumnsNames(data); // 行数  列数  列的名称      List<Object>
                // 件数 ： count
                // 合格数： ispass
                // 不合格数： ispass=0
                try {
                    ResultSetMetaData resultSetMetaData = data.getMetaData();

                    for (int i = 0; i <= (int) reqportformlist.get(0) - 1; i++) {
                        if (data.getString("IsPass").contains("1")) {
                            oknum += 1;
                        } else {
                            ngnum += 1;
                        }
                        data.next();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Log.e(TAG, "MYSQL查出数据: " + oknum + "   " + ngnum);
                Log.e(TAG, "MYSQL查出数据: " + reqportformlist.get(0) + "   " + reqportformlist.get(1) + "     " + reqportformlist.get(2));
            }
        });
    }

    //更改fragment 切换
    private void ChangeFragmentTable(int position) {
        //更改
        //        Log.e("L11Y03JJJJ", "更改页面到第  ：" + position);
        imghuancun.setSelected(false);

        switch (position) { //滑动触发
            case 0:
                imgproduct.setSelected(true);
                imghuancun = imgproduct;
               ProLoadMysql();
                break;
            case 1:
                imgequipmentapplication.setSelected(true);
                imghuancun = imgequipmentapplication;
                //申请权限  摄像头 和 音频权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,//是否允许打开摄像头
                                Manifest.permission.RECORD_AUDIO,//是否允许打开音频
                                Manifest.permission.WRITE_EXTERNAL_STORAGE} //是否允许访问文件夹.....
                        , 1000);
                break;
            case 2:
                imgequipmentmanagement.setSelected(true);
                imghuancun = imgequipmentmanagement;
                break;
            case 3:
                imgreportforms.setSelected(true);
                imghuancun = imgreportforms;
                ReportLoadMysql();
                break;
        }
        //点击事件
        if (position == R.id.id_linearlayout_product_bottom) {
            imgproduct.setSelected(true);
            imghuancun = imgproduct;
            main_viewPager_activity.setCurrentItem(0);
        } else if (position == R.id.id_linearlayout_equipmentapplication_bottom) {

            imgequipmentapplication.setSelected(true);
            imghuancun = imgequipmentapplication;
            main_viewPager_activity.setCurrentItem(1);

        } else if (position == R.id.id_linearlayout_equipmentmanagement_bottom) {
            imgequipmentapplication.setSelected(true);
            imghuancun = imgequipmentapplication;
//            //申请权限  摄像头 和 音频权限
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,//是否允许打开摄像头
//                            Manifest.permission.RECORD_AUDIO,//是否允许打开音频
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE} //是否允许访问文件夹.....
//                    , 1000);
            main_viewPager_activity.setCurrentItem(2);

        } else if (position == R.id.id_linearlayout_reportforms_bottom) {
            imgreportforms.setSelected(true);
            imghuancun = imgreportforms;
            main_viewPager_activity.setCurrentItem(3);
//            ReportLoadMysql();
        }
    }

    // 获取到查到的数据的行 列 列名称
    private ArrayList<Object> MysqlRowsColumnsNames(ResultSet resultSet) {
        ArrayList<Object> list = new ArrayList<>();
        int rowscount;
        int columnscount;
        ArrayList<String> columnname = new ArrayList<>();

        try {
            resultSet.last();
            rowscount = resultSet.getRow();
            resultSet.first();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            columnscount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnscount; i++) {
                columnname.add(resultSetMetaData.getColumnName(i));
            }
            list.add(rowscount);
            list.add(columnscount);
            list.add(columnname);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    // 点击事件触发
    @Override
    public void onClick(View view) {
        ChangeFragmentTable(view.getId());
    }

    //重写该方法，以回调的形式来获取Activity返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("L11Y03JJJJ", "MainActivity    接收到  requestCode：" + requestCode + " resultCode:" + resultCode + " data:" + data);

        //当请求码和结果码都是0时，也就是处理的特定结果-----------巡检上传后的返回结果
        if (requestCode == 100 && resultCode == 101) { // 巡检  想i返回结果
            //获取Bundle中的数据
            Bundle bundle = data.getExtras();
            String city = bundle.getString("key1");
            Log.e("L11Y03JJJJ", "key1:  " + city);
        } else if (requestCode == 100 && resultCode == 102) { //巡检 相册返回结果
            //获取Bundle中的数据
            Bundle bundle = data.getExtras();
            String city = bundle.getString("key2");
            Log.e("L11Y03JJJJ", "key2:  " + city);
        } else if (requestCode == 102 && resultCode == 103) { // 扫码维修
            //Toast.makeText(MainActivity.this, "设备 扫码维修 "  + "  提交成功！！ ", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 101 && resultCode == 102) { // 扫码签到 返回
            //接收到扫码返回的信息
            Bundle bundle = data.getExtras();
            String code = bundle.getString("code");
            if (code.contains("扫码失败")) {
                Toast.makeText(MainActivity.this, "设备码： " + code + "  维修人员签到失败！！ ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "设备码： " + code + "  维修人员签到成功！！ ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 设备应用页面的巡检功能事件 进行跳转activity
    public void PatrolInspectionAction(View view) {
        Intent intent = new Intent(MainActivity.this, PatrolInspection_MainActivity.class);

        // 携带数据到下一个activity中
        //封装 Bundle对象
        Bundle bundle = new Bundle();
        bundle.putString("MainActivity", "activity1");
        //通过intent携带
        intent.putExtras(bundle);
        startActivityForResult(intent, 100);
    }

    // 扫码签到按钮
    public void ScanEquipmentCodeSigninAction(View view) {
        Intent intent = new Intent(MainActivity.this, ScanCodePreview.class);
        intent.putExtra("scan", "1");
        startActivityForResult(intent, 101); //跳转 使用照相机 会到回调形式那里接收到数据
    }

    // 扫码报修按钮
    public void ScanEquipmentCodeRepairAction(View view) {
        Intent intent = new Intent(MainActivity.this, ScanCodePreview.class);
        intent.putExtra("scan", "2");
        startActivityForResult(intent, 102); //跳转 使用照相机 会到回调形式那里接收到数据
    }

    public void ShowECharts(View view) {


        webView = findViewById(R.id.testidss);
        webView.loadUrl("file:///android_asset/html/echartstest1.html");

//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) { //1
//
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {//2
//
//                super.onPageFinished(view, url);
//            }
//
//
//        });
//        if (webView == null) {
//            Log.e(TAG, "SHow: eee" + webView);
//        } else {
//            Log.e(TAG, "SHow: eee222222" + webView);
//
//        }

    }
    public void USEJSAction(View view) {
        webView = findViewById(R.id.testidss);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/html/index.html");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {//2
                super.onPageFinished(view, url);
                webView.loadUrl("Javascript:test2(" + "'" + oknum + "'" + "," + "'" + ngnum + "'" + ")");
            }

        });
//        webView.evaluateJavascript("Javascript:test2(" + "'" + oknum + "'" + "," + "'" + ngnum + "'" + ")", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String s) {
//
//            }
//        });
        //webView.loadUrl("Javascript:test2(" + "'" + oknum + "'" + "," + "'" + ngnum + "'" + ")");
    }
}