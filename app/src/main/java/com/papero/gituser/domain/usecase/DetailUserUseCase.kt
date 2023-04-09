package com.papero.gituser.domain.usecase

import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class DetailUserUseCase(private val detailRepository: DetailRepository) {
    fun execute(username: String): Observable<Resource<UserDetail>>{
        return detailRepository.getDetailUser(username)
    }
}