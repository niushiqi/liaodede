package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.user.activity.PicBigActivity;
import com.dyyj.idd.chatmore.utils.DeviceUtils;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;

import java.util.ArrayList;
import java.util.List;

public class CirclePhotoAdapter extends RecyclerView.Adapter<CirclePhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> mData;
    private String id;

    public CirclePhotoAdapter(Context context, List<String> mData, String id) {
        this.context = context;
        this.mData = mData;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamics_photo, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Glide.with(context).load(mData.get(position)).asBitmap().transform(new RoundedCornersTransformation(context, (int) DeviceUtils.dp2px(context.getResources(), 5), 0)).placeholder(R.drawable.ic_photo_default).error(R.drawable.ic_photo_default).into(holder.ivPhoto);
        int wh = (int) ((DeviceUtils.getScreenWidth(context.getResources()) - DeviceUtils.dp2px(context.getResources(), 66F)) / 3);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(wh, wh);
        params.topMargin = (int) DeviceUtils.dp2px(context.getResources(), 4);
        holder.itemView.setLayoutParams(params);
        holder.itemView.setOnClickListener(v -> {
            PicBigActivity.Companion.start(context, (ArrayList<String>) mData, position);
            EventTrackingUtils.joinPoint(new EventBeans("ck_messpage_viewpicture", id));
        });
//        RequestOptions requestOption = new RequestOptions();
//                //RequestOptions.bitmapTransform(new RoundedCornersTransformation((int) DeviceUtils.dp2px(context.getResources(), 5), 0));
//        requestOption.error(R.drawable.ic_default_img).placeholder(R.drawable.ic_default_img);
//        Glide.with(context).load(mData.get(position)).apply(requestOption).into(holder.ivPhoto);
        Glide.with(context).load(mData.get(position)).asBitmap().crossFade().error(
                R.drawable.ic_default_img).placeholder(R.drawable.ic_default_img).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }
}
