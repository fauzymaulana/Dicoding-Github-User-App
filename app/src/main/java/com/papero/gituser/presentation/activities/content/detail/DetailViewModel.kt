package com.papero.gituser.presentation.activities.content.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.domain.usecase.SaveFavoriteUseCase
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetailViewModel(
    private val detailUserUseCase: DetailUserUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {

    private val _detailResult = MutableLiveData<Resource<UserDetail>>()
    val detailResult: LiveData<Resource<UserDetail>>
        get() = _detailResult

    private val _saveResult = MutableLiveData<Resource<FavoriteRealm>>()
    val saveResult : LiveData<Resource<FavoriteRealm>> = _saveResult

    fun getDetailUser(username: String){
        detailUserUseCase.execute(username)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({response ->
                _detailResult.postValue(Resource.Success(response.data))
            },{error ->
                _detailResult.postValue(Resource.Error(error.message.toString()))
            }).isDisposed
    }

    fun saveFavorite(favorite: Favorite){
        saveFavoriteUseCase.execute(favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({value ->
//                _saveResult.postValue(value)
            },{error ->
                _saveResult.postValue(Resource.Error(error.message.toString()))
            })
    }

}