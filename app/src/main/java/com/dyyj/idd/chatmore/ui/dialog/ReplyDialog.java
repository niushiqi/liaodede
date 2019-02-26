package com.dyyj.idd.chatmore.ui.dialog;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.network.result.PlazaPicResult;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.ui.adapter.CommentPicAdapter;
import com.dyyj.idd.chatmore.utils.GifsSizeFilter;
import com.dyyj.idd.chatmore.utils.Utils;
import com.othershe.nicedialog.ViewHolder;
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

@SuppressLint("ValidFragment")
public class ReplyDialog extends CommonDialog implements View.OnClickListener {
    private List<String> mPhotos;
    private CommentPicAdapter picAdapter;
    private RecyclerView rlv_pics;
    private EditText et_comments;

    private String mImages = "";
    private String mTopicId;//话题id
    private String mCommentId;
    private String replyToUserId;//被回复人id
    private String replyToReplyId;//被回复内容id
    private String replyToUserName;//被回复人
    private ProgressDialog mProgressDialog;

    public ReplyDialog(String tipicId, String commentId, String replyToUserId,
                       String replyToReplyId, String replyToUserName) {
        this.mTopicId = tipicId;
        this.mCommentId = commentId;
        this.replyToUserId = replyToUserId;
        this.replyToReplyId = replyToReplyId;
        this.replyToUserName = replyToUserName;
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_reply;
    }

    @Override
    public void convertView(ViewHolder holder, CommonDialog dialog) {
        EventBus.getDefault().register(this);
        ImageView iv_pic = holder.getView(R.id.iv_comments_pic);
        TextView tv_send = holder.getView(R.id.tv_comments_send);
        rlv_pics = holder.getView(R.id.rlv_comments_pics);
        et_comments = holder.getView(R.id.et_comments_content);

        iv_pic.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlv_pics.setLayoutManager(layoutManager);

        Utils.showKeyboard(getActivity(), et_comments);

        if (!TextUtils.isEmpty(replyToUserName)) {
            et_comments.setHint(String.format(getResources().getString(R.string.reply_hint), replyToUserName));
        }
        mPhotos = new ArrayList();
        picAdapter = new CommentPicAdapter(getActivity(), mPhotos);
        rlv_pics.setAdapter(picAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_comments_pic:
                Utils.closeBoardIfShow(getActivity());
                openPhoto();
                break;
            case R.id.tv_comments_send:
                Utils.closeBoardIfShow(getActivity());
                if (mPhotos.size() > 0) {
                    uploadPic();
                } else {
                    sendComments();
                }
                break;
        }
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
            Toast.makeText(getActivity(), "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        ChatApp.Companion.getInstance().getDataRepository()
                .postTopicCommentReply(ChatApp.Companion.getInstance().getDataRepository().getUserid(),
                        mTopicId, mCommentId, replyToReplyId,
                        mImages, replyToUserId, commentContent)
                .subscribe((Consumer<StatusResult>) result -> {
                    closeProgress();
                    if (result.getErrorCode() == 200) {
                        EventBus.getDefault().post("200");
                        dismiss();
                        Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    closeProgress();
                });
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(getActivity());
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
     * 选择相册图片
     */
    private void openPhoto() {
        Matisse.from(getActivity())
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9 - mPhotos.size())
                .addFilter(new GifsSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(101);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<String> data) {
        mPhotos.addAll(data);
        if (mPhotos.size() > 0) {
            rlv_pics.setVisibility(View.VISIBLE);
        } else {
            rlv_pics.setVisibility(View.GONE);
        }
        picAdapter.notifyDataSetChanged();
    }

}
