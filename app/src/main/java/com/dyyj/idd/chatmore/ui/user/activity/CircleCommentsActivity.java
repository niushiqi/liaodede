package com.dyyj.idd.chatmore.ui.user.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.model.DataRepository;
import com.dyyj.idd.chatmore.model.network.result.SpaceCircleReplyResult;
import com.dyyj.idd.chatmore.model.network.result.SpaceCircleResult;
import com.dyyj.idd.chatmore.model.network.result.SpaceUserInfoResult;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.model.network.result.UserTopicResult;
import com.dyyj.idd.chatmore.ui.adapter.CommentPicAdapter;
import com.dyyj.idd.chatmore.ui.adapter.HeadPraiseAdapter;
import com.dyyj.idd.chatmore.ui.adapter.ImageAdapter;
import com.dyyj.idd.chatmore.ui.adapter.SpaceCircleCommentsAdapter;
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.DateFormatter;
import com.dyyj.idd.chatmore.utils.DisplayUtils;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;
import com.dyyj.idd.chatmore.utils.GifsSizeFilter;
import com.dyyj.idd.chatmore.utils.Utils;
import com.example.zhouwei.library.CustomPopWindow;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity.REQUEST_CODE_CHOOSE;

/**
 * desc 好友圈评论页面
 * 2018/12/25 10:59 PM
 */
public class CircleCommentsActivity extends FragmentActivity implements View.OnClickListener {

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
    private TextView tv_level;
    private GridView gv_pic;
    private TextView tv_content;
    private TextView tv_time;
    private View headView;


    private ProgressDialog mProgressDialog;
    private View tv_agree_;
    private ImageView iv_like;
    private ImageView iv_menu;
    private TextView txt_like;
    private GridView gv_praise;

    private List<String> mPhotos;//评论图片
    private SpaceCircleCommentsAdapter commentsAdapter;//评论内容
    private CommentPicAdapter picAdapter;//评论图片适配器
    private String mUserId;//id
    private String mCommentID;//id
    private String mImages = "";//图片
    private String replyId;//被回复的评论id
    private String replyUserId;//被回复的评论人id
    private List<SpaceCircleReplyResult.UserComment> mDatas;
    private SpaceCircleResult.UserComment mDetailData;

    public static void launch(Context context, String id, String commentID) {
        Intent intent = new Intent(context, CircleCommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("commentID", commentID);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_comments);
//        EventBus.getDefault().register(this);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);

    }

    private void initData() {
        mUserId = getIntent().getStringExtra("id");
        mCommentID = getIntent().getStringExtra("commentID");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlv_pics.setLayoutManager(layoutManager);

        mPhotos = new ArrayList();
        picAdapter = new CommentPicAdapter(getApplicationContext(), mPhotos);//选择图片Item
        rlv_pics.setAdapter(picAdapter);

        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(this);
        commentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlv_comments.setLayoutManager(commentLayoutManager);
        mDatas = new ArrayList();
        commentsAdapter = new SpaceCircleCommentsAdapter(R.layout.item_space_circle_comments, mDatas);//好友圈评论item
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
        resetSend();
    }


