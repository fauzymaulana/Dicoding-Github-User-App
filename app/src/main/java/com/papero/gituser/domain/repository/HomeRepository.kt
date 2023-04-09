package com.papero.gituser.domain.repository

import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable


interface HomeRepository {

    fun getAllData(): Observable<Resource<ArrayList<UserResponse>>>
    fun searchByUsername(username: String): Observable<Resource<ArrayList<UserResponse>>>
}