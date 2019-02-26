package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.UserSquareMessageListResult;
import com.dyyj.idd.chatmore.ui.user.activity.CommentsActivity;
import com.dyyj.idd.chatmore.ui.user.activity.PicBigActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * desc 我的消息 广场数据
 *
 * @author zhangcx
 * 2018/12/27 2:20 PM
 */
public class SquareAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserSquareMessageListResult.UserMessage> mDatas;

    private OnHeadClickListener mHeadListener;
    private OnReplyClickListener mReplyListener;

    public SquareAdapter(Context context, List<UserSquareMessageListResult.UserMessage> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    public void setHeadClick(OnHeadClickListener listener) {
        this.mHeadListener = listener;
    }

    public void setReplyClick(OnReplyClickListener listener) {
        this.mReplyListener = listener;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_square, null);
            holder.iv_head = convertView.findViewById(R.id.iv_item_square_head);
            holder.iv_offical = convertView.findViewById(R.id.iv_item_square_offical);
            holder.tv_name = convertView.findViewById(R.id.tv_item_square_name);
            holder.tv_time = convertView.findViewById(R.id.tv_item_square_time);
            holder.iv_type = convertView.findViewById(R.id.iv_item_square_type);
            holder.tv_type = convertView.findViewById(R.id.tv_item_square_type);
            holder.tv_comments = convertView.findViewById(R.id.tv_item_square_comment_content);
            holder.iv_topic_pic = convertView.findViewById(R.id.iv_item_square_topic_pic);
            holder.tv_topic_title = convertView.findViewById(R.id.tv_item_square_topic_title);
            holder.tv_topic_desc = convertView.findViewById(R.id.tv_item_square_topic_desc);
            holder.rl_topic = convertView.findViewById(R.id.rl_item_square_topic);
            holder.gv_pic = convertView.findViewById(R.id.gv_item_square_comment_pic);
            holder.tv_reply = convertView.findViewById(R.id.tv_item_square_reply);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserSquareMessageListResult.UserMessage message = mDatas.get(position);
        ImageLoader.loadHead(holder.iv_head, message.getAvatar());
        holder.tv_name.setText(message.getNickname());
        holder.tv_time.setText(DateFormatter.timeToDate(message.getMessageTimestamp()));
        holder.tv_topic_title.setText(message.getCommentContent());
        holder.tv_topic_desc.setText("#" + message.getSquareTopicTitle() + "#");
        ImageLoader.loadPicCorner(holder.iv_topic_pic, message.getSquareTopicImage());
        if (message.getCommentImage().size() > 0) {
            ImageLoader.loadPicCorner(holder.iv_topic_pic, message.getCommentImage().get(0));
        }
        switch (Objects.requireNonNull(message.getMessageType())) {
            case "1"://评论我的贴子
                holder.iv_offical.setVisibility(View.INVISIBLE);
                holder.iv_type.setVisibility(View.VISIBLE);
                holder.iv_type.setImageResource(R.drawable.ic_square_comments);
                holder.tv_type.setText(Html.fromHtml(mContext.getResources().getString(R.string.square_comments)));
                holder.tv_reply.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(message.getMessageContent())) {
                    holder.tv_comments.setVisibility(View.GONE);
                } else {
                    holder.tv_comments.setVisibility(View.VISIBLE);
                    holder.tv_comments.setText(message.getMessageContent());
                }
                //点击回复
                holder.tv_reply.setOnClickListener(v -> {
                    mReplyListener.onClick(position);
                });
                //显示评论图片
                List<String> commentImages = message.getReplyImage();
                if (commentImages.size() > 0) {
                    holder.gv_pic.setVisibility(View.VISIBLE);
                    ImageAdapter imageAdapter = new ImageAdapter(mContext, commentImages);
                    holder.gv_pic.setAdapter(imageAdapter);
                } else {
                    holder.gv_pic.setVisibility(View.GONE);
                }
                holder.gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PicBigActivity.Companion.start(mContext, (ArrayList<String>) commentImages, position);
                    }
                });
                break;
            case "2"://回复我的评论
                holder.iv_offical.setVisibility(View.INVISIBLE);
                holder.iv_type.setVisibility(View.VISIBLE);
                holder.iv_type.setImageResource(R.drawable.ic_square_comments);
                holder.tv_type.setText(Html.fromHtml(mContext.getResources().getString(R.string.square_comments)));
                holder.tv_reply.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(message.getMessageContent())) {
                    holder.tv_comments.setVisibility(View.GONE);
                } else {
                    holder.tv_comments.setVisibility(View.VISIBLE);
                    holder.tv_comments.setText(message.getMessageContent());
                }
                //点击回复
                holder.tv_reply.setOnClickListener(v -> {
                    mReplyListener.onClick(position);
                });
                //显示评论图片
                List<String> replyImages = message.getReplyImage();
                if (replyImages.size() > 0) {
                    holder.gv_pic.setVisibility(View.VISIBLE);
                    ImageAdapter imageAdapter = new ImageAdapter(mContext, replyImages);
                    holder.gv_pic.setAdapter(imageAdapter);
                } else {
                    holder.gv_pic.setVisibility(View.GONE);
                }
                holder.gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PicBigActivity.Companion.start(mContext, (ArrayList<String>) replyImages, position);
                    }
                });
                break;
            case "3"://点赞贴子
                holder.tv_reply.setVisibility(View.INVISIBLE);
                holder.tv_comments.setVisibility(View.GONE);
                holder.gv_pic.setVisibility(View.GONE);
                holder.iv_offical.setVisibility(View.INVISIBLE);
                holder.iv_type.setVisibility(View.VISIBLE);
                holder.iv_type.setImageResource(R.drawable.ic_square_praise);
                holder.tv_type.setText(Html.fromHtml(mContext.getResources().getString(R.string.square_praise)));
                break;
            case "4"://点赞我的评论
                holder.tv_reply.setVisibility(View.INVISIBLE);
                holder.tv_comments.setVisibility(View.GONE);
                holder.gv_pic.setVisibility(View.GONE);
                holder.iv_offical.setVisibility(View.INVISIBLE);
                holder.iv_type.setVisibility(View.VISIBLE);
                holder.iv_type.setImageResource(R.drawable.ic_square_praise);
                holder.tv_type.setText(Html.fromHtml(mContext.getResources().getString(R.string.square_praise_comment)));
                break;
            case "5"://贴主设置为神评论
                holder.tv_reply.setVisibility(View.INVISIBLE);
                holder.tv_comments.setVisibility(View.GONE);
                holder.gv_pic.setVisibility(View.GONE);
                holder.iv_offical.setVisibility(View.INVISIBLE);
                holder.iv_type.setVisibility(View.GONE);
                String format = String.format(mContext.getResources().getString(R.string.square_good_user), message.getNickname());
                holder.tv_type.setText(Html.fromHtml(format));
                break;
            case "6"://官方推为热门话题
                holder.tv_reply.setVisibility(View.INVISIBLE);
                holder.tv_comments.setVisibility(View.GONE);
                holder.gv_pic.setVisibility(View.GONE);
                holder.iv_offical.setVisibility(View.VISIBLE);
                holder.iv_type.setVisibility(View.GONE);
                holder.tv_type.setText(Html.fromHtml(mContext.getResources().getString(R.string.square_hot)));
                break;
        }
        //头像点击
        holder.iv_head.setOnClickListener(v -> mHeadListener.onClick(mDatas.get(position).getUserId()));
        //话题点击
        holder.rl_topic.setOnClickListener(v -> {
            CommentsActivity.launch(mContext, message.getSquareCommentId() + "",message.getSquareCommentId());
        });
        return convertView;
    }

    class ViewHolder {
        ImageView iv_head;
        ImageView iv_offical;
        TextView tv_name;
        TextView tv_time;
        TextView tv_reply;
        ImageView iv_type;
        TextView tv_type;
        TextView tv_comments;
        ImageView iv_topic_pic;
        TextView tv_topic_title;
        TextView tv_topic_desc;
        RelativeLayout rl_topic;
        GridView gv_pic;
    }

    public interface OnHeadClickListener {
        void onClick(String id);
    }

    public interface OnReplyClickListener {
        void onClick(int postion);
    }
}
