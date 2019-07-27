package com.lz233.qrtrans;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.MyViewHolder> {

    //private static final String TAG = "MyAdapter";
    private List<String> datas;
    private LayoutInflater inflater;

    public ResultListAdapter(Context context) {
        List<String> datas = new ArrayList<String>();
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rv_main_item_title);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.e(TAG, "create a new item");
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.rv_main_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Log.e(TAG, "set value to item:" + position);
        holder.title.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addData(int position, String content){
        datas.add(position,content);
    }

    public void addData(String content){
        datas.add(getItemCount(),content);
    }

    public void removeData(int position){
        datas.remove(position);
    }

}