package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDAO
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.model.TagModel

private const val TAG = "CreateFragmentViewModel"

class CreateFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var title: String = "No Title"
    var content: String = "hello"
    var timeCreate: Long = System.currentTimeMillis()
    var desImage: String = "no Des"
    var imageUri: String = "no"
    var motionId: Int = 0

    var tagModelList: MutableLiveData<MutableList<TagModel>> =
        MutableLiveData(mutableListOf(TagModel(0, "")))
    var isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)

    var listMotion = mutableListOf<Motion>(
        Motion(0, R.drawable.ic_motion_item_fun, R.string.funny),
        Motion(1, R.drawable.ic_motion_item_happy, R.string.happy),
        Motion(2, R.drawable.ic_motion_item_cute, R.string.Cute),
        Motion(3, R.drawable.ic_motion_item_cool, R.string.cool),
        Motion(4, R.drawable.ic_motion_item_wow, R.string.wow),
        Motion(5, R.drawable.ic_motion_item_worried, R.string.diseases),
        Motion(6, R.drawable.ic_motion_item_interesting, R.string.noninteresting),
        Motion(7, R.drawable.ic_motion_item_hate, R.string.hate)
    )

    val dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    val tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()

    fun getListTag(): MutableList<TagModel> {
        Log.d(TAG, "getListTag: ${tagModelList.value!!.size}")
        return tagModelList.value!!
    }

    fun setFavorite(favorite: Boolean) {
        isFavorite.value = favorite
    }

    fun getFavorite(): Boolean {
        return isFavorite.value!!
    }

    fun addTag() {
        tagModelList.value = tagModelList.value!!.also {
            it.add(TagModel(0, ""))
        }
    }

    fun deleteTag(position: Int) {
        val size = tagModelList.value!!.size
        if (size > 1) {
            tagModelList.value = tagModelList.value!!.also {
                it.removeAt(position)
            }
        } else {
            tagModelList.value = tagModelList.value!!.also {
                it[0] = TagModel(0, "")
            }
        }
    }

    fun insertDinote() {
        removeTag()
        val dinote = Dinote(0, title, content, imageUri, desImage, timeCreate, motionId, isFavorite.value!!, getListTag())
        dinoteDAO?.onInsert(dinote)

    }

    private fun removeTag() {
        for (i in getListTag()) {
            if (i.contentTag.isEmpty()) {
                tagModelList.value = tagModelList.value.also {
                    it!!.remove(i)
                }
            } else {
                if (tagDAO?.countTag(i.contentTag)!! == 0) {
                    tagDAO.insert(i)
                }
            }
        }
    }
}