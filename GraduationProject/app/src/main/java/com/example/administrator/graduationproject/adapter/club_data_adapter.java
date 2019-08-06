package com.example.administrator.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.administrator.graduationproject.R;

public class club_data_adapter extends RecyclerView.Adapter<club_data_adapter.ViewHolder>{
    public ArrayList<club_data> mData;

    public club_data_adapter(ArrayList<club_data> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<club_data> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击
    static OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    @Override
    public club_data_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_item, parent, false);
        // 实例化viewholder
        club_data_adapter.ViewHolder viewHolder = new club_data_adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(club_data_adapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.club_name.setText(mData.get(position).name);
        holder.club_ownername.setText(mData.get(position).ownername);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView club_name;
        TextView club_ownername;


        public ViewHolder(View itemView) {
            super(itemView);
            club_name = (TextView) itemView.findViewById(R.id.club_name);
            club_ownername = (TextView) itemView.findViewById(R.id.club_owner);

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
