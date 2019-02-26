package com.dyyj.idd.chatmore.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.model.network.result.SpaceCircleReplyResult;
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;

import java.util.List;


/**
 * desc 好友圈评论列表
 *
 * @author zhangcx
 * 2018/12/26 10:44 AM
 */
public class SpaceCircleCommentsAdapter extends BaseQuickAdapter<SpaceCircleReplyResult.UserComment, BaseViewHolder> {

    private String detailUserID;
    private OnHeadClickListener mHeadListener;
    private OnReplyClickListener mReplyListener;
    private boolean isOwn;

    public SpaceCircleCommentsAdapter(int layoutResId, @Nullable List<SpaceCircleReplyResult.UserComment> data) {
        super(layoutResId, data);
    }

    /**
     * 是否为自己发的贴子
     */
    public void setIsMyselfy(String userID) {
        detailUserID = userID;
        String userid = ChatApp.Companion.getInstance().getDataRepository().getUserid();
        //我自己的帖子
        isOwn = TextUtils.equals(userid, detailUserID);

        notifyDataSetChanged();
    }

    /**
     * 头像点击
     */
    public void setHeadOnclik(OnHeadClickListener listener) {
        this.mHeadListener = listener;
    }

    /**
     * 评论点击回复
     */
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.mReplyListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, SpaceCircleReplyResult.UserComment item) {
        ImageView iv_head = helper.getView(R.id.iv_item_comments_head);
        TextView tv_time = helper.getView(R.id.tv_item_comment_time);
        TextView tv_nice = helper.getView(R.id.tv_item_comments_nice);
        TextView tv_chat = helper.getView(R.id.tv_item_comments_chat);
        TextView tv_name = helper.getView(R.id.tv_item_comments_name);
        RelativeLayout rl_conent = helper.getView(R.id.rl_item_comment_conent);
        TextView tv_comment = helper.getView(R.id.tv_item_comments_comment);
//        GridView gv_pic = helper.getView(R.id.gv_item_comment_pic);

        tv_time.setText(DateFormatter.timeToDate(item.getAdd_timestamp()));

        final String reply_user_id = item.getReply_user_id();
        String userid = ChatApp.Companion.getInstance().getDataRepository().getUserid();

        boolean isReplyMe = TextUtils.equals(userid, item.getReply_to_user_id());//回复我的
        boolean isMe = TextUtils.equals(userid, reply_user_id);//我评论的
        boolean isMananger = TextUtils.equals(detailUserID, reply_user_id);//贴主自己评论的

        tv_nice.setVisibility(View.INVISIBLE);
        tv_chat.setVisibility(View.INVISIBLE);


        if (isOwn) {
            if (!isMe) {
                tv_nice.setVisibility(View.VISIBLE);
                tv_chat.setVisibility(View.VISIBLE);
            }

        } else {
            if (isMananger || isReplyMe) {
                tv_nice.setVisibility(View.VISIBLE);
                tv_chat.setVisibility(View.VISIBLE);
            }
        }

        if (isOwn || isMe || isMananger) {
            ImageLoader.loadHead(iv_head, item.getAvatar());
            tv_name.setText(item.getNickname());
            iv_head.setClickable(true);
        } else {
            iv_head.setImageResource(R.drawable.ic_message_system_head);
            tv_name.setText(item.getReply_virtual_name());
            iv_head.setClickable(false);
        }

        String reply_to_user_nickname = "匿名";
        if (isOwn || isMe || isReplyMe) {
            reply_to_user_nickname = item.getReply_to_user_nickname();
        }

        //回复状态
        if (!"0".equals(item.getReply_id())) {
            String content = mContext.getResources().getString(R.string.comments_content);
            content = String.format(content, reply_to_user_nickname, item.getReply_message());
            tv_comment.setText(Html.fromHtml(content));
        } else {
            tv_comment.setText(item.getReply_message());
        }

//        List<String> images = item.getReplyImage();
//        if (images.size() > 0) {
//            gv_pic.setVisibility(View.VISIBLE);
//            ImageAdapter imageAdapter = new ImageAdapter(mContext, images);
//            gv_pic.setAdapter(imageAdapter);
//        } else {
//            gv_pic.setVisibility(View.GONE);
//        }
//
//        gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PicBigActivity.Companion.start(mContext, (ArrayList<String>) images, position);
//            }
//        });

        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeadListener.onClick(reply_user_id);
            }
        });
        tv_nice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyListener.onClick(helper.getAdapterPosition() - 1);
            }
        });
        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mChatListener.onClick(helper.getAdapterPosition() - 1);
                String nickname = item.getNickname();
                String avatar = item.getAvatar();
                ChatActivity.Companion.start(v.getContext(), reply_user_id, nickname, avatar, null);

                EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_privatechat2", reply_user_id));
            }
        });
    }


    public interface OnHeadClickListener {
        void onClick(String userId);
    }

    public interface OnReplyClickListener {
        void onClick(int position);
    }

//    public String getRandomString() {
//        char c = (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
//        char c2 = (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
//        return new String(new char[]{c, c2});
//    }


}
