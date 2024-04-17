package com.example.upton_app.bottom_fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.upton_app.ManageMysql.MysqlConnection;
import com.example.upton_app.R;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFormsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFormsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportFormsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFormsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFormsFragment newInstance(String param1, String param2) {
        ReportFormsFragment fragment = new ReportFormsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_report_forms, container, false);
        }
        initView();
        return rootView;
//        return inflater.inflate(R.layout.fragment_report_forms, container, false);
    }

    private WebView webView;

    private void initView() {
        //  ReportLoadMysql();
        //   webView.loadUrl("file:///android_asset/html/echartstest1.html");

        webView = rootView.findViewById(R.id.testidss);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/html/index.html");
    }

//    private MysqlConnection mysqlConnection;
//    private List<Object> list;
//
//    private void ReportLoadMysql() { //报表查询
//        mysqlConnection = new MysqlConnection();
//        mysqlConnection.Connectionsql("SELECT * FROM p701399.orgdatalist order by id desc limit 100 ");
//        mysqlConnection.setMysqlCallback(new MysqlConnection.Callback() { //回调 接收到查到的数据信息
//            @Override
//            public void onDataChange(ResultSet data) {
//                list = MysqlRowsColumnsNames(data); // 行数  列数  列的名称      List<Object>
//                // 件数 ： count
//                // 合格数： ispass
//                // 不合格数： ispass=0
//                try {
//                    ResultSetMetaData resultSetMetaData = data.getMetaData();
//                    int oknum = 0;
//                    int ngnum = 0;
//                    for (int i = 0; i <= (int) list.get(0) - 1; i++) {
//                        if (data.getString("IsPass").contains("1")) {
//                            oknum += 1;
//                        } else {
//                            ngnum += 1;
//                        }
//                        data.next();
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
////                Log.e("AAAAAAAAAAAAAaa", "MYSQL查出数据: " + list.get(0) + "   " + list.get(1) + "     " + list.get(2));
//            }
//        });
//    }

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
}