package com.example.administrator.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.graduationproject.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-2.
 */

public class account_data_adapter extends RecyclerView.Adapter<account_data_adapter.ViewHolder> {
    public ArrayList<account_data> mData;

    public account_data_adapter(ArrayList<account_data> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<account_data> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static account_data_adapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击
    static account_data_adapter.OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按
    public void setOnItemClickListener(account_data_adapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(account_data_adapter.OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    @Override
    public account_data_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item, parent, false);
        // 实例化viewholder
        account_data_adapter.ViewHolder viewHolder = new account_data_adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(account_data_adapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.account_name.setText(mData.get(position).name);
        holder.like.setText(mData.get(position).like);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView account_name;
        TextView like;

        public ViewHolder(View itemView) {
            super(itemView);
            account_name = (TextView) itemView.findViewById(R.id.account_name);
            like = (TextView) itemView.findViewById(R.id.like);

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
