package com.papero.gituser.domain.repository

import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

interface DetailRepository {

    fun getDetailUser(username: String): Observable<Resource<UserDetail>>
    fun getFollowers(username: String): Observable<Resource<ArrayList<UserResponse>>>
    fun getFollowing(username: String): Observable<Resource<ArrayList<UserResponse>>>

    fun saveFavorite(username: String): Observable<Resource<String>>

    fun getFavorite(username: String): Observable<Resource<Boolean>>
}