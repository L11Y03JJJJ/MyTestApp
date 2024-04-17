package com.example.upton_app;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.upton_app", appContext.getPackageName());
    }
    Connection conn = null;
    Statement stmt = null;
    ResultSet resultSet = null;
    @Test
    public void Test2() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动程序");
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("成功连接到数据库");
            stmt = conn.createStatement();
            // = stmt.executeQuery("SELECT * FROM orgdatalist order by id desc limit 10");
//            while (rs.next()) {
//                System.out.println(rs.getString("Barcode"));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    private static final String URL = "jdbc:mysql://192.168.3.20:3306/p701399?useSSL=false&serverTimezone=UTC";

    private static final String USERNAME = "sa";

    private static final String PASSWORD = "6660";

    public static Connection getConnection() {
        Connection connection = null;
        try {

// 加载驱动

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return connection;

    }

    public static void mn(String[] args) {

// 连接Mysql数据库

        Connection connection = getConnection();

// 执行SQL查询语句

        Statement statement = null;

        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM orgdatalist");

            while (resultSet.next()) {

                int id = resultSet.getInt("BarCode”");

                Log.e("TAG", "mn: " + id);
                System.out.println(id);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try {

                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException e) {

                e.printStackTrace();

            }

        }

        while (true) {
        }
    }


}