    /**
     * 获取话题内容
     */
    @SuppressLint("CheckResult")
    private void getTopicData() {
        if (TextUtils.isEmpty(mUserId)) {
            return;
        }

        ChatApp.Companion.getInstance().getDataRepository()
                .getTopicInfoById(mCommentID)
                .subscribe((Consumer<SpaceCircleResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        SpaceCircleResult.UserComment detailData = result.getData();
                        mDetailData = detailData;
                        initCommentData();
                        tv_content.setText(detailData.getTopicBody());
                        tv_time.setText(DateFormatter.timeToDate(detailData.getAddTimeStamp()));
                        txt_like.setText(String.valueOf(detailData.getTopicVoteCount()));
                        toggleLike();

                        //贴子图片
                        List<String> commentImages = detailData.getTopicImgs();
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
                            tv_agree_.setVisibility(View.VISIBLE);
                            gv_praise.setVisibility(View.VISIBLE);
                            HeadPraiseAdapter praiseAdapter = new HeadPraiseAdapter(getApplicationContext(), agreeAvatars);
                            gv_praise.setAdapter(praiseAdapter);
                        } else {
                            tv_agree_.setVisibility(View.GONE);
                            gv_praise.setVisibility(View.GONE);
                        }
                        //判断是否为自己发的贴子
                        commentsAdapter.setIsMyselfy(detailData.getUserId());

                    }
                }, throwable -> {

                });

        //用户信息
        ChatApp.Companion.getInstance().getDataRepository()
                .getMyInfo(mUserId)
                .subscribe((Consumer<SpaceUserInfoResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        UserTopicResult.UserInfo detailData = result.getData();

                        ImageLoader.loadHead(iv_head, detailData.getAvatar());
                        tv_name.setText(detailData.getNickname());
                        tv_level.setText(detailData.getUserLevel());

                        int age = claAge(detailData.getBirthday());
                        if (TextUtils.equals("2", detailData.getGender())) {
                            tv_sex_age.setText(String.format("女 / %d岁 ", age));
                        } else {
                            tv_sex_age.setText(String.format("男 / %d岁 ", age));
                        }

                    }
                }, throwable -> {

                });
    }

    private int claAge(String birthday) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(birthday);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date);
            int i = cal1.get(Calendar.YEAR);
            Calendar cal2 = Calendar.getInstance();
            int z = cal2.get(Calendar.YEAR);
            return z - i;
        } catch (ParseException e) {
            return 18;
        }
    }

    /**
     * 获取评论列表页面
     */
    @SuppressLint("CheckResult")
    private void getCommentListData() {
        ChatApp.Companion.getInstance().getDataRepository()
                .getTopicReply(mCommentID)
                .subscribe((Consumer<SpaceCircleReplyResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        mDatas.clear();
                        mDatas.addAll(result.getData());
                        commentsAdapter.notifyDataSetChanged();
                    }
                }, throwable -> {

                });
    }


    private void initView() {
        headView = LayoutInflater.from(this).inflate(R.layout.layout_header_comment, null);////好友圈评论 楼主发的帖子
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
        tv_level = headView.findViewById(R.id.tv_comments_level);
        tv_content = headView.findViewById(R.id.tv_comments_content);
        tv_time = headView.findViewById(R.id.tv_comments_time);
        gv_pic = headView.findViewById(R.id.gv_comment_pic);
        tv_agree_ = headView.findViewById(R.id.tv_agree_);
        gv_praise = headView.findViewById(R.id.gv_comment_priase);
        txt_like = headView.findViewById(R.id.txt_like);
        iv_like = headView.findViewById(R.id.iv_like);
        iv_menu = headView.findViewById(R.id.iv_menu);
        iv_like.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
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
                PicBigActivity.Companion.start(getApplicationContext(), (ArrayList<String>) mDetailData.getTopicImgs(), position);
            }
        });
        //头像点击
        commentsAdapter.setHeadOnclik(new SpaceCircleCommentsAdapter.OnHeadClickListener() {
            @Override
            public void onClick(String userId) {
                PlazaSpaceActivity.Companion.start(CircleCommentsActivity.this, userId, null);
            }
        });
