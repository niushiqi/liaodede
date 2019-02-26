package com.dyyj.idd.chatmore.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.PayHistoryResult;
import com.dyyj.idd.chatmore.utils.DateUtils;

import java.util.List;

/**
 * Created by mlb on 2018/10/1.
 **/

public class PayHistoryAdapter extends BaseQuickAdapter<PayHistoryResult.recordData, BaseViewHolder> {


    public PayHistoryAdapter(@Nullable List<PayHistoryResult.recordData> data) {
        super(R.layout.item_withdraw_record,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PayHistoryResult.recordData item) {

        helper.setText(R.id.tv_time,item.getTimeTip());

        LinearLayout myLayout = helper.getView(R.id.ll_layout);
        myLayout.removeAllViews();
        for (int i = 0;i < item.getList().size();i++) {

            View itemlayout = LayoutInflater.from(mContext).inflate(R.layout.item_pay_history,null);
            if (item.getList().get(i).getOrderDesc().contains("得得豆")) {
                ((ImageView)itemlayout.findViewById(R.id.iv_alipay)).setImageResource(R.drawable.ic_proud_peas);
            }else if(item.getList().get(i).getOrderDesc().contains("魔石")){
                ((ImageView)itemlayout.findViewById(R.id.iv_alipay)).setImageResource(R.drawable.img_moshi);
            }else{
                ((ImageView)itemlayout.findViewById(R.id.iv_alipay)).setImageResource(R.drawable.ic_match_card);
            }
            ( (TextView)itemlayout.findViewById(R.id.tv_pay_num1)).setText("支付 "+item.getList().get(i).getOrderPrice()+"元");
            ( (TextView)itemlayout.findViewById(R.id.tv_pay_num)).setText(item.getList().get(i).getOrderDesc());
            ( (TextView)itemlayout.findViewById(R.id.tv_time)).setText(DateUtils.getStrTime("HH:mm",item.getList().get(i).getOrderTime()));

                String payStatus = item.getList().get(i).getOrderStatus();
                if ("0".equals(payStatus)) {
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setText("处理中");
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setTextColor(Color.parseColor("#00A9F0"));
                }else if("1".equals(payStatus)){
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setText("支付中");
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setTextColor(Color.parseColor("#00A9F0"));
                }else if("2".equals(payStatus)){
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setText("已完成");

                    ((TextView) itemlayout.findViewById(R.id.tv_pay_status)).setTextColor(Color.parseColor("#999999"));
                }else if("3".equals(payStatus)){
                    ((TextView) itemlayout.findViewById(R.id.tv_pay_status)).setText("支付失败");
                    ( (TextView)itemlayout.findViewById(R.id.tv_pay_status)).setTextColor(Color.parseColor("#ff0000"));

                }


            myLayout.addView(itemlayout);

        }
    }

}

