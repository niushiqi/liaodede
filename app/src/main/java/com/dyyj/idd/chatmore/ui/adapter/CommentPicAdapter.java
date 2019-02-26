package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;

import java.util.List;
/**
  * desc 添加评论图片
  * @author  zhangcx
  * 2018/12/26 10:44 AM
  */
public class CommentPicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<String> mDatas;

    private OnDeleteListener mDelListener;

    public CommentPicAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    public void setDeleteListener(OnDeleteListener listener) {
        this.mDelListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        ImageLoader.loadPicCorner(holder.iv_pic, mDatas.get(position));
        holder.tv_num.setText(String.valueOf(position + 1));
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        ImageView iv_delete;
        TextView tv_num;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.iv_item_pic);
            iv_delete = itemView.findViewById(R.id.iv_item_pic_delete);
            tv_num = itemView.findViewById(R.id.tv_item_pic_num);
        }
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }
}
