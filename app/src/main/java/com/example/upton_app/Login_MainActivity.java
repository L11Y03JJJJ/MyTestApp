package com.example.upton_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Login_MainActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private EditText et_name;
    private EditText et_pwd;

    private EditText et_ip;
    private EditText et_port;
    public static String ips;
    public static Object ports;


    private CheckBox cb_remeber;
    private CheckBox cb_autologin;
    private Button bt_register;
    private Button bt_login;

    public static List<Object> GetMyIPAndPort() {
        List<Object> list = new ArrayList<>();
        list.add(ips);
        list.add(ports);
        return list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        //1. 获取首选项 sp
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        initView(); //初始化

        //第二次打开  从sp获取数据 进行画面同步   回想数据
        boolean remeber = sp.getBoolean("remeber", false); //为空 返回莫i认知
        boolean auto = sp.getBoolean("autologin", false); //为空 返回莫i认知
        if (remeber) {
            //获取sp里面的name和pd  并输出在text
            String name = sp.getString("name", " ");
            String pwd = sp.getString("pwd", " ");
            String ip = sp.getString("ip", " ");
            String port = sp.getString("port", " ");
            et_name.setText(name);
            et_pwd.setText(pwd);
            et_ip.setText(ip);
            et_port.setText(port);

            ips=ip;
            ports=port;

            cb_remeber.setChecked(true);
        }
        if (auto) {
            cb_autologin.setChecked(true);
            //模拟自动登录
            Toast.makeText(Login_MainActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login_MainActivity.this, MainActivity.class)); //页面跳转
        }

    }

    private void initView() {//初始化
        //找到控件
        et_name = findViewById(R.id.et_name);
        et_pwd = findViewById(R.id.et_pwd);
        et_ip=findViewById(R.id.et_ip);
        et_port=findViewById(R.id.et_port);

        cb_remeber = findViewById(R.id.cb_remeber);
        cb_autologin = findViewById(R.id.cb_autologin);

        bt_register = findViewById(R.id.bt_register);
        bt_login = findViewById(R.id.bt_login);


//        设置监听
        MyOnClickListerner l = new MyOnClickListerner();
        bt_login.setOnClickListener(l);
        bt_register.setOnClickListener(l);
    }


    //重写监听
    private class MyOnClickListerner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bt_register) {
                Toast.makeText(Login_MainActivity.this, "功能未开发", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.bt_login) {
                //登录操作
                String name = et_name.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                String ip=et_ip.getText().toString().trim();
                String port=et_port.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(Login_MainActivity.this, "名称或密码为空异常！请输入--", Toast.LENGTH_SHORT).show();
                } else {
                    //判断是否勾选
                    if (cb_remeber.isChecked()) {
                        //进行sp保存操作  tongshi  记住密码的状态也保存
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name", name);
                        editor.putString("pwd", pwd);
                        editor.putString("ip", ip);
                        editor.putString("port", port);

                        editor.putBoolean("remeber", true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("remeber", false);
                        editor.apply();
                    }

                    // 自动登录状态判断
                    if (cb_autologin.isChecked()) {
                        //进行sp保存操作  同时  记住密码的状态也保存
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("autologin", true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("autologin", false);
                        editor.apply();
                    }


                    startActivity(new Intent(Login_MainActivity.this, MainActivity.class));//跳转
                }
            }
        }
    }
}