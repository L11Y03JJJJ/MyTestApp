package com.example.upton_app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyViewHolder> {
    private List<String> mdata;
    private Context mcontext;
    private String TAG = "L11Y03JJJJ";

    public MyRecycleViewAdapter(List<String> datas, Context context) {
        this.mdata = datas;
        this.mcontext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //找到itemview 返回  ViewHolder
        View view = View.inflate(mcontext, R.layout.myrecycleviewitems, null); //先找到 myrecycleviewitems 布局
        return new MyViewHolder(view);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(@NonNull View itemView) { //此时itemView接收到的是item布局文件
            super(itemView);
            textView = itemView.findViewById(R.id.id_recycleviewitem_textview); //找到item布局文件中的textview
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecItemClick != null) {
                        onRecItemClick.ItemCLick(getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { //给每个item绑定holder 渲染到页面上
        holder.textView.setText(mdata.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mdata == null ? 0 : mdata.size();
    }

    // 点击监听事件的
    public void setRecycleItemCLickLister(OnRecycleItemClick lister) {
        onRecItemClick = lister;
    }

    private OnRecycleItemClick onRecItemClick;

    public interface OnRecycleItemClick {
        void ItemCLick(int position);
    }
}
