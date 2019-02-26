package com.dyyj.idd.chatmore.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.LevelTaskResult;

import java.util.List;

public class LevelTaskAdapter extends BaseQuickAdapter<LevelTaskResult.Data.LevelTaskData, BaseViewHolder> {
    public LevelTaskAdapter(@Nullable List<LevelTaskResult.Data.LevelTaskData> data) {
        super(R.layout.item_level_task, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LevelTaskResult.Data.LevelTaskData item) {

        //tv_level_text
        helper.setText(R.id.tv_user_level, "" + item.getLevel())
                .setText(R.id.tv_level_text, item.getLevel() + "级")
                .setText(R.id.tv_task_name, item.getTaskCondition().getTaskName());

        if (item.getCurrentExperience() >= item.getLevelExperience()) {
            helper.setText(R.id.tv_level_text2, "经验值已满");
        } else {
            helper.setText(R.id.tv_level_text2, "经验值不足");
        }

        if (item.getTaskCondition().getTaskStatus() == 0) {
            // 未完成
            helper.getView(R.id.tv_task_status).setBackgroundResource(R.drawable.shape_task_status);
            helper.setText(R.id.tv_task_status, "去完成");
            helper.setTextColor(R.id.tv_task_status, Color.parseColor("#884D00"));
        } else {
            // 已完成
            helper.getView(R.id.tv_task_status).setBackgroundResource(0);
            helper.setText(R.id.tv_task_status, "已完成");
            helper.setTextColor(R.id.tv_task_status, Color.parseColor("#FFAC9E"));
        }

        //不等于空或者不等于""  则显示
        if (item.getTaskCondition().getTaskDesc() != null || !"".equals(item.getTaskCondition().getTaskDesc())) {
            helper.setText(R.id.tv_describe_rules, item.getTaskCondition().getTaskDesc());
        }

        helper.addOnClickListener(R.id.tv_task_status);

    }
}