//        //删除评论
//        commentsAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//                SpaceCircleReplyResult.UserComment message = mDatas.get(position);
//                String userid = ChatApp.Companion.getInstance().getDataRepository().getUserid();
//                //是否为自己的评论留言
//                if (TextUtils.equals(userid, message.getReply_user_id())) {
//                    showDeleteTip(position, 1);
//                } else if (TextUtils.equals(detailData.getUserId(), userid)) {//贴主
//                    showDeleteTip(position, 0);
//                }
//                return true;
//            }
//        });

        //点击评论内容进行回复
        commentsAdapter.setOnReplyClickListener(new SpaceCircleCommentsAdapter.OnReplyClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    SpaceCircleReplyResult.UserComment message = mDatas.get(position);

                    replyId = message.getId();
                    replyUserId = message.getReply_user_id();
                    String nickname = message.getNickname();
                    et_comments.setText("");
                    et_comments.setHint(String.format(getResources().getString(R.string.reply_hint), nickname));
                    et_comments.requestFocus();

                    Utils.showKeyboard(CircleCommentsActivity.this, et_comments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

//    /**
//     * 删除动态弹窗提示
//     * flag 0贴主删除 1自己删除
//     */
//    private void showDeleteTip(int position, int flag) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("提示")
//                .setMessage("确定删除该动态")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (flag) {
//                            case 0:
//                                deleteReply(position);
//                                break;
//                            case 1:
//                                deleteMyReply(position);
//                                break;
//                        }
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        builder.create().show();
//    }
//
//    /**
//     * 删除自己的评论
//     */
//    @SuppressLint("CheckResult")
//    private void deleteMyReply(int position) {
//        SpaceCircleReplyResult.UserComment reply = mDatas.get(position);
//        ChatApp.Companion.getInstance().getDataRepository()
//                .deleteMyReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
//                        reply.getSquareTopicId(), reply.getSquareTopicCommentId(), reply.getSquareTopicCommentReplyId())
//                .subscribe((Consumer<StatusResult>) result -> {
//                    if (result.getErrorCode() == 200) {
//                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
//                        mDatas.remove(position);
//                        commentsAdapter.notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) {
//                    }
//                });
//
//    }
//
//    /**
//     * 删除自己的评论
//     */
//    @SuppressLint("CheckResult")
//    private void deleteReply(int position) {
//        SpaceCircleReplyResult.UserComment reply = mDatas.get(position);
//        ChatApp.Companion.getInstance().getDataRepository()
//                .deleteReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
//                        reply.getSquareTopicId(), reply.getSquareTopicCommentId(), reply.getSquareTopicCommentReplyId())
//                .subscribe((Consumer<StatusResult>) result -> {
//                    if (result.getErrorCode() == 200) {
//                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
//                        mDatas.remove(position);
//                        commentsAdapter.notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(getApplicationContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) {
//                    }
//                });
//
//    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_comments://关闭键盘
                resetSend();
                Utils.closeBoardIfShow(CircleCommentsActivity.this);
                break;
            case R.id.iv_comment_return://关闭页面
                Utils.closeBoardIfShow(CircleCommentsActivity.this);
                finish();
                break;
//            case R.id.iv_comments_pic://选择图片
//                Utils.closeBoardIfShow(CircleCommentsActivity.this);
//                openPhoto();
//                break;
            case R.id.tv_comments_send://发布评论
                Utils.closeBoardIfShow(CircleCommentsActivity.this);
//                if (mPhotos.size() > 0) {
//                    uploadPic();
//                } else {
                sendComments();
//                }

                resetSend();
                break;
            case R.id.iv_like:
                like();
                break;
            case R.id.iv_menu:
                showPop();
                break;
        }
    }

    public void resetSend() {
        replyId = "0";
        if (null != mDetailData) {
            replyUserId = mDetailData.getUserId();
        }
        et_comments.setText("");
        et_comments.setHint("发个评论吧");
    }

    //删除动态
    private void deleteDynamic() {
        ChatApp.Companion.getInstance().getDataRepository()
                .deleteCircleDynamic(mUserId,mCommentID)
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

    private void showPop() {
        View contentView = View.inflate(this, R.layout.pop_report_or_delete, null);
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
        if (TextUtils.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid(), mUserId)) {//如果是自己的帖子，隐藏举报按钮
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
                    deleteDynamic();
                    break;
                case R.id.report_btn:
                    Toast.makeText(CircleCommentsActivity.this,"举报", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
        reportBtn.setOnClickListener(onClickListener);
        closeBtn.setOnClickListener(onClickListener);
    }

//    /**
//     * 上传图片
//     */
//    @SuppressLint("CheckResult")
//    private void uploadPic() {
//        showProgress();
//        ChatApp.Companion.getInstance().getDataRepository()
//                .postUploadPicPlaza(mPhotos)
//                .subscribe((Consumer<ArrayList<PlazaPicResult>>) result -> {
//                    System.out.println("+++++++result" + result);
//                    StringBuffer sb = new StringBuffer();
//                    for (int i = 0; i < result.size(); i++) {
//                        System.out.println("+++++++url" + result.get(i).getData().getImageFilename());
//                        if (i == result.size() - 1) {
//                            sb.append(result.get(i).getData().getImageFilename());
//                        } else {
//                            sb.append(result.get(i).getData().getImageFilename()).append(",");
//                        }
//                    }
//                    mImages = sb.toString();
//                    System.out.println("+++++++" + mImages);
//                    sendComments();
//                }, throwable -> {
//                });
//    }

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
                .createReplyNew(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        commentContent, mCommentID, this.replyUserId, this.replyId)
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
        Matisse.from(CircleCommentsActivity.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9 - mPhotos.size())
                .addFilter(new GifsSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void like() {
        if (mDetailData.isVote() == 1) return;

        DataRepository dataRepository = ChatApp.Companion.getInstance().getDataRepository();
        dataRepository.voteTopic(dataRepository.getUserid(), mDetailData.getId()).subscribe(it -> {
            if (it.getErrorCode() == 200) {
                mDetailData.setVote(1);
                int i = 1 + mDetailData.getTopicVoteCount();
                mDetailData.setTopicVoteCount(i);
                txt_like.setText(i + "");
                toggleLike();

            } else {
                Toast.makeText(getApplicationContext(), it.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
            EventTrackingUtils.joinPoint(new EventBeans("ck_square_like", mDetailData.getId()));
        }, e -> {
            e.printStackTrace();
        });
    }

    private void toggleLike() {
        if (mDetailData.isVote() == 1) {
            iv_like.setImageResource(R.drawable.ic_dynamic_like_ok);
            txt_like.setTextColor(Color.parseColor("#FF715F"));
        } else {
            iv_like.setImageResource(R.drawable.ic_dynamic_like);
            txt_like.setTextColor(Color.parseColor("#7F8D9C"));
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
