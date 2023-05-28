package com.papero.gituser.data.repository

import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class DetailRepositoryImpl(private var requestClient: RequestClient) : DetailRepository {

    override fun getDetailUser(username: String): Observable<Resource<UserDetail>> {
        return requestClient.user().getDetailUsers(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<UserDetail>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
                }

            }
            .startWith(Resource.Loading())
    }

    override fun getFollowers(username: String): Observable<Resource<ArrayList<UserResponse>>> {
        return requestClient.user().getFollowers(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<ArrayList<UserResponse>>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
                }
            }
            .startWith(Resource.Loading())
    }

    override fun getFollowing(username: String): Observable<Resource<ArrayList<UserResponse>>> {
        return requestClient.user().getFollowing(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<ArrayList<UserResponse>>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
                }
            }
            .startWith(Resource.Loading())
    }

//    override fun saveUserToRealm(username: String): Observable<Resource<FavoriteRealm>> {
//        return requestClient
//    }
}