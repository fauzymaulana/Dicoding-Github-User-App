package com.papero.gituser.domain.usecase

import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.HomeRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

class AllUserUseCase(private val homeRepository: HomeRepository) {
    fun execute(): Observable<Resource<ArrayList<UserResponse>>>{
        return homeRepository.getAllData()
    }
}