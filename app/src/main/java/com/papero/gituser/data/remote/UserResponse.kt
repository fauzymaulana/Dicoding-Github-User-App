package com.papero.gituser.data.remote

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @SerializedName("avatar_url")
    var avatarUrl: String? = null,

    @SerializedName("login")
    var username: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("type")
    var type: String? = null

)
