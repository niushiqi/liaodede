package com.dyyj.idd.chatmore.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyyj.idd.chatmore.R;

/**
 * Created by test on 2017/11/22.
 */


public class CallOutgoingBannerAdapter extends RecyclerView.Adapter<CallOutgoingBannerAdapter.MzViewHolder> {


    private int[] images = {R.drawable.call_outgoing_banner1,R.drawable.call_outgoing_banner2,R.drawable.call_outgoing_banner3,R.drawable.call_outgoing_banner4,R.drawable.call_outgoing_banner5};
    private boolean isVoiceMatch;
    public CallOutgoingBannerAdapter(boolean isVoiceMatch) {
        this.isVoiceMatch = isVoiceMatch;
    }

    @Override
    public CallOutgoingBannerAdapter.MzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MzViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_outgoing_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(CallOutgoingBannerAdapter.MzViewHolder holder, final int position) {
        final int P = position % images.length;
        if (P == 0 && isVoiceMatch){
            ((ImageView) holder.imageView).setImageResource(R.drawable.call_outgoing_banner6);
        } else {
            ((ImageView) holder.imageView).setImageResource(images[P]);
        }
    }

    @Override
    public int getItemCount() {
       return images.length;
    }


    class MzViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MzViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

}
