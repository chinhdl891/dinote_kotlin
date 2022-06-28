package com.bzk.dinoteslite.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagConvert {
        @TypeConverter
        fun getStringFromList(tagList: List<TagModel?>?): String? {
            return Gson().toJson(tagList)
        }

        @TypeConverter
        fun tagList(json: String?): List<TagModel>? {
            val gson = Gson()
            val collectionType = object : TypeToken<Collection<TagModel?>?>() {}.type
            val enums: Collection<TagModel> =
                gson.fromJson<Collection<TagModel>>(json, collectionType)
            return enums as List<TagModel>
        }
}