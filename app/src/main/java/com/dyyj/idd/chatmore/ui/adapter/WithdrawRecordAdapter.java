package com.dyyj.idd.chatmore.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.WithdrawRecordResult;
import com.dyyj.idd.chatmore.utils.DateUtils;

import java.util.List;

/**
 * Created by xddCompany on 2018/10/1.
 */

public class WithdrawRecordAdapter extends BaseQuickAdapter<WithdrawRecordResult.recordData, BaseViewHolder> {


    public WithdrawRecordAdapter(@Nullable List<WithdrawRecordResult.recordData> data) {
        super(R.layout.item_withdraw_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WithdrawRecordResult.recordData item) {

        helper.setText(R.id.tv_time, item.getTimeTip());

        LinearLayout myLayout = helper.getView(R.id.ll_layout);
        myLayout.removeAllViews();
        for (int i = 0; i < item.getList().size(); i++) {

            View itemlayout = LayoutInflater.from(mContext).inflate(R.layout.item_withdraw_record2, null);
            ((TextView) itemlayout.findViewById(R.id.tv_alipay_num)).setText("支付宝账号  " + item.getList().get(i).getApply_alipay_account());
            ((TextView) itemlayout.findViewById(R.id.tv_withdraw_num)).setText("提现金额： " + item.getList().get(i).getApply_amount());
            ((TextView) itemlayout.findViewById(R.id.tv_time)).setText(DateUtils.getStrTime("HH:mm", item.getList().get(i).getApply_time()));
            if ("0".equals(item.getList().get(i).getApply_status())) {
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("审核中");
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#00A9F0"));
            } else if ("1".equals(item.getList().get(i).getApply_status())) {
                String payStatus = item.getList().get(i).getApply_pay_status();
                if ("0".equals(payStatus)) {
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("提现中");
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#00A9F0"));
                } else if ("1".equals(payStatus)) {
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("已完成");
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#999999"));
                } else if ("2".equals(payStatus)) {
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("提现失败");
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#ff0000"));
                } else if ("3".equals(payStatus)) {
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("涉嫌违规，已冻结");
                    ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#fe7575"));
                }
            } else if ("2".equals(item.getList().get(i).getApply_status())) {
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("未通过审核");
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#999999"));
            } else if ("3".equals(item.getList().get(i).getApply_status())) {
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setText("涉嫌违规已冻结");
                ((TextView) itemlayout.findViewById(R.id.tv_withdraw_status)).setTextColor(Color.parseColor("#fe7575"));
            }

            myLayout.addView(itemlayout);

        }
    }

}

