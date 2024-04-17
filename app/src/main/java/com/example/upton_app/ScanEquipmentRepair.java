package com.example.upton_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.upton_app.Service.SocketService;

public class ScanEquipmentRepair extends AppCompatActivity {

    private ServiceConnection serviceConnection;
    private SocketService socketService;
    private SocketService.SocketBinder binder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_equipment_repair);

        bindSocketService(); //绑定socket服务
        //接收携带来的数据
        Intent intent = getIntent();
        //拆除bundle
        String code = intent.getStringExtra("code");

        TextView textView = findViewById(R.id.id_equipment_repair_codetextview);
        textView.setText(code);
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
        Intent intent = new Intent(ScanEquipmentRepair.this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


    }


    private EditText editText1, editText2;

    public void EquipmentRepairSubmitAction(View view) { // 提交
        editText1 = findViewById(R.id.id1);
        editText2 = findViewById(R.id.id2);
        String a1 = editText1.getText().toString();
        String a2 = editText2.getText().toString();

        socketService.sendData(a1 + "    " + a2);

        socketService.setCallback(new SocketService.Callback() {
            @Override
            public void onDataChange(String data) {
                if (data.contains("repairsubmitsuccess")) {
                    // 因为子线程不允许进行ui的修改 所以新建线程来处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScanEquipmentRepair.this, "提交成功 ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ScanEquipmentRepair.this, MainActivity.class);
                            intent.putExtra("code", "code");
                            // 服务器收到后 返回完成信号  关闭这个周期
                            ScanEquipmentRepair.this.finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScanEquipmentRepair.this, "提交失败 ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //消息循环使用 但是有问题
//                    Looper.prepare();
//                    Toast.makeText(ScanEquipmentRepair.this, "失败 ", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
            }
        });
    }


    public void EquipmentRepairClearAction(View view) { // 清除页面的信息
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确认清除页面所有信息吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("暂时退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();

    }


}