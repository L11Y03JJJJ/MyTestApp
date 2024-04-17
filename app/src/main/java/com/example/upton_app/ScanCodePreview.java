package com.example.upton_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.upton_app.Service.SocketService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class ScanCodePreview extends AppCompatActivity implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder holder;

    private ServiceConnection serviceConnection;
    private SocketService socketService;
    private SocketService.SocketBinder binder;

    private String whereScanCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancodepreview);

        SurfaceView preview = findViewById(R.id.scan_preview);
        holder = preview.getHolder();
        holder.addCallback(this);

        //接收携带来的数据
        Intent intent = getIntent();
        //拆除bundle
        String code = intent.getStringExtra("scan");
        whereScanCode=code;
//        Log.e("TAG", "onCreate: "+whereScanCode );


        bindSocketService(); //绑定socket服务

        // 限制横屏  禁止
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        Intent intent = new Intent(ScanCodePreview.this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void TTTT(View view) {
        IntentIntegrator integrator = new IntentIntegrator(ScanCodePreview.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("请将二维码置于扫描框内2");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();

            IntentIntegrator integrator = new IntentIntegrator(ScanCodePreview.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

            integrator.setPrompt("请将二维码置于扫描框内");
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
    }

    //重写该方法，以回调的形式来获取Activity返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("L11Y03JJJJ", "ScanCodePreview_MainActivity    接收到  requestCode：" + requestCode
                + " resultCode:" + resultCode
                + " data:" + data);

        // 转换为
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(ScanCodePreview.this, "扫到的QR码为：" + result.getContents(), Toast.LENGTH_SHORT).show();
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            //给上一个activity回调数据
            if (data == null) {
                Intent intent = new Intent();
                intent.putExtra("code", "扫码失败");//Scan code failed
                ScanCodePreview.this.setResult(102, intent); //返回给Mainactivity
            } else {
                String code = result.getContents();
                if (whereScanCode.contains("1")) { //签到
                    Intent intent = new Intent(ScanCodePreview.this, ScanEquipmentRepair.class);
                    intent.putExtra("code", code);
                    socketService.sendData(code);
                    ScanCodePreview.this.setResult(102, intent); //返回给Mainactivity
                } else { //报修
                    Intent intent = new Intent(ScanCodePreview.this, ScanEquipmentRepair.class);
                    intent.putExtra("code", code);
                    startActivityForResult(intent, 103);
                }

//                if (code.length() == 4) { // 扫码报修签到
//                    Intent intent = new Intent(ScanCodePreview.this, ScanEquipmentRepair.class);
//                    intent.putExtra("code", code);
//                    socketService.sendData(code);
//                    ScanCodePreview.this.setResult(102, intent); //返回给Mainactivity
//                } else if (code.length() == 8) { //扫码报修长度 跳转另一个周期
//                    Intent intent = new Intent(ScanCodePreview.this, ScanEquipmentRepair.class);
//                    intent.putExtra("code", code);
//                    startActivityForResult(intent, 103);
//                }

            }
            ScanCodePreview.this.finish(); // 不管有没有扫到 都直接关闭这个生命周期
        }
    }
}