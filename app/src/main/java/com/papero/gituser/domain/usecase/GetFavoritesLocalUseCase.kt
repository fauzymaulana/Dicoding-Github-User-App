package com.papero.gituser.domain.usecase

import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.domain.repository.FavoriteRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class GetFavoritesLocalUseCase(private val repository: FavoriteRepository) {
    fun execute(): Observable<Resource<ArrayList<FavoriteRealm>>>{
        return repository.getFavorites()
    }
}