package com.papero.gituser.data.repository

import android.annotation.SuppressLint
import com.papero.gituser.data.remote.Search
import com.papero.gituser.data.remote.SearchResponse
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.HomeRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.observable.ObservableAll
import io.reactivex.rxjava3.disposables.CompositeDisposable
//import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeRepositoryImpl(private var requestClient: RequestClient) : HomeRepository {

    override fun getAllData(): Observable<Resource<ArrayList<UserResponse>>> {
        return Observable.create { emitter ->

            emitter.onNext(Resource.Loading())

            requestClient.user().getAllUsers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { emitter.onNext(Resource.Loading(null)) }
                .subscribe({ response ->
                    val data = response ?: arrayListOf()
                    emitter.onNext(Resource.Success(data))
                }, { error ->
                    emitter.onNext(Resource.Error(error.message.toString(), null))
                }).isDisposed

        }
    }

    override fun searchByUsername(username: String): Observable<Resource<SearchResponse>> {
        return Observable.create { emitter ->

            emitter.onNext(Resource.Loading())

            requestClient.user().getSearchUsers(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    emitter.onNext(Resource.Success(response))
                }, { error ->
                    emitter.onNext(Resource.Error(error.message.toString()))
                }).isDisposed
        }
    }
}