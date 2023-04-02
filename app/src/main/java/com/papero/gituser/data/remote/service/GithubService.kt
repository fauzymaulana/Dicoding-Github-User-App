package com.papero.gituser.data.remote.service

import com.papero.gituser.data.remote.SearchResponse
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.utilities.libraries.Routes
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface GithubService {

    @GET(Routes.SEARCH)
    fun getSearchUsers(@Query("q")query: String): Observable<SearchResponse>

    @GET(Routes.ALL_DATA)
    fun getAllUsers(): Observable<ArrayList<UserResponse>>

    @GET(Routes.DETAIL_DATA+"{username}")
    fun getDetailUsers(@Path("username")username: String): Observable<UserDetail>

}