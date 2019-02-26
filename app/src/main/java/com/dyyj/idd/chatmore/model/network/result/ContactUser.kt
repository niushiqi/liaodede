package com.dyyj.idd.chatmore.model.network.result

import com.dyyj.idd.chatmore.utils.Utils

data class ContactUser(var name: String, var phone: String) : Comparable<ContactUser> {

//    var headLetter: Char? = Utils.getHeadChar(name)
    var headLetter = lazy { Utils.getHeadChar(name) }

    override fun compareTo(other: ContactUser): Int {
        if (other !is ContactUser) throw ClassCastException()
        val that = other as ContactUser
        if (headLetter.value == '#') {
            return if (that.headLetter.value == '#') {
                0
            } else 1
        }
        if (that.headLetter.value == '#') {
            return -1
        } else if (that.headLetter.value > headLetter.value) {
            return -1
        } else if (that.headLetter.value == headLetter.value) {
            return 0
        }
        return 1
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        if (this === other) {
            return true
        }
        val that = other as ContactUser
        return name == that.name && phone == that.phone
    }
}