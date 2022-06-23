package com.bzk.dinoteslite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.model.Tag

class CreateFragmentViewModel : ViewModel() {
    var title: String? = null
    var content: String? = null
    var timeCreate = 0L
    var desImage: String? = null
    var imageUri: String? = null
    var motionId = 0
    var tagList: MutableList<Tag> = mutableListOf()
    var isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)

    var listMotion = mutableListOf<Motion>(
        Motion(0, R.drawable.ic_motion_item_fun, R.string.funny),
        Motion(0, R.drawable.ic_motion_item_happy, R.string.happy),
        Motion(0, R.drawable.ic_motion_item_cute, R.string.Cute),
        Motion(0, R.drawable.ic_motion_item_cool, R.string.cool),
        Motion(0, R.drawable.ic_motion_item_wow, R.string.wow),
        Motion(0, R.drawable.ic_motion_item_worried, R.string.diseases),
        Motion(0, R.drawable.ic_motion_item_interesting, R.string.noninteresting),
        Motion(0, R.drawable.ic_motion_item_hate, R.string.hate)
    )


    fun setFavorite(favorite: Boolean) {
        isFavorite.value = favorite
    }

    fun getFavorite(): Boolean {
        return isFavorite.value!!
    }

}