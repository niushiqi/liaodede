package com.dyyj.idd.chatmore.ui.user.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.model.network.result.UserSquareDynamicListResult;
import com.dyyj.idd.chatmore.ui.adapter.DynamicSquareAdapter;
import com.dyyj.idd.chatmore.ui.user.activity.CommentsActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.functions.Consumer;

/**
 * desc 动态广场
 *
 * @author zhangcx
 * 2018/12/26 5:30 PM
 */
public class DynamicSquareFragment extends Fragment {
    ListView lv_square;
    SwipeRefreshLayout refreshLayout;

    List<UserSquareDynamicListResult.UserMessage> mData;
    private DynamicSquareAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_square, container, false);
        initView(view);
        setListener();
        return view;
    }

    private void initView(View view) {
        lv_square = view.findViewById(R.id.lv_square);
        refreshLayout = view.findViewById(R.id.refresh_square);
        mData = new ArrayList<>();
        mAdapter = new DynamicSquareAdapter(getActivity(), mData);
        lv_square.setAdapter(mAdapter);
        getData();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this::getData);

        lv_square.setOnItemClickListener((parent, view, position, id) -> {
            String mId;//贴子id
            if (TextUtils.equals(mData.get(position).getDynamicType(), "1")) {//贴子类型
                mId = mData.get(position).getCommentId();
            } else {//其他类型 评论 点赞等
                mId = mData.get(position).getSquareTopicCommentId();
            }
            CommentsActivity.launch(getActivity(), mId, mData.get(position).getId());
        });
        lv_square.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteTip(position);
                return true;
            }
        });
    }

    /**
     * 删除动态弹窗提示
     */
    private void showDeleteTip(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("确定删除该动态")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReply(position);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    /**
     * 删除动态
     */
    @SuppressLint("CheckResult")
    private void deleteReply(int position) {
        UserSquareDynamicListResult.UserMessage message = mData.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .deleteDynamic(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        message.getId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        mData.remove(position);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }


    @SuppressLint("CheckResult")
    private void getData() {
        ChatApp.Companion.getInstance().getDataRepository()
                .getMySquareDynamicList(ChatApp.Companion.getInstance().getDataRepository().getUserid())
                .subscribe((Consumer<UserSquareDynamicListResult>) result -> {
                    refreshLayout.setRefreshing(false);
                    if (result.getErrorCode() == 200) {
                        mData.clear();
                        mData.addAll(Objects.requireNonNull(result.getData()));
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

}
