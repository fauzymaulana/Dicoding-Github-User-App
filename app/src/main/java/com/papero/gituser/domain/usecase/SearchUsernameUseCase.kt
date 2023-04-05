package com.papero.gituser.domain.usecase

import com.papero.gituser.data.remote.SearchResponse
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.HomeRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class SearchUsernameUseCase(private val homeRepository: HomeRepository) {
    fun execute(username: String): Observable<Resource<ArrayList<UserResponse>>>{
        return homeRepository.searchByUsername(username)
    }
}