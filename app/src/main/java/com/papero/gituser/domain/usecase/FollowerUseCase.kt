package com.papero.gituser.domain.usecase

import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.domain.repository.HomeRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class FollowerUseCase(private val detailRepository: DetailRepository) {
    fun execute(username: String): Observable<Resource<ArrayList<UserResponse>>>{
        return detailRepository.getFollowers(username)
    }
}