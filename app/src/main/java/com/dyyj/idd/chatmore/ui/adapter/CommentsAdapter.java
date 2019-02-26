package com.dyyj.idd.chatmore.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.model.network.result.UserSquareCommentReplyResult;
import com.dyyj.idd.chatmore.ui.user.activity.PicBigActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * desc 评论列表
 *
 * @author zhangcx
 * 2018/12/26 10:44 AM
 */
public class CommentsAdapter extends BaseQuickAdapter<UserSquareCommentReplyResult.UserReply, BaseViewHolder> {

    private boolean mIsSelf;
    private OnHeadClickListener mHeadListener;
    private OnNbClickListener mNbListener;
    private OnPraiseClickListener mPraiseListener;
    private OnReplyClickListener mReplyListener;
    private OnChatClickListener mChatListener;

    public CommentsAdapter(int layoutResId, @Nullable List<UserSquareCommentReplyResult.UserReply> data) {
        super(layoutResId, data);
    }

    /**
     * 是否为自己发的贴子
     */
    public void setIsMyselfy(boolean isSelf) {
        this.mIsSelf = isSelf;
        notifyDataSetChanged();
    }

    /**
     * 头像点击
     */
    public void setHeadOnclik(OnHeadClickListener listener) {
        this.mHeadListener = listener;
    }

    /**
     * 置为神评论点击
     */
    public void setOnNbClickListener(OnNbClickListener listener) {
        this.mNbListener = listener;
    }

    /**
     * 点赞
     */
    public void setOnPraiseClickListener(OnPraiseClickListener listener) {
        this.mPraiseListener = listener;
    }

    /**
     * 私聊点击
     */
    public void setOnChatClickListener(OnChatClickListener listener) {
        this.mChatListener = listener;
    }

    /**
     * 评论点击回复
     */
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.mReplyListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserSquareCommentReplyResult.UserReply item) {
        TextView tv_tag = helper.getView(R.id.tv_item_comments_tag);
        ImageView iv_head = helper.getView(R.id.iv_item_comments_head);
        TextView tv_time = helper.getView(R.id.tv_item_comment_time);
        TextView tv_nice = helper.getView(R.id.tv_item_comments_nice);
        TextView tv_chat = helper.getView(R.id.tv_item_comments_chat);
        TextView tv_name = helper.getView(R.id.tv_item_comments_name);
        RelativeLayout rl_conent = helper.getView(R.id.rl_item_comment_conent);
        TextView tv_comment = helper.getView(R.id.tv_item_comments_comment);
        GridView gv_pic = helper.getView(R.id.gv_item_comment_pic);
        ImageView iv_praise = helper.getView(R.id.iv_item_comments_praise);
        TextView tv_praise = helper.getView(R.id.tv_item_comments_praise_num);

        if (mIsSelf) {
            tv_nice.setVisibility(View.VISIBLE);
        } else {
            tv_nice.setVisibility(View.INVISIBLE);
        }
        ImageLoader.loadHead(iv_head, item.getAvatar());
        tv_name.setText(item.getReplyUserNickname());
        tv_praise.setText(item.getReplyAgreeNum());
        tv_time.setText(DateFormatter.timeToDate(item.getReplyTimestamp()));
        //回复状态
        if (!TextUtils.isEmpty(item.getReplyToUserNickname())) {
            tv_tag.setVisibility(View.INVISIBLE);
            String content = mContext.getResources().getString(R.string.comments_content);
            content = String.format(content, item.getReplyToUserNickname(), item.getReplyContent());
            tv_comment.setText(Html.fromHtml(content));
        } else {
            tv_tag.setVisibility(View.VISIBLE);
            tv_comment.setText(item.getReplyContent());
        }
        //置为神评论的状态
        if (TextUtils.equals("0", item.getReplyNb())) {
            tv_nice.setBackgroundResource(R.drawable.bg_comment_nice_add);
            tv_nice.setTextColor(mContext.getResources().getColor(R.color.color_comment_nice_add));
            tv_nice.setText(mContext.getResources().getString(R.string.comments_nice_add));
            tv_tag.setBackgroundResource(R.drawable.bg_comment_floor);
            tv_tag.setText(item.getFloor() + "楼");
        } else {
            tv_nice.setBackgroundResource(R.drawable.bg_comment_nice_cancle);
            tv_nice.setTextColor(mContext.getResources().getColor(R.color.color_comment_nice_cancle));
            tv_nice.setText(mContext.getResources().getString(R.string.comments_nice_cancle));
            tv_tag.setBackgroundResource(R.drawable.bg_comments_nice);
            tv_tag.setText("神评论");
        }
        List<String> images = item.getReplyImage();
        if (images.size() > 0) {
            gv_pic.setVisibility(View.VISIBLE);
            ImageAdapter imageAdapter = new ImageAdapter(mContext, images);
            gv_pic.setAdapter(imageAdapter);
        } else {
            gv_pic.setVisibility(View.GONE);
        }

        gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicBigActivity.Companion.start(mContext, (ArrayList<String>) images, position);
            }
        });
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeadListener.onClick(item.getReplyUserId());
            }
        });
        tv_nice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNbListener.onClick(helper.getAdapterPosition() - 1);
            }
        });
        iv_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPraiseListener.onClick(helper.getAdapterPosition() - 1);
            }
        });
        rl_conent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyListener.onClick(helper.getAdapterPosition() - 1);
            }
        });
        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mChatListener.onClick(helper.getAdapterPosition() - 1);
                mChatListener.onClick(item.getReplyUserId());
                EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_privatechat2", item.getReplyUserId()));
            }
        });
    }


    public interface OnHeadClickListener {
        void onClick(String userId);
    }

    public interface OnNbClickListener {
        void onClick(int position);
    }

    public interface OnPraiseClickListener {
        void onClick(int position);
    }

    public interface OnReplyClickListener {
        void onClick(int position);
    }

    public interface OnChatClickListener {
        //void onClick(int position);
        void onClick(String userId);
    }
}
