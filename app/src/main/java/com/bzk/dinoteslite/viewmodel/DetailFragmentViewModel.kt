package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.GlobalApp
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.model.TagModel

private const val TAG = "DetailFragmentViewModel"

class DetailFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var mDinote: Dinote
    var id: Int = 0
    var tagModelList: MutableLiveData<MutableList<TagModel>> =
        MutableLiveData(mutableListOf(TagModel(0, "")))
    var isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)
    var enableView: ObservableField<Boolean> = ObservableField(false)
    var txtUpdate: ObservableField<String> = ObservableField(application.getString(R.string.update))
    var isUpdate: MutableLiveData<Boolean> = MutableLiveData(false)
    var setUpdate: MutableLiveData<Boolean> = MutableLiveData(false)
    var blockView: ObservableField<Int> = ObservableField(ViewGroup.FOCUS_BLOCK_DESCENDANTS)

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
    private val tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()

    fun getListTag(): MutableList<TagModel> {
        return tagModelList.value!!
    }

    fun getFavorite() {
        isFavorite.value = mDinote.isLike
    }

    fun addTag() {
        tagModelList.value = tagModelList.value!!.also {
            val size = tagModelList.value!!.size
            val lastContentTag = tagModelList.value!![size - 1].contentTag
            if (lastContentTag.isNotEmpty()) {
                it.add(TagModel(0, ""))
            }
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

    fun dropDinote(dinote: Dinote) {
        dinoteDAO?.onDelete(dinote)
    }

    fun updateDinote() {
        removeTag()
        mDinote.apply {
            ListTag = getListTag()
            isLike = isFavorite.value!!
        }
        dinoteDAO?.onUpdate(mDinote)
    }

    fun onClickEnableView() {
        enableView.set(true)
        blockView.set(ViewGroup.FOCUS_BEFORE_DESCENDANTS)
        txtUpdate.set(getApplication<GlobalApp>().applicationContext.getString(R.string.save))
        if (isUpdate.value == true) {
            setUpdate.value = true
        }
        isUpdate.value = !isUpdate.value!!
    }

    private fun removeTag() {
        for (i in getListTag()) {
            if (i.contentTag.isEmpty()) {
                tagModelList.value = tagModelList.value.also {
                    if (it?.size!! > 0) {
                        it.remove(i)
                    }
                }
            } else {
                if (tagDAO?.countTag(i.contentTag) == 0) {
                    val tagModel = TagModel(0, i.contentTag)
                    tagDAO.insert(tagModel)
                }
            }
        }
    }

    fun setFavoriteDinote(favorite: Boolean) {
        mDinote.isLike = favorite
        dinoteDAO?.onUpdate(mDinote)
    }
}