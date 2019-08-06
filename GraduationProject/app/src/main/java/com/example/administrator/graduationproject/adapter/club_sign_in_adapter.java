package com.example.administrator.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.graduationproject.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-20.
 */

public class club_sign_in_adapter extends RecyclerView.Adapter<club_sign_in_adapter.ViewHolder>  {
    public ArrayList<club_sign_in> mData;

    public club_sign_in_adapter(ArrayList<club_sign_in> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<club_sign_in> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static club_sign_in_adapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击
    static club_sign_in_adapter.OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按
    public void setOnItemClickListener(club_sign_in_adapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(club_sign_in_adapter.OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    @Override
    public club_sign_in_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_sign_in_item, parent, false);
        // 实例化viewholder
        club_sign_in_adapter.ViewHolder viewHolder = new club_sign_in_adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(club_sign_in_adapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.club_name.setText(mData.get(position).name);
        holder.time.setText(mData.get(position).time);
        holder.state.setText(mData.get(position).state);
        holder.message.setText(mData.get(position).message);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView club_name;
        TextView time;
        TextView state;
        TextView message;


        public ViewHolder(View itemView) {
            super(itemView);
            club_name = (TextView) itemView.findViewById(R.id.club_name);
            time = (TextView) itemView.findViewById(R.id.time);
            state = (TextView) itemView.findViewById(R.id.state);
            message = (TextView) itemView.findViewById(R.id.message);

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
