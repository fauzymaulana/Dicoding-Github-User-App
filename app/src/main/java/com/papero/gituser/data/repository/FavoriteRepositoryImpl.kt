package com.papero.gituser.data.repository

import android.util.Log
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.repository.FavoriteRepository
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.realm.Realm
import retrofit2.HttpException
import java.io.IOException

class FavoriteRepositoryImpl(private val realm: Realm): FavoriteRepository {

    override fun saveFavorite(account: Favorite): Observable<Resource<String>> {
        return Observable.create { emitter ->
            try {
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { db ->

                    val data = db.where(FavoriteRealm::class.java).equalTo("username", account.username).findFirst()
                    if (data == null){
                        val fav = db.createObject(FavoriteRealm::class.java)
                        fav.username = account.username
                        fav.img = account.img
                         fav.status = true
                        emitter.onNext(Resource.Success("${account.username} berhasil disimpan"))

                        val d = db.where(FavoriteRealm::class.java).findAll()
                    }
                }

            }catch (e: HttpException){
                emitter.onError(e)
            }catch (e: IOException){
                emitter.onError(e)
            }catch (e: Exception){
                emitter.onError(e)
            }finally {
                emitter.onComplete()
            }
        }
    }

    override fun getFavorites(): Observable<Resource<ArrayList<Favorite>>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { db ->
                    val favoriteLocal = db.where(FavoriteRealm::class.java).findAll()
                    val objFav = ArrayList<Favorite>()
                    favoriteLocal.forEach { item ->
                        val fav = Favorite().apply {
                            this.username = item.username
                            this.img = item.img
                            this.status = item.status
                            this.type = item.type
                        }
                        objFav.add(fav)
                    }

                    emitter.onNext(Resource.Success(objFav))
                }
            }catch (e: Exception){
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }
}