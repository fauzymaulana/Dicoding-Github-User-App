package com.papero.gituser.presentation.activities.content.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(
    private val detailUserUseCase: DetailUserUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _detailResult = MutableLiveData<Resource<UserDetail>>()
    val detailResult: LiveData<Resource<UserDetail>>
        get() = _detailResult

    fun getDetailUser(username: String){
        detailUserUseCase.execute(username)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({response ->
                _detailResult.postValue(Resource.Success(response.data))
            },{error ->
                _detailResult.postValue(Resource.Error(error.message.toString()))
            })
    }

}