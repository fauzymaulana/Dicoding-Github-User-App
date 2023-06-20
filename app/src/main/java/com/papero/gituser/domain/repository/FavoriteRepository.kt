package com.papero.gituser.domain.repository

import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable

interface FavoriteRepository {

    fun saveFavorite(account: Favorite): Observable<Resource<String>>
    fun getFavorites(): Observable<Resource<ArrayList<Favorite>>>
}