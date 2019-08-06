package com.example.administrator.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.graduationproject.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-22.
 */

public class message_data_adapter extends RecyclerView.Adapter<message_data_adapter.ViewHolder> {
    public ArrayList<message_data> mData;

    public message_data_adapter(ArrayList<message_data> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<message_data> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static message_data_adapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击
    static message_data_adapter.OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按
    public void setOnItemClickListener(message_data_adapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(message_data_adapter.OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    @Override
    public message_data_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        // 实例化viewholder
        message_data_adapter.ViewHolder viewHolder = new message_data_adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(message_data_adapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.time.setText(mData.get(position).time);
        if (mData.get(position).sendorget.equals("0")){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(mData.get(position).content);
        }
        else if (mData.get(position).sendorget.equals("1")){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(mData.get(position).content);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView time;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout=(LinearLayout) itemView.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout) itemView.findViewById(R.id.right_layout);
            time = (TextView) itemView.findViewById(R.id.time);
            leftMsg=(TextView) itemView.findViewById(R.id.left_msg);
            rightMsg=(TextView) itemView.findViewById(R.id.right_msg);

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
