package com.example.upton_app.ManageMysql;

import com.example.upton_app.Service.SocketService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlConnection implements  DBDao{
    private static final String URL = "jdbc:mysql://192.168.3.20:3306/p701399?" +
            "useSSL=false&serverTimezone=UTC&characterEncoding=utf8&BatchedStatements=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "6660";
    Connection conn = null;
    Statement stmt = null;
    ResultSet resultSet = null;

    //回调使用
    private Callback callback;

    //回调方法的使用
    public void setMysqlCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void selectsqluse(String sql) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultSet!=null){
                    System.out.println("存在resultSet");
                }
                if (sql.length() == 0 || sql.isEmpty()) {
                    System.out.println("NULL2222");
                } else {
                    try {
                        System.out.println("存在222222");
                        resultSet = stmt.executeQuery(sql);
                        callback.onDataChange(resultSet);//回调给activity页面
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static interface Callback {
        void onDataChange(ResultSet data);
    }


    public void Connectionsql() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("成功加载MySQL驱动程序");
                    conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    System.out.println("成功连接到数据库");
                    stmt = conn.createStatement();

//                    if (sql.length() == 0 || sql.isEmpty())
//                    {
//                        System.out.println("NULL");
//                    } else {
//                        System.out.println("存在");
//                        resultSet = stmt.executeQuery(sql);
//                        callback.onDataChange(resultSet);//回调给activity页面
//                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
//                    try {
//                        if (stmt != null)
//                            stmt.close();
//                        if (conn != null)
//                            conn.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }).start();
    }


//    public ResultSet USE_SQL(String sql) {
//        try {
//            resultSet = stmt.executeQuery(sql);
//            return resultSet;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}



