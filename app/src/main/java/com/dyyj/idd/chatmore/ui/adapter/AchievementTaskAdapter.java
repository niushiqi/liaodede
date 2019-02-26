package com.dyyj.idd.chatmore.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.AchievementTaskResult;
import com.dyyj.idd.chatmore.weiget.HorizontalProgressBar;

import java.util.List;

public class AchievementTaskAdapter extends BaseQuickAdapter<AchievementTaskResult.Data.AchievementTaskData, BaseViewHolder> {
    public AchievementTaskAdapter(@Nullable List<AchievementTaskResult.Data.AchievementTaskData> data) {
        super(R.layout.item_achievement_task, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AchievementTaskResult.Data.AchievementTaskData item) {

        StringBuilder taskCash = new StringBuilder();
        if (Float.parseFloat(item.getTaskCash()) > 0) {
            taskCash.append("现金" + item.getTaskCash() + "元 ");
        }
        if (item.getTaskCoin() > 0) {
            if (taskCash.toString().length() > 0)
                taskCash.append("， ");
            taskCash.append("金币" + item.getTaskCoin() + "枚 ");
        }

        helper.setText(R.id.tv_task_rank, item.getTaskSequence() + "")
                .setText(R.id.tv_task_name, item.getTaskCategory())
                .setText(R.id.tv_task_money, "奖励：" + taskCash)
                .setText(R.id.tv_content, item.getTaskContent())
                .setText(R.id.tv_progress, item.getTaskCompleteNum() + "/" + item.getTaskNum());

        HorizontalProgressBar progress = helper.getView(R.id.horizontal_progress_bar);
        progress.setMax(item.getTaskNum());
        progress.setProgress(item.getTaskCompleteNum());


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
