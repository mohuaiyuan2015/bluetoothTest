package com.example.yf_04.bluetoothtest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yf_04.bluetoothtest.Communicate;
import com.example.yf_04.bluetoothtest.bean.MDevice;

import com.example.yf_04.bluetoothtest.R;

import java.util.List;

/**
 * Created by YF-04 on 2017/7/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "MyAdapter";
    private Context context;
    private List<MDevice> list;

    private OnItemClickListener onItemClickListener;

    public MyAdapter(Context context,List<MDevice>list){
        this.context=context;
        this.list=list;

    }

    public void clear() {
        list.clear();
    }



    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDevName;
        TextView tvDevSignal;
        TextView tvDevMac;

        public ViewHolder(final View view) {
            super(view);
            tvDevName= (TextView) view.findViewById(R.id.tv_dev_name);
            tvDevSignal= (TextView) view.findViewById(R.id.tv_dev_signal);
            tvDevMac= (TextView) view.findViewById(R.id.tv_dev_mac);
        }
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (Integer) v.getTag());
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        ViewHolder cellViewHolder = (ViewHolder) holder;
        cellViewHolder.itemView.setTag(position);
        MDevice mDevice=list.get(position);
        holder.tvDevName.setText(mDevice.getDevice().getName());
        holder.tvDevSignal.setText(mDevice.getRssi()+"dBm");
        holder.tvDevMac.setText(mDevice.getDevice().getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
