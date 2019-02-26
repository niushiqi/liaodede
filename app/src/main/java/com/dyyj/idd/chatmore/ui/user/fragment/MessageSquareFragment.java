package com.dyyj.idd.chatmore.ui.user.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.model.network.result.UserSquareMessageListResult;
import com.dyyj.idd.chatmore.ui.adapter.SquareAdapter;
import com.dyyj.idd.chatmore.ui.dialog.PersonDialog;
import com.dyyj.idd.chatmore.ui.dialog.ReplyDialog;
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity;
import com.dyyj.idd.chatmore.ui.user.activity.CommentsActivity;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;
import static com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity.REQUEST_CODE_CHOOSE;

/**
 * desc
 *
 * @author zhangcx
 * 2018/12/26 2:06 PM
 */
public class MessageSquareFragment extends Fragment {
    ListView lv_square;
    SwipeRefreshLayout refreshLayout;

    private SquareAdapter mAdapter;
    private List<UserSquareMessageListResult.UserMessage> mData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_square, container, false);
        initView(view);
        setListener();
        return view;
    }

    private void initView(View view) {
        lv_square = view.findViewById(R.id.lv_square);
        refreshLayout = view.findViewById(R.id.refresh_square);
        mData = new ArrayList<>();
        mAdapter = new SquareAdapter(getActivity(), mData);
        lv_square.setAdapter(mAdapter);
        getData();
        clearUnread();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(() -> getData());

        lv_square.setOnItemClickListener((parent, view, position, id) -> {
            CommentsActivity.launch(getActivity(), mData.get(position).getSquareCommentId() + "",null);
        });
        mAdapter.setReplyClick(new SquareAdapter.OnReplyClickListener() {
            @Override
            public void onClick(int postion) {
                UserSquareMessageListResult.UserMessage message = mData.get(postion);
                new ReplyDialog(message.getSquareTopicId() + "", message.getSquareCommentId(),
                        message.getUserId(), message.getSquareReplyId(), message.getNickname())
                        .setShowBottom(true).show(getChildFragmentManager());
            }
        });
        /**
         * 条目头像点击
         */
        mAdapter.setHeadClick(id -> {
            PlazaSpaceActivity.Companion.start(getContext(), id,null);

//            PersonDialog personDialog = new PersonDialog(id);
//            personDialog
//                    .setOutCancel(false)
//                    .show(getChildFragmentManager());
        });
    }

    @SuppressLint("CheckResult")
    private void getData() {
        ChatApp.Companion.getInstance().getDataRepository()
                .getMySquareMessageList(ChatApp.Companion.getInstance().getDataRepository().getUserid())
                .subscribe((Consumer<UserSquareMessageListResult>) result -> {
                    refreshLayout.setRefreshing(false);
                    if (result.getErrorCode() == 200) {
                        mData.clear();
                        mData.addAll(Objects.requireNonNull(result.getData()));
                        mAdapter.notifyDataSetChanged();

                    }
                }, throwable -> refreshLayout.setRefreshing(false));
    }

    private void clearUnread() {
        ChatApp.Companion.getInstance().getDataRepository()
                .clearSquareUnReadMessage(ChatApp.Companion.getInstance().getDataRepository().getUserid())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        EventBus.getDefault().post(new ClearSquareUnMsgVM(result.getErrorCode() == 200));
                    }
                }, throwable -> refreshLayout.setRefreshing(false));
    }

    public static final class ClearSquareUnMsgVM {
        private boolean success;

        public final boolean getSuccess() {
            return this.success;
        }

        public ClearSquareUnMsgVM(boolean success) {
            this.success = success;
        }
    }
}
