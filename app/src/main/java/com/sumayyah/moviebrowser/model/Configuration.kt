package com.sumayyah.moviebrowser.model

import com.google.gson.annotations.SerializedName

data class Configuration(
    @SerializedName("images"      ) var imageConfig     : ImageConfig?           = null,
    @SerializedName("change_keys" ) var changeKeys : ArrayList<String> = arrayListOf()
)
