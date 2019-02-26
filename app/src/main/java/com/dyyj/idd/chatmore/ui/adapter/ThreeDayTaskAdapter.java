package com.dyyj.idd.chatmore.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.ThreeDayTaskResult;

import java.util.List;

public class ThreeDayTaskAdapter extends BaseQuickAdapter<ThreeDayTaskResult.Data.ThreeDayTaskData, BaseViewHolder> {
    public ThreeDayTaskAdapter(@Nullable List<ThreeDayTaskResult.Data.ThreeDayTaskData> data) {
        super(R.layout.item_three_day_task, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThreeDayTaskResult.Data.ThreeDayTaskData item) {

        StringBuilder taskCash = new StringBuilder();
        if (Float.parseFloat(item.getTaskCash()) > 0) {
            taskCash.append(item.getTaskCash() + "现金");
        }
        if (item.getTaskCoin() > 0) {
            if (taskCash.toString().length() > 0)
                taskCash.append("， ");
            taskCash.append(item.getTaskCoin() + "金币");
        }
        if (item.getTaskStone() > 0) {
            if (taskCash.toString().length() > 0)
                taskCash.append("， ");
            taskCash.append(item.getTaskStone() + "魔石");
        }
        if (item.getTaskExperience() > 0) {
            if (taskCash.toString().length() > 0)
                taskCash.append("， ");
            taskCash.append(item.getTaskExperience() + "经验值");
        }

        helper.setText(R.id.tv_task_rank, item.getTaskSequence() + "")
                .setText(R.id.tv_task_name, item.getTaskContent())
                .setText(R.id.tv_task_money, taskCash.toString() + "");

        if (item.getTaskStatus() == 0) {
            // 未完成
            helper.getView(R.id.tv_task_status).setBackgroundResource(R.drawable.shape_task_status);
            helper.setText(R.id.tv_task_status, "去完成");
            helper.setTextColor(R.id.tv_task_status, Color.parseColor("#884D00"));
        } else if (item.getTaskStatus() == 1) {
            // 已完成
            helper.getView(R.id.tv_task_status).setBackgroundResource(R.drawable.shape_task_success_status);
            helper.setText(R.id.tv_task_status, "可领取");
            helper.setTextColor(R.id.tv_task_status, Color.parseColor("#884D00"));
        } else {
            // 已完成
            helper.getView(R.id.tv_task_status).setBackgroundResource(0);
            helper.setText(R.id.tv_task_status, "已入账");
            helper.setTextColor(R.id.tv_task_status, Color.parseColor("#FFAC9E"));
        }

        helper.addOnClickListener(R.id.tv_task_status);

    }
}
