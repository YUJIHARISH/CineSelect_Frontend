package com.saveetha.cineselect.data

import com.google.gson.annotations.SerializedName

data class CastMember(
    val name: String,
    @SerializedName("profile_path") val profilePath: String?
)
