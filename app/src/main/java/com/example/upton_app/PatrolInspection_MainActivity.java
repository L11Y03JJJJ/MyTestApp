package com.example.upton_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.upton_app.Service.SocketService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PatrolInspection_MainActivity extends AppCompatActivity {

    private ServiceConnection serviceConnection;
    private SocketService socketService;
    private SocketService.SocketBinder binder;


    private ImageView imageView; //图片容器
    private Uri imageurl; // 图片的临时保存路径
    private PopupWindow pop; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_inspection_main);
        imageView = findViewById(R.id.xunjianpic);

        //接收携带来的数据
//        Intent intent = getIntent();
//        //拆除bundle
//        String name = intent.getStringExtra("MainActivity");
//
//        Log.e("TAG", "onCreate: "+name );
        bindSocketService(); //绑定socket服务
    }

    //绑定socket服务 进行提交照片到服务器的操作
    private void bindSocketService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent(PatrolInspection_MainActivity.this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    // 弹出popup  选择不同的功能
    public void popupwindowAction(View view) {
        View popview = getLayoutInflater().inflate(R.layout.popupwindow_choosepic, null);
        Button bt1 = popview.findViewById(R.id.btn1);
        Button bt2 = popview.findViewById(R.id.btn2);
        Button bt3 = popview.findViewById(R.id.btn3);

        //最后一个true --》点击空白可以退出
        pop = new PopupWindow(popview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.aaaaaaaaaa)); //更改背景图片
        pop.showAsDropDown(view);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getExternalCacheDir(), "out.jpg");
                try {
                    if (file.exists()) { //存在就先删除
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 新版安卓要使用FileProvider才行    com.example.upton_app.fileProvider对应xml中的信息
                imageurl = FileProvider.getUriForFile(PatrolInspection_MainActivity.this, "com.example.upton_app.fileProvider", file);
                //打开照相机,进行拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //设置照片的临时保存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageurl);
                startActivityForResult(intent, 0); //跳转 使用照相机 会到回调形式那里接收到数据
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(PatrolInspection_MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(PatrolInspection_MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                } else {
                    openAlbum();//打开相册操作
//                }
            }
        });
        //取消按钮
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("TAG", "取消: ");
                pop.dismiss(); // 点击后退出
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2); //跳转 使用照相机 会到回调形式那里接收到数据
    }

    //重写该方法，以回调的形式来获取Activity返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("L11Y03JJJJ", "PatrolInspection_MainActivity    接收到  requestCode：" + requestCode
                + " resultCode:" + resultCode
                + " data:" + data);

        //当请求码都是0时，也就是处理的特定结果
        try {
            if (requestCode == 0) {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageurl));

                //动态添加方法
//                LinearLayout linearLayout = new LinearLayout(this);
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                ImageView imageView1 = new ImageView(this);
//                imageView1.setImageBitmap(bitmap);
//                linearLayout.addView(imageView1);
//                setContentView(linearLayout);


                // Glide处理图片 暂时不用
//                RequestOptions requestOptions = new RequestOptions()
//                        .override(100, 100);
//                Glide.with(this)
//                        .load(bitmap)
//                        .apply(requestOptions) //在load之后
//                        .transform(new CircleCrop()) //圆角
////                .transform(new RoundedCorners(30)) //角度设置
////                .transform(new GranularRoundedCorners(30,80,80,30)) //角度分别设置
////                .transform(new Rotate(90)) //旋转
//
//                        .into(imageView);
                imageView.setImageBitmap(bitmap);
                pop.dismiss();//隐藏

                //给上一个activity回调数据
                Intent intent = new Intent();
                intent.putExtra("key1", "value1");
                //设置该Activity的结果码，并设置结束之后退回的Activity
                PatrolInspection_MainActivity.this.setResult(101, intent);
                //结束本Activity
//            PatrolInspection_MainActivity.this.finish();
                socketService.sendData("code=1");

            } else if (requestCode == 2) { //相册
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                imageurl=data.getData();
                imageView.setImageBitmap(bitmap);
                pop.dismiss();//隐藏
                //给上一个activity回调数据
                Intent intent = new Intent();
                intent.putExtra("key2", "value2");
                //设置该Activity的结果码，并设置结束之后退回的Activity
                PatrolInspection_MainActivity.this.setResult(102, intent);
                socketService.sendData("code=2");
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (requestCode == 0 && resultCode == 0) {
            //获取Bundle中的数据
//            Bundle bundle=data.getExtras();
//            String city=bundle.getString("city");
            //修改编辑框的内容
            Log.e("LLLLLL", "巡检页面: ");
        }
    }


    //上传数据给服务器
    public void SubmitAction(View view) {
        socketService.sendData(String.valueOf(imageurl));

    }


//    if(requestCode ==REQUEST_CODE_SCAN)
//
//    {
//        Object obj = data.getParcelableExtra(ScanUtil.RESULT);
//        if (obj instanceof HmsScan) {
//            if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
//                Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//    }
//
}