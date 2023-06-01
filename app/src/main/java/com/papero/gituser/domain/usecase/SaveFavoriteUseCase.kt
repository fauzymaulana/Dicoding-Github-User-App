package com.papero.gituser.domain.usecase

import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class SaveFavoriteUseCase(private val repository: DetailRepository) {
    fun execute(username: String): Observable<Resource<String>> {
        return repository.saveFavorite(username)
    }
}