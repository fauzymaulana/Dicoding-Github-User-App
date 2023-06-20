package com.papero.gituser.domain.usecase

import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.repository.FavoriteRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class GetFavoritesLocalUseCase(private val repository: FavoriteRepository) {
    fun execute(): Observable<Resource<ArrayList<Favorite>>>{
        return repository.getFavorites()
    }
}