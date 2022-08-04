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
    var title: String = ""
    var content: String = ""
    var timeCreate: Long = System.currentTimeMillis()
    var desImage: String = ""
    var imageUri: String = ""
    var motionId: Int = 0
    var dinote: Dinote? = null
    var lisTagIsEmpty = mutableListOf<TagModel>()
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
        return tagModelList.value!!
    }

    fun setFavorite(favorite: Boolean) {
        isFavorite.value = favorite
    }

    fun getFavorite(): Boolean {
        return isFavorite.value!!
    }

    fun addTag() {
        tagModelList.value?.let {
            tagModelList.value = it.also {
                val size = it.size
                val lastContentTag = it[size - 1].contentTag
                if (lastContentTag.isNotEmpty()) {
                    it.add(TagModel(0, ""))
                }
            }
        }
    }

    fun deleteTag(position: Int) {
        tagModelList.value?.let {
            val size = it.size
            if (size > 1) {
                tagModelList.value = tagModelList.value.also { tagModelList ->
                    tagModelList?.removeAt(position)
                }
            } else {
                tagModelList.value = tagModelList.value.also { tagModelList ->
                    tagModelList?.set(0, TagModel(0, ""))
                }
            }
        }
    }

    fun insertDinote() {
        removeTag()
        dinote = Dinote(0,
            title,
            content,
            imageUri,
            desImage,
            timeCreate,
            motionId,
            isFavorite.value!!,
            setIdForTag())
        dinoteDAO?.onInsert(dinote!!)
    }

    private fun setIdForTag(): List<TagModel> {
        tagModelList.value?.let {
            it.forEach { tagModel ->
                val tag = tagDAO?.getListTag(tagModel.contentTag)?.get(0)
                tagModel.id = tag?.id ?: 0
            }
        }
        return tagModelList.value ?: mutableListOf(TagModel(0, ""))
    }

    fun checkTagIsEmpty(): MutableList<TagModel> {
        tagModelList.value?.forEach {
            lisTagIsEmpty.add(it)
        }
        return lisTagIsEmpty
    }

    private fun removeTag() {
        for (i in getListTag()) {
            if (i.contentTag.isEmpty()) {
                tagModelList.value = tagModelList.value.also {
                    it?.remove(i)
                }
            } else {
                if (tagDAO?.countTag(i.contentTag) == 0) {
                    tagDAO.insert(i)
                }
            }
        }
    }
}