package com.papero.gituser.domain.usecase

import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.repository.FavoriteRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class SaveFavoriteUseCases(private val repository: FavoriteRepository) {
    fun execute(favorite: Favorite): Observable<Resource<String>>{
        return repository.saveFavorite(favorite)
    }
}