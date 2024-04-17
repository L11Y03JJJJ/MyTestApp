package com.example.upton_app.bottom_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.upton_app.MainActivity;
import com.example.upton_app.ManageMysql.MysqlConnection;
import com.example.upton_app.MyRecycleViewAdapter;
import com.example.upton_app.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Context mParam2;
    View rootView;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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
            mParam2 = (Context) getActivity().getApplicationContext();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_product, container, false);
        }

        initView();
        return rootView;
        //return inflater.inflate(R.layout.fragment_product, container, false);
    }

    private void initView() {

    }



//    private void ProLoadMysql() { //生产加载信息
//        //连接数据库操作
//        mysqlConnection = new MysqlConnection();
//        mysqlConnection.Connectionsql("SELECT * FROM p701399.orgdatalist order by id desc limit 100 ");
//        mysqlConnection.setMysqlCallback(new MysqlConnection.Callback() { //回调 接收到查到的数据信息
//            @Override
//            public void onDataChange(ResultSet data) {
//                try {
//                    itemsdata = new ArrayList<>();
//                    while (data.next()) {
//                        itemsdata.add(data.getString("Barcode"));
//                    }
//
////                    recyclerView = rootView.findViewById(R.id.id_product_recycleview);
////                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mParam2, 2, RecyclerView.VERTICAL, false);
////                    MyRecycleViewAdapter myRecycleViewAdapter = new MyRecycleViewAdapter(itemsdata,mParam2); // 必须要这样 否则点击事件无法出发
////
////
////                    recyclerView.setLayoutManager(gridLayoutManager);//设置
////                    recyclerView.setAdapter(myRecycleViewAdapter);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}