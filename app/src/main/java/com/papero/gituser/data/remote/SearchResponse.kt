package com.papero.gituser.data.remote

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total_count")
    var totalCount: Int? = null,

    @SerializedName("items")
    var items: ArrayList<UserResponse>? = null
)

data class Search(
    @SerializedName("login")
    var username: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("avatar_url")
    var avatarUrl: String? = null,

    @SerializedName("type")
    var type: String? = null
)

