package com.papero.gituser.presentation.activities.content.detail.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.usecase.FollowerUseCase
import com.papero.gituser.domain.usecase.FollowingUseCase
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FollowViewModel(
    private val followerUseCase: FollowerUseCase,
    private val followingUseCase: FollowingUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _followingDataResult = MutableLiveData<Resource<ArrayList<UserResponse>>>()
    val followingDataResult: LiveData<Resource<ArrayList<UserResponse>>>
        get() = _followingDataResult

    private val _followerDataResult = MutableLiveData<Resource<ArrayList<UserResponse>>>()
    val followerDataResult: LiveData<Resource<ArrayList<UserResponse>>>
        get() = _followerDataResult


    fun getFollowingUser(username: String) {
        compositeDisposable.add(
            followingUseCase.execute(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    _followingDataResult.postValue(Resource.Success(response.data))
                }, { error ->
                    _followingDataResult.postValue(Resource.Error(error.toString()))
                })
        )
    }

    fun getFollowerUser(username: String) {
        compositeDisposable.add(
            followerUseCase.execute(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    _followerDataResult.postValue(Resource.Success(response.data))
                }, { error ->
                    _followerDataResult.postValue(Resource.Error(error.toString()))
                })
        )
    }


}