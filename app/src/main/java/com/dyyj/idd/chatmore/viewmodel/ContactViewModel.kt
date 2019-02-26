package com.dyyj.idd.chatmore.viewmodel

import android.content.Context
import android.provider.ContactsContract
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.ContactUser
import com.dyyj.idd.chatmore.ui.adapter.ContactAdapter
import com.dyyj.idd.chatmore.utils.Util
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*


class ContactViewModel : ViewModel() {

    var mMatchData: List<String>? = null
    val mMatchCurrentUserData: ArrayList<ContactUser> = arrayListOf()

    var UserSelect: ContactUser? = null

    private val mAdapter by lazy { ContactAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    private fun getContactMessage(context: Context) {
        Observable.create(ObservableOnSubscribe<List<ContactUser>> { subscriber ->
            try {
                val cursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)
                if (cursor != null) {
                    val nameId = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                    val numberId = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val contacts = ArrayList<ContactUser>()
                    while (cursor!!.moveToNext()) {
                        val username = cursor!!.getString(nameId).trim()
                        val number = cursor!!.getString(numberId).trim().replace(" ", "").replace("　", "")
                        if (!Util.isMobile(number)) {
                            continue
                        }
                        if ((mMatchData != null) and (mMatchData?.size != 0)) {
                            if (mMatchData?.contains(number)!!) {
                                mMatchCurrentUserData.add(ContactUser(username, number))
                            } else {
                                contacts.add(ContactUser(username, number))
                            }
                        } else {
                            contacts.add(ContactUser(username, number))
                        }
                    }
                    contacts.sort()
                    subscriber.onNext(contacts)
                }
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    EventBus.getDefault().post(ContactVM(it))
                }
    }

    fun netSendMessage(phone: String) {
        val subscribe = mDataRepository.biaodaSendMsg(phone).subscribe(
                {EventBus.getDefault().post(ContactSendVM(it.errorCode == 200))},
                {EventBus.getDefault().post(ContactSendVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netUploadContact(phones: String) {
        val subscribe = mDataRepository.uploadContact(phones).subscribe(
                {
//                    Toast.makeText(ChatApp.getInstance().niceChatContext(), it.errorCode.toString(), Toast.LENGTH_SHORT).show()
                },{}
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netGetMatch(context: Context) {
        val subscribe = mDataRepository.biaobaiMatch().subscribe(
                {
                    mMatchData = it.data
                    getContactMessage(context)
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    class ContactSendVM(val success: Boolean)
    class ContactVM(val obj: List<ContactUser>)

}