package com.papero.gituser.presentation.activities.content.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.domain.usecase.GetFavoriteLocalUseCase
import com.papero.gituser.domain.usecase.SaveFavoriteUseCase
import com.papero.gituser.domain.usecase.SaveFavoriteUseCases
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(
    private val detailUserUseCase: DetailUserUseCase,
    private val saveFavoriteUseCases: SaveFavoriteUseCases,
    private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val getFavoriteLocalUseCase: GetFavoriteLocalUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _detailResult = MutableLiveData<Resource<UserDetail>>()
    val detailResult: LiveData<Resource<UserDetail>>
        get() = _detailResult

    private val _saveResult = MutableLiveData<Resource<FavoriteRealm>>()
    val saveResult: LiveData<Resource<FavoriteRealm>> = _saveResult

    private val _saveFavorite = MutableLiveData<Resource<String>>()
    val saveFavorite: LiveData<Resource<String>> = _saveFavorite

    private val _isSaved = MutableLiveData<Resource<Boolean>>()
    val isSaved: LiveData<Resource<Boolean>> = _isSaved

    fun getDetailUser(username: String) {
        detailUserUseCase.execute(username)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response ->
                _detailResult.postValue(Resource.Success(response.data))
            }, { error ->
                _detailResult.postValue(Resource.Error(error.message.toString()))
            }).isDisposed
    }

    fun saveFavorites(favorite: Favorite) {
        saveFavoriteUseCases.execute(favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ value ->
//                _saveResult.postValue(value)
            }, { error ->
                _saveResult.postValue(Resource.Error(error.message.toString()))
            })
    }

    fun saveFavorite(username: String) {
        compositeDisposable.add(
            saveFavoriteUseCase.execute(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ value ->
                    _saveFavorite.postValue(value)
                }, { error ->
                    _saveFavorite.postValue(Resource.Error(error.message.toString()))
                })
        )
    }

    fun getFavoriteLocal(username: String){
        compositeDisposable.add(
            getFavoriteLocalUseCase.execute(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({value ->
                    _isSaved.postValue(value)
                },{error ->
                    _isSaved.postValue(Resource.Error(error.toString()))
                })
        )
    }

}