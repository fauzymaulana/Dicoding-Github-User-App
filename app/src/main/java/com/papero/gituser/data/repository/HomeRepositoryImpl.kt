package com.papero.gituser.data.repository

import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.HomeRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class HomeRepositoryImpl(private var requestClient: RequestClient) : HomeRepository {

    override fun getAllData(): Observable<Resource<ArrayList<UserResponse>>> {
        return Observable.create { emitter ->

            emitter.onNext(Resource.Loading())

            requestClient.user().getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    val data = response ?: arrayListOf()
                    emitter.onNext(Resource.Success(data))
                }, { error ->
                    when(error){
                        is IOException -> {emitter.onNext(Resource.Error("Your network is offline"))}
                        is Exception -> {emitter.onNext(Resource.Error("Something went wrong"))}
                        else -> {emitter.onNext(Resource.Error(error.message.toString()))}
                    }
                    emitter.onComplete()
                }).isDisposed

        }
    }

    override fun searchByUsername(username: String): Observable<Resource<ArrayList<UserResponse>>> {
        return Observable.create { emitter ->

            emitter.onNext(Resource.Loading())

            requestClient.user().getSearchUsers(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    val data = response.items

                    emitter.onNext(Resource.Success(data))
                    emitter.onComplete()
                }, { error ->
                    when(error){
                        is IOException -> {emitter.onNext(Resource.Error("Your network is offline"))}
                        is Exception -> {emitter.onNext(Resource.Error("Something went wrong"))}
                        else -> {emitter.onNext(Resource.Error(error.message.toString()))}
                    }
                    emitter.onComplete()
                }).isDisposed
        }
    }
}