package com.dyyj.idd.chatmore.ui.user.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.network.result.PlazaPicResult;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.model.network.result.UserSquareCommentReplyResult;
import com.dyyj.idd.chatmore.model.network.result.UserSquareCommentResult;
import com.dyyj.idd.chatmore.ui.adapter.CommentPicAdapter;
import com.dyyj.idd.chatmore.ui.adapter.CommentsAdapter;
import com.dyyj.idd.chatmore.ui.adapter.HeadPraiseAdapter;
import com.dyyj.idd.chatmore.ui.adapter.ImageAdapter;
import com.dyyj.idd.chatmore.ui.dialog.ReplyDialog;
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;
import com.dyyj.idd.chatmore.utils.DisplayUtils;
import com.dyyj.idd.chatmore.utils.GifsSizeFilter;
import com.dyyj.idd.chatmore.utils.Utils;
import com.example.zhouwei.library.CustomPopWindow;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity.REQUEST_CODE_CHOOSE;

/**
 * desc 评论
 * 2018/12/25 10:59 PM
 */
public class CommentsActivity extends FragmentActivity implements View.OnClickListener {

    String CURRENT_USER_ID = "currentUserId";

    CustomPopWindow mPopWindow = null;

    RelativeLayout rl_root;
    ImageView iv_pic;
    TextView tv_send;
    EditText et_comments;
    RecyclerView rlv_comments;
    RecyclerView rlv_pics;
    ImageView iv_return;
    private ImageView iv_head;
    private TextView tv_name;
    private TextView tv_sex_age;
    private TextView tv_chat;
    private TextView tv_level;
    private GridView gv_pic;
    private TextView tv_content;
    private TextView tv_time;
    private ImageView iv_menu;
    TextView tv_topic_title;
    TextView tv_topic_content;
    private View headView;


    private List<String> mPhotos;//评论图片
    private CommentsAdapter commentsAdapter;//评论内容
    private CommentPicAdapter picAdapter;//评论图片适配器
    private String mId;//话题id
    private String mDeleteId;//话题id
    private String mImages = "";//图片
    private String replyToReplyId;
    private String replyToUserId;//被回复人id
    private List<UserSquareCommentReplyResult.UserReply> mDatas;
    private UserSquareCommentResult.UserComment detailData;
    private String squareTopicId;
    private String squareTopicCommentId;
    private ProgressDialog mProgressDialog;
    private LinearLayout ll_praise;
    private GridView gv_praise;

