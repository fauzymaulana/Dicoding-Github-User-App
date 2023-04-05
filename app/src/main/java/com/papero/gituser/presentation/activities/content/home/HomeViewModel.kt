package com.papero.gituser.presentation.activities.content.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.papero.gituser.data.remote.SearchResponse
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.usecase.AllUserUseCase
import com.papero.gituser.domain.usecase.SearchUsernameUseCase
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
    private val allUserUseCase: AllUserUseCase,
    private val searchUsernameUseCase: SearchUsernameUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _allDataResult = MutableLiveData<Resource<ArrayList<UserResponse>>>()
    val allDataResult: LiveData<Resource<ArrayList<UserResponse>>>
        get() = _allDataResult

    private val _searchResult = MutableLiveData<Resource<ArrayList<UserResponse>>>()
    val searchResult: LiveData<Resource<ArrayList<UserResponse>>>
        get() = _searchResult

    fun getAllData() {
        _allDataResult.postValue(Resource.Loading())
        compositeDisposable.add(
            allUserUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ response ->
                    _allDataResult.postValue(Resource.Success(response.data))
                },{error ->
                    _allDataResult.postValue(Resource.Error(error.toString()))
                })
        )
    }

    fun searchUsername(username: String) {

        compositeDisposable.add(
            searchUsernameUseCase.execute(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    _searchResult.postValue(Resource.Success(response.data))
                }, { error ->
                    _searchResult.postValue(Resource.Error(error.toString()))
                })
        )
    }
}