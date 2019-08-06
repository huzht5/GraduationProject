package com.example.administrator.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.graduationproject.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-21.
 */

public class account_message_adapter extends RecyclerView.Adapter<account_message_adapter.ViewHolder> {
    public ArrayList<account_message> mData;

    public account_message_adapter(ArrayList<account_message> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<account_message> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static account_message_adapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击
    static account_message_adapter.OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按
    public void setOnItemClickListener(account_message_adapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(account_message_adapter.OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    @Override
    public account_message_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fg_message_item, parent, false);
        // 实例化viewholder
        account_message_adapter.ViewHolder viewHolder = new account_message_adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(account_message_adapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.account_name.setText(mData.get(position).name);
        String mtime = mData.get(position).time.substring(12, 17);
        holder.time.setText(mtime);
        holder.message.setText(mData.get(position).message);
        if (mData.get(position).state.equals("0")) holder.tip.setVisibility(View.INVISIBLE);
        else if (mData.get(position).state.equals("1")) holder.tip.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView account_name;
        TextView time;
        TextView message;
        ImageView tip;


        public ViewHolder(View itemView) {
            super(itemView);
            account_name = (TextView) itemView.findViewById(R.id.account_name);
            time = (TextView) itemView.findViewById(R.id.time);
            message = (TextView) itemView.findViewById(R.id.message);
            tip = (ImageView) itemView.findViewById(R.id.tip);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongItemClickListener != null) {
                        mOnLongItemClickListener.onLongItemClick(v, getAdapterPosition());

                    }
                    return true;
                }
            });
        }
    }
}