    public static void launch(Context context, String id, String deleteId) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("deleteId", deleteId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        EventBus.getDefault().register(this);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mId = getIntent().getStringExtra("id");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlv_pics.setLayoutManager(layoutManager);

        mPhotos = new ArrayList();
        picAdapter = new CommentPicAdapter(getApplicationContext(), mPhotos);
        rlv_pics.setAdapter(picAdapter);

        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(this);
        commentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlv_comments.setLayoutManager(commentLayoutManager);
        mDatas = new ArrayList();
        commentsAdapter = new CommentsAdapter(R.layout.item_comments, mDatas);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.shape_line_horizontal));
        rlv_comments.addItemDecoration(divider);
        rlv_comments.setAdapter(commentsAdapter);
        commentsAdapter.addHeaderView(headView);
        getTopicData();
        getCommentListData();
    }

    /**
     * 初始化 评论接口时的参数
     */
    private void initCommentData() {
        Utils.closeBoardIfShow(this);
        replyToUserId = "";
        replyToReplyId = "";
        if (detailData != null) {
            squareTopicId = detailData.getSquareTopicId();
            squareTopicCommentId = detailData.getSquareTopicCommentId();
        }
        et_comments.setHint("发个评论吧");
    }


    /**
     * 获取话题内容
     */
    @SuppressLint("CheckResult")
    private void getTopicData() {
        if (TextUtils.isEmpty(mId)) {
            return;
        }
        ChatApp.Companion.getInstance().getDataRepository()
                .getCommentByID(ChatApp.Companion.getInstance().getDataRepository().getUserid(), mId)
                .subscribe((Consumer<UserSquareCommentResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        detailData = result.getData();
                        initCommentData();
                        tv_content.setText(detailData.getCommentContent());
                        tv_time.setText(DateFormatter.timeToDate(detailData.getCommentTimestamp()));
                        ImageLoader.loadHead(iv_head, detailData.getAvatar());
                        tv_name.setText(detailData.getNickname());
//                        tv_topic_title.setText(detailData.getSquareTopicTitle());
                        tv_topic_content.setText(detailData.getSquareTopicTitle());
                        tv_level.setText(detailData.getUserLevel());
                        if (TextUtils.equals("2", detailData.getGender())) {
                            tv_sex_age.setText(String.valueOf("女 / " + detailData.getBirthday()));
                        } else {
                            tv_sex_age.setText(String.valueOf("男 / " + detailData.getBirthday()));
                        }
                        String userid = ChatApp.Companion.getInstance().getDataRepository().getUserid();
                        //自己的贴子
                        if (TextUtils.equals(userid, detailData.getUserId())) {
                            tv_chat.setVisibility(View.INVISIBLE);
                        } else {
                            tv_chat.setVisibility(View.VISIBLE);
                        }
                        //贴子图片
                        List<String> commentImages = detailData.getCommentImage();
                        if (commentImages.size() > 0) {
                            gv_pic.setVisibility(View.VISIBLE);
                            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), commentImages);
                            gv_pic.setAdapter(imageAdapter);
                        } else {
                            gv_pic.setVisibility(View.GONE);
                        }
                        //点赞头像
                        List<String> agreeAvatars = detailData.getAgreeAvatar();
                        if (agreeAvatars.size() > 0) {
                            ll_praise.setVisibility(View.VISIBLE);
                            HeadPraiseAdapter praiseAdapter = new HeadPraiseAdapter(getApplicationContext(), agreeAvatars);
                            gv_praise.setAdapter(praiseAdapter);
                        } else {
                            ll_praise.setVisibility(View.GONE);
                        }
                        //判断是否为自己发的贴子
                        boolean isMyself = TextUtils.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid(), detailData.getUserId());
                        commentsAdapter.setIsMyselfy(isMyself);

                    }
                }, throwable -> {

                });
    }

    /**
     * 获取评论列表页面
     */
    @SuppressLint("CheckResult")
    private void getCommentListData() {
        ChatApp.Companion.getInstance().getDataRepository()
                .getCommentReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(), mId)
                .subscribe((Consumer<UserSquareCommentReplyResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        mDatas.clear();
                        mDatas.addAll(result.getData());
                        commentsAdapter.notifyDataSetChanged();
                    }
                }, throwable -> {

                });
    }


    private void initView() {
        headView = LayoutInflater.from(this).inflate(R.layout.header_comment, null);
        rl_root = findViewById(R.id.rl_comments);
        iv_return = findViewById(R.id.iv_comment_return);
        iv_pic = findViewById(R.id.iv_comments_pic);
        tv_send = findViewById(R.id.tv_comments_send);
        rlv_pics = findViewById(R.id.rlv_comments_pics);
        et_comments = findViewById(R.id.et_comments_content);
        rlv_comments = findViewById(R.id.rlv_comments);
        iv_head = headView.findViewById(R.id.iv_comments_head);
        tv_name = headView.findViewById(R.id.tv_comments_name);
        tv_sex_age = headView.findViewById(R.id.tv_comments_sex);
        tv_chat = headView.findViewById(R.id.tv_comment_chat);
        tv_level = headView.findViewById(R.id.tv_comments_level);
        tv_content = headView.findViewById(R.id.tv_comments_content);
        tv_time = headView.findViewById(R.id.tv_comments_time);
        iv_menu = headView.findViewById(R.id.img_menu);
        gv_pic = headView.findViewById(R.id.gv_comment_pic);
        tv_topic_title = headView.findViewById(R.id.tv_comment_topic_title);
        tv_topic_content = headView.findViewById(R.id.tv_comment_topic_content);
        ll_praise = headView.findViewById(R.id.ll_comment_praise);
        gv_praise = headView.findViewById(R.id.gv_comment_priase);
        iv_head.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        tv_chat.setOnClickListener(this);
        iv_pic.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        rl_root.setOnClickListener(this);

    }

    private void setListener() {
        picAdapter.setDeleteListener(position -> {
            mPhotos.remove(position);
            picAdapter.notifyDataSetChanged();
        });
        gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicBigActivity.Companion.start(CommentsActivity.this, (ArrayList<String>) detailData.getCommentImage(), position);
            }
        });
        //头像点击
        commentsAdapter.setHeadOnclik(new CommentsAdapter.OnHeadClickListener() {
            @Override
            public void onClick(String userId) {
                PlazaSpaceActivity.Companion.start(CommentsActivity.this, userId, squareTopicId);
//                new PersonDialog(userId).setTopicID(squareTopicId).setOutCancel(false).show(getSupportFragmentManager());
            }
        });
        //删除评论
        commentsAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                UserSquareCommentReplyResult.UserReply message = mDatas.get(position);
                String userid = ChatApp.Companion.getInstance().getDataRepository().getUserid();
                //是否为自己的评论留言
                if (TextUtils.equals(userid, message.getReplyUserId())) {
                    showDeleteTip(position, 1);
                } else if (TextUtils.equals(detailData.getUserId(), userid)) {//贴主
                    showDeleteTip(position, 0);
                }
                return true;
            }
        });
        //置为神评论
        commentsAdapter.setOnNbClickListener(new CommentsAdapter.OnNbClickListener() {
            @Override
            public void onClick(int position) {
                if (TextUtils.equals(mDatas.get(position).getReplyNb(), "0")) {//设为神评论
                    setNb(position);
                } else {
                    cancleNb(position);
                }
            }
        });
        //点赞
        commentsAdapter.setOnPraiseClickListener(new CommentsAdapter.OnPraiseClickListener() {
            @Override
            public void onClick(int position) {
                setPraise(position);
            }
        });
        //点击评论内容进行回复
        commentsAdapter.setOnReplyClickListener(new CommentsAdapter.OnReplyClickListener() {
            @Override
            public void onClick(int position) {
                UserSquareCommentReplyResult.UserReply message = mDatas.get(position);
                Log.i("zhangwj", "" + message.getReplyToUserId());
                new ReplyDialog(message.getSquareTopicId() + "", message.getSquareTopicCommentId(),
                        message.getReplyUserId(), message.getReplyToReplyId(), message.getReplyUserNickname())
                        .setShowBottom(true).show(getSupportFragmentManager());
            }
        });
        commentsAdapter.setOnChatClickListener(new CommentsAdapter.OnChatClickListener() {
            @Override
            public void onClick(String userId) {
                PlazaSpaceActivity.Companion.start(CommentsActivity.this, userId, squareTopicId);

//                new PersonDialog(userId).setTopicID(squareTopicId).setOutCancel(false).show(getSupportFragmentManager());

            }
        });
    }

    /**
     * 删除动态弹窗提示
     * flag 0贴主删除 1自己删除
     */
    private void showDeleteTip(int position, int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定删除该动态")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (flag) {
                            case 0:
                                deleteReply(position);
                                break;
                            case 1:
                                deleteMyReply(position);
                                break;
                        }
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
     * 删除自己的评论
     */
    @SuppressLint("CheckResult")
    private void deleteMyReply(int position) {
        UserSquareCommentReplyResult.UserReply reply = mDatas.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .deleteMyReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        reply.getSquareTopicId(), reply.getSquareTopicCommentId(), reply.getSquareTopicCommentReplyId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        mDatas.remove(position);
                        commentsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });

    }

    /**
     * 删除自己的评论
     */
    @SuppressLint("CheckResult")
    private void deleteReply(int position) {
        UserSquareCommentReplyResult.UserReply reply = mDatas.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .deleteReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        reply.getSquareTopicId(), reply.getSquareTopicCommentId(), reply.getSquareTopicCommentReplyId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        mDatas.remove(position);
                        commentsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });

    }

    /**
     * 取消神评论
     */
    @SuppressLint("CheckResult")
    private void cancleNb(int position) {
        UserSquareCommentReplyResult.UserReply item = mDatas.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .cancleNbReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        item.getSquareTopicId(), item.getSquareTopicCommentId(), item.getSquareTopicCommentReplyId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        getCommentListData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

    /**
     * 点赞
     */
    @SuppressLint("CheckResult")
    private void setPraise(int position) {
        UserSquareCommentReplyResult.UserReply item = mDatas.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .postTopicCommentReplyAgree(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        item.getSquareTopicId(), item.getSquareTopicCommentId(), item.getSquareTopicCommentReplyId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
                        int agreeNum = Integer.parseInt(mDatas.get(position).getReplyAgreeNum()) + 1;
                        mDatas.get(position).setReplyAgreeNum(String.valueOf(agreeNum));
                        commentsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void setNb(int position) {
        UserSquareCommentReplyResult.UserReply item = mDatas.get(position);
        ChatApp.Companion.getInstance().getDataRepository()
                .setNbReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        item.getSquareTopicId(), item.getSquareTopicCommentId(), item.getSquareTopicCommentReplyId())
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        getCommentListData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_comments://关闭键盘
                Utils.closeBoardIfShow(CommentsActivity.this);
                break;
            case R.id.iv_comments_head://贴子主头像点击
                if (null == detailData || TextUtils.isEmpty(detailData.getUserId())) {
                    return;
                }
                PlazaSpaceActivity.Companion.start(CommentsActivity.this, detailData.getUserId(), squareTopicId);

//                new PersonDialog(detailData.getUserId()).setTopicID(squareTopicId).setOutCancel(false).show(getSupportFragmentManager());
                break;
            case R.id.img_menu://点击功能键弹出PopWin，举报or删除
                showPop();
                break;
            case R.id.tv_comment_chat://贴子主私聊他
                if (null == detailData || TextUtils.isEmpty(detailData.getUserId())) {
                    return;
                }
                PlazaSpaceActivity.Companion.start(CommentsActivity.this, detailData.getUserId(), squareTopicId);
//                new PersonDialog(detailData.getUserId()).setTopicID(squareTopicId).setOutCancel(false).show(getSupportFragmentManager());
                break;
            case R.id.iv_comment_return://关闭页面
                Utils.closeBoardIfShow(CommentsActivity.this);
                finish();
                break;
            case R.id.iv_comments_pic://选择图片
                Utils.closeBoardIfShow(CommentsActivity.this);
                openPhoto();
                break;
            case R.id.tv_comments_send://发布评论
                Utils.closeBoardIfShow(CommentsActivity.this);
                if (mPhotos.size() > 0) {
                    uploadPic();
                } else {
                    sendComments();
                }
                break;
        }
    }

    private void showPop() {
        View contentView = View.inflate(this, R.layout.pop_report_or_delete, null);
        RelativeLayout rl = contentView.findViewById(R.id.rl_1);
        handleClickListener(contentView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(contentView)
                    .size(DisplayUtils.dp2px(this, 60f), LinearLayout.LayoutParams.WRAP_CONTENT)
                    .create();
            mPopWindow.showAsDropDown(iv_menu, -DisplayUtils.dp2px(this, -5f), -DisplayUtils.dp2px(this, (mPopWindow.getHeight()+iv_menu.getHeight()+2)), Gravity.RIGHT);//iv_menu.getWidth()/2+
        }
    }

    private void handleClickListener(View contentView) {
        ImageView reportBtn = contentView.findViewById(R.id.report_btn);
        ImageView closeBtn = contentView.findViewById(R.id.close_btn);
        //判断是否为自己发的贴子
        if (TextUtils.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid(), detailData.getUserId())) {//如果是自己的帖子，隐藏举报按钮
            reportBtn.setVisibility(View.GONE);
        }else{
            closeBtn.setVisibility(View.GONE);
        }
        View.OnClickListener onClickListener = v -> {
            if (mPopWindow != null) {
                mPopWindow.dissmiss();
            }
            switch (v.getId()) {
                case R.id.close_btn:
                    //Toast.makeText(CommentsActivity.this,"关闭", Toast.LENGTH_SHORT).show();
                    deleteDynamic();
                    break;
                case R.id.report_btn:
                    Toast.makeText(CommentsActivity.this,"举报", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
        reportBtn.setOnClickListener(onClickListener);
        closeBtn.setOnClickListener(onClickListener);
    }

    private void deleteDynamic() {
        ChatApp.Companion.getInstance().getDataRepository()
                .deleteDynamic(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        getIntent().getStringExtra("deleteId"))
                .subscribe((Consumer<StatusResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        finish();
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

    /**
     * 上传图片
     */
    @SuppressLint("CheckResult")
    private void uploadPic() {
        showProgress();
        ChatApp.Companion.getInstance().getDataRepository()
                .postUploadPicPlaza(mPhotos)
                .subscribe((Consumer<ArrayList<PlazaPicResult>>) result -> {
                    System.out.println("+++++++result" + result);
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < result.size(); i++) {
                        System.out.println("+++++++url" + result.get(i).getData().getImageFilename());
                        if (i == result.size() - 1) {
                            sb.append(result.get(i).getData().getImageFilename());
                        } else {
                            sb.append(result.get(i).getData().getImageFilename()).append(",");
                        }
                    }
                    mImages = sb.toString();
                    System.out.println("+++++++" + mImages);
                    sendComments();
                }, throwable -> {
                });
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(this);
        // 进度条为水平旋转
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置点击返回不能取消
//        mProgressDialog.setCancelable(true);
        //设置触摸对话框以外的区域不会消失
        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void closeProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 发送留言
     */
    @SuppressLint("CheckResult")
    private void sendComments() {
        String commentContent = et_comments.getText().toString();//评论内容
        if (TextUtils.isEmpty(commentContent)) {
            commentContent = "";
        }
        if (TextUtils.isEmpty(commentContent) && TextUtils.isEmpty(mImages)) {
            Toast.makeText(getApplicationContext(), "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        ChatApp.Companion.getInstance().getDataRepository()
                .postTopicCommentReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        squareTopicId, squareTopicCommentId, replyToReplyId,
                        mImages, replyToUserId, commentContent)
                .subscribe((Consumer<StatusResult>) result -> {
                    closeProgress();
                    if (result.getErrorCode() == 200) {
                        initCommentData();
                        getCommentListData();
                        et_comments.setText("");
                        mImages = "";
                        mPhotos.clear();
                        picAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                });
    }

    /**
     * 选择相册图片
     */
    private void openPhoto() {
        Matisse.from(CommentsActivity.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9 - mPhotos.size())
                .addFilter(new GifsSizeFilter(10, 10, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String code) {
        Utils.closeBoardIfShow(CommentsActivity.this);
        if (code.equals("200")) {
            getCommentListData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mPhotos.addAll(Matisse.obtainPathResult(data));
            if (mPhotos.size() > 0) {
                rlv_pics.setVisibility(View.VISIBLE);
            } else {
                rlv_pics.setVisibility(View.GONE);
            }
            picAdapter.notifyDataSetChanged();
        }
        //回复弹窗选择图片回调
        if (requestCode == 101 && resultCode == RESULT_OK) {
            EventBus.getDefault().post(Matisse.obtainPathResult(data));
        }
    }
}
