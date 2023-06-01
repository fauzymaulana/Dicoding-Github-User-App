package com.papero.gituser.domain.usecase

import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class GetFavoriteLocalUseCase(private val repository: DetailRepository) {
    fun execute(username: String): Observable<Resource<Boolean>>{
        return repository.getFavorite(username)
    }
}