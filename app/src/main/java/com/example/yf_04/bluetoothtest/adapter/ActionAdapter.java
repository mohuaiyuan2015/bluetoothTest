package com.example.yf_04.bluetoothtest.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yf_04.bluetoothtest.R;
import com.example.yf_04.bluetoothtest.Utils.Utils;
import com.example.yf_04.bluetoothtest.bean.BasicAction;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by YF-04 on 2017/7/31.
 */

public class ActionAdapter  extends RecyclerView.Adapter<ActionAdapter.ViewHolder>{

    private static final String TAG = "ActionAdapter";

    private List<BasicAction> list;
    private Context context;

    private MyOnItemClickListener myOnItemClickListener;

    public MyOnItemClickListener getMyOnItemClickListener() {
        return myOnItemClickListener;
    }

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.myOnItemClickListener = myOnItemClickListener;
    }

    public interface MyOnItemClickListener {
        void OnItemClickListener(View view, int position);
    }


    public ActionAdapter(Context context,List<BasicAction> basicActions){
        this.list=basicActions;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.action_item_layout,parent,false);
        ViewHolder viewHolder =new ViewHolder(view);
        viewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (myOnItemClickListener != null) {
                    myOnItemClickListener.OnItemClickListener(v, (Integer) v.getTag());
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder viewHolder =(ViewHolder) holder;
        viewHolder.btnAction.setTag(position);
        BasicAction basicAction=list.get(position);
        holder.btnAction.setText(Utils.getStringByName(context,basicAction.getTextId()));

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }




    class ViewHolder extends  RecyclerView.ViewHolder{

        private Button btnAction;

        public ViewHolder(View itemView) {
            super(itemView);
            btnAction= (Button) itemView.findViewById(R.id.btnAction);
        }
    }


}