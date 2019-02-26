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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity;
import com.dyyj.idd.chatmore.model.network.result.UserSquareDynamicListResult;
import com.dyyj.idd.chatmore.ui.user.activity.PicBigActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * desc 动态 广场数据适配
 *
 * @author zhangcx
 * 2018/12/26 6:25 PM
 */
public class DynamicSquareAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserSquareDynamicListResult.UserMessage> mDatas;

    private final int TYPE_dynamic = 0;
    private final int TYPE_COMMENT = 1;

    public DynamicSquareAdapter(Context context, List<UserSquareDynamicListResult.UserMessage> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).getDynamicType().equals("1")) {
            return TYPE_dynamic;
        } else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        int viewType = getItemViewType(position);
        UserInfoEntity userInfo = ChatApp.Companion.getInstance().getDataRepository().getUserInfoEntity();
        if (viewType == TYPE_COMMENT) {//动态
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_square, null);
                holder.iv_head = convertView.findViewById(R.id.iv_item_square_head);
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
                holder.ll_reply = convertView.findViewById(R.id.ll_item_square_reply);
                holder.tv_reply = convertView.findViewById(R.id.tv_item_square_reply);
                holder.gv_reply_pic = convertView.findViewById(R.id.gv_item_square_reply_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserSquareDynamicListResult.UserMessage message = mDatas.get(position);
            ImageLoader.loadHead(holder.iv_head, userInfo.avatar);
            holder.tv_name.setText(userInfo.nickname);
            holder.tv_time.setText(DateFormatter.timeToDate(message.getDynamicTimestamp()));
            holder.tv_topic_title.setText(message.getSquareTopicCommentContent());
            holder.tv_topic_desc.setText("#" + message.getSquareTopicTitle() + "#");
            ImageLoader.loadPicCorner(holder.iv_topic_pic, message.getSquareTopicImage());
            if (message.getSquareTopicCommentImage().size() > 0) {
                ImageLoader.loadPicCorner(holder.iv_topic_pic, message.getSquareTopicCommentImage().get(0));
            }
            String type = null;
            switch (Objects.requireNonNull(message.getDynamicType())) {
                case "2"://对帖子发表评论
                    holder.ll_reply.setVisibility(View.GONE);
                    holder.iv_type.setImageResource(R.drawable.ic_square_comments);
                    type = mContext.getResources().getString(R.string.dynamic_comments);
                    type = String.format(type, message.getNickname());
                    //显示评论内容
                    if (TextUtils.isEmpty(message.getSquareTopicCommentContent())) {
                        holder.tv_comments.setVisibility(View.GONE);
                    } else {
                        holder.tv_comments.setVisibility(View.VISIBLE);
                    }
                    holder.tv_comments.setText(message.getReplyContent());
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
                case "3"://回复评论
                    holder.ll_reply.setVisibility(View.VISIBLE);
                    type = mContext.getResources().getString(R.string.dynamic_reply);
                    type = String.format(type, message.getNickname());
                    //显示回复评论内容
                    if (TextUtils.isEmpty(message.getReplyContent())) {
                        holder.tv_comments.setVisibility(View.GONE);
                    } else {
                        holder.tv_comments.setVisibility(View.VISIBLE);
                        holder.tv_comments.setText(message.getReplyContent());
                    }
                    //显示回复的评论图片
                    List<String> replyImages = message.getCommentImage();
                    if (replyImages.size() > 0) {
                        holder.gv_pic.setVisibility(View.VISIBLE);
                        ImageAdapter imageAdapter = new ImageAdapter(mContext, replyImages);
                        holder.gv_pic.setAdapter(imageAdapter);
                    } else {
                        holder.gv_pic.setVisibility(View.GONE);
                    }

                    //显示被回复的内容
                    String comments = mContext.getResources().getString(R.string.dynamic_reply_comments);
                    comments = String.format(comments, message.getNickname(), message.getSquareTopicCommentReply());
                    holder.tv_reply.setText(Html.fromHtml(comments));
                    //被回复的图片
                    List<String> commentImages2 = message.getSquareTopicCommentReplyImage();
                    if (commentImages2.size() > 0) {
                        holder.gv_reply_pic.setVisibility(View.VISIBLE);
                        ImageAdapter imageAdapter = new ImageAdapter(mContext, commentImages2);
                        holder.gv_reply_pic.setAdapter(imageAdapter);
                    } else {
                        holder.gv_reply_pic.setVisibility(View.GONE);
                    }
                    holder.gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PicBigActivity.Companion.start(mContext, (ArrayList<String>) replyImages, position);
                        }
                    });
                    holder.gv_reply_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PicBigActivity.Companion.start(mContext, (ArrayList<String>) commentImages2, position);
                        }
                    });
                    break;
                case "4"://点赞帖子
                    holder.ll_reply.setVisibility(View.GONE);
                    holder.gv_pic.setVisibility(View.GONE);
                    holder.tv_comments.setVisibility(View.GONE);
                    holder.iv_type.setImageResource(R.drawable.ic_square_praise);
                    type = mContext.getResources().getString(R.string.dynamic_praise_tiezi);
                    type = String.format(type, message.getNickname());
                    break;
                case "5"://点赞评论
                    holder.ll_reply.setVisibility(View.GONE);
                    holder.gv_pic.setVisibility(View.GONE);
                    holder.tv_comments.setVisibility(View.GONE);
                    holder.iv_type.setImageResource(R.drawable.ic_square_praise);
                    type = mContext.getResources().getString(R.string.dynamic_praise_comments);
                    type = String.format(type, message.getNickname());
                    break;
            }
            holder.tv_type.setText(Html.fromHtml(type));
            //发表的动态
        } else if (viewType == TYPE_dynamic) {
            DynamicHolder holder;
            if (convertView == null) {
                holder = new DynamicHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_square_dynamic, null);
                holder.iv_head = convertView.findViewById(R.id.iv_item_dynamic_head);
                holder.tv_name = convertView.findViewById(R.id.tv_item_dynamic_name);
                holder.tv_time = convertView.findViewById(R.id.tv_item_dynamic_time);
                holder.tv_topic = convertView.findViewById(R.id.tv_item_dynamic_topic);
                holder.tv_content = convertView.findViewById(R.id.tv_item_dynamic_content);
                holder.gv_pic = convertView.findViewById(R.id.gv_item_dynamic_pic);
                holder.tv_comments_nums = convertView.findViewById(R.id.tv_item_dynamic_comments);
                holder.tv_praise_nums = convertView.findViewById(R.id.tv_item_dynamic_praise);
                convertView.setTag(holder);
            } else {
                holder = (DynamicHolder) convertView.getTag();
            }
            UserSquareDynamicListResult.UserMessage message = mDatas.get(position);
            ImageLoader.loadHead(holder.iv_head, userInfo.avatar);
            holder.tv_name.setText(userInfo.nickname);
            holder.tv_time.setText(DateFormatter.timeToDate(message.getDynamicTimestamp()));
            holder.tv_topic.setText("话题#"+message.getSquareTopicTitle()+"#");
            holder.tv_praise_nums.setText(message.getCommentAgreeNum());
            if (TextUtils.isEmpty(message.getCommentContent())) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(message.getCommentContent());
            }
            List<String> commentImages = message.getCommentImage();
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
        }
        return convertView;
    }


    class ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_time;
        ImageView iv_type;
        TextView tv_type;
        TextView tv_comments;
        ImageView iv_topic_pic;
        TextView tv_topic_title;
        TextView tv_topic_desc;
        RelativeLayout rl_topic;
        GridView gv_pic;//评论的图片
        LinearLayout ll_reply;
        TextView tv_reply;
        GridView gv_reply_pic;//回复评论的图片
    }

    class DynamicHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_time;
        TextView tv_topic;
        TextView tv_content;
        TextView tv_comments_nums;//评论数量
        TextView tv_praise_nums;
        GridView gv_pic;
    }
}
