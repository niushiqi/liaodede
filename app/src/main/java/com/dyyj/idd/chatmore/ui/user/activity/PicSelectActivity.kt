package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPicSelectBinding
import com.dyyj.idd.chatmore.ui.user.photo.Folder
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.ui.user.photo.ImageAdapter
import com.dyyj.idd.chatmore.ui.user.photo.ImageModel
import com.dyyj.idd.chatmore.viewmodel.PicSelectViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

@RuntimePermissions
class PicSelectActivity : BaseActivity<ActivityPicSelectBinding, PicSelectViewModel>() {

    companion object {

        var fromPublish: Boolean = false

        const val CODE_TAKE_PHOTO = 100

        var photoList: ArrayList<Image>? = null

        fun start(context: Context, list: ArrayList<Image>, fromPub: Boolean? = false) {
            photoList = list
            fromPublish = fromPub ?: false
            context.startActivity(Intent(context, PicSelectActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_pic_select
    }

    override fun onViewModel(): PicSelectViewModel {
        return PicSelectViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "从相册选择图片 "

        loadImageForSDCardWithPermissionCheck()

        mBinding.txtPre.setOnClickListener {
            //            if (mViewModel.getAdapter().getSelectList().size == 0) {
//                Toast.makeText(this@PicSelectActivity, "请选择图片", Toast.LENGTH_SHORT).show()
//            } else {
//                PicPreActivity.start(this@PicSelectActivity, mViewModel.getAdapter().getSelectList(), fromPublish)
            if (fromPublish) {
                EventBus.getDefault().post(PicPreActivity.RefreshPubPic(mViewModel.getAdapter().getSelectList()))
            } else {
                PublishActivity.start(this@PicSelectActivity, mViewModel.getAdapter().getSelectList())
            }
            finish()

//            }
        }
    }

    private val LOCAL = "local"
    private val mData: ArrayList<Image> = arrayListOf()
    private lateinit var mFolder: Folder

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun loadImageForSDCard() {
        ImageModel.loadImageForSDCard(this, object : ImageModel.DataCallback {
            override fun onSuccess(folders: ArrayList<Folder>) {
                mFolder = folders[0]
                runOnUiThread {
                    if (folders[0] != null) {
                        setFolder(folders[0])
                    }
                }
            }
        })
    }

    private fun initRecyclerView(list: ArrayList<Image>) {
        mViewModel.getAdapter().initData(list)
        mBinding.rvList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvList.layoutManager = GridLayoutManager(this@PicSelectActivity, 4)
        mBinding.rvList.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().setTakePicListener(object : ImageAdapter.ITakePicListener {
            override fun onTake() {
                takePicWithPermissionCheck()
            }
        })
        mViewModel.getAdapter().refreshImages(photoList!!)
    }

    private fun setFolder(folder: Folder) {
        if (folder != null) {
            mData.clear()
            mData.addAll(folder.getImages())
            for (data in mData) {
                data.setType(0)
            }
            val image = Image(LOCAL, 0, LOCAL, 0)
            image.type = 1
            mData.add(0, image)
            initRecyclerView(mData)
        }
    }

    @Subscribe
    fun onPicChangeVM(obj: PicPreActivity.PicChangeVM) {
//        mViewModel.getAdapter().refreshImages(obj)
        finish()
    }

    private var mImageCaptureUri: Uri? = null
    private var takeImageFile: File? = null

    @NeedsPermission(Manifest.permission.CAMERA)
    fun takePic() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (intent.resolveActivity(packageManager) != null) {
            mImageCaptureUri = makeImageUri()
            Log.i("zhangwj","mImageCaptureUri="+mImageCaptureUri)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        }
        startActivityForResult(intent, CODE_TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            var path = mImageCaptureUri?.path
            //需要替换掉路径
            if(path!!.contains("/rc_external_path")) {
                 path = path.replace("/rc_external_path", Environment.getExternalStorageDirectory().absolutePath)
            }
            scannerPicture(this@PicSelectActivity, path!!, path)
            val image = Image(path, System.currentTimeMillis(), path, 0)
            mFolder.images.add(0, image)
            mFolder.images = mFolder.images
            mViewModel.getAdapter().addSelectImage(image)
            setFolder(mFolder)
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    private fun makeImageUri(): Uri {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            takeImageFile = File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
        } else {
            takeImageFile = Environment.getDataDirectory()
        }
        takeImageFile = createFile(takeImageFile!!, "IMG_", ".jpg")
        //return Uri.fromFile(takeImageFile!!)
        //Android 7.0后不能直接使用file格式的Uri,需要使用FileProvider
        return FileProvider.getUriForFile(this@PicSelectActivity, "com.dyyj.idd.chatmore.FileProvider", takeImageFile!!)!!
    }

    private fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) folder.mkdirs()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }

    private fun scannerPicture(context: Context, path: String, fileName: String) {
        // 其次把文件插入到系统图库
        var uri = ""
        try {
            uri = MediaStore.Images.Media.insertImage(context.contentResolver,
                    path, fileName, null)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        if (TextUtils.isEmpty(uri)) {
            return
        }
        val newPath = getFilePathByContentResolver(context, Uri.parse(uri))
        if (TextUtils.isEmpty(newPath)) {
            return
        }
        // 最后通知图库更新
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(newPath))))
    }

    private fun getFilePathByContentResolver(context: Context, uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val c = context.contentResolver.query(uri, null, null, null, null)
        var filePath: String? = null
        if (null == c) {
            throw IllegalArgumentException(
                    "Query on $uri returns null result.")
        }
        try {
            if (c.count != 1 || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            }
        } finally {
            c.close()
        }
        return filePath
    }
}