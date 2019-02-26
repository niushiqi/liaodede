package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.ui.user.activity.PicBigActivity;
import com.dyyj.idd.chatmore.utils.DeviceUtils;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PlazaPhotoAdapter extends RecyclerView.Adapter<PlazaPhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> mData;
    private String id;

    public PlazaPhotoAdapter(Context context, List<String> mData, String id) {
        this.context = context;
        this.mData = mData;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plaza_photo, null));
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
        String imgUrl = mData.get(position);
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.toLowerCase().endsWith(".gif")) {
            holder.tv.setVisibility(View.VISIBLE);
        } else {
            holder.tv.setVisibility(View.INVISIBLE);
        }

        Context context = holder.ivPhoto.getContext();
        Glide.with(this.context).load(imgUrl)
                .asBitmap().crossFade()
                .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 10, 0))
                .error(R.drawable.ic_default_img).placeholder(R.drawable.ic_default_img)
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        View tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
