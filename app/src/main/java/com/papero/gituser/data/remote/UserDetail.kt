package com.papero.gituser.data.remote


import com.google.gson.annotations.SerializedName

data class UserDetail(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("company")
    var company: String? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("repositories")
    var repositories: Int? = null,
    @SerializedName("followers")
    var followers: Int? = null,
    @SerializedName("following")
    var following: Int? = null
)
