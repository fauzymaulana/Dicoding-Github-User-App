package com.papero.gituser.data.repository

import android.util.Log
import android.widget.Toast
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.repository.FavoriteRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.HttpException
import java.io.IOException

class FavoriteRepositoryImpl(): FavoriteRepository {

    override fun saveFavorite(account: Favorite): Observable<Resource<String>> {
        return Observable.create { emitter ->
            try {
                val realm = Realm.getDefaultInstance()
                val realmThread = Thread.currentThread().name
                Log.d("Realm Thread Rpo", realmThread ?: "Unknown")

                realm.executeTransaction { db ->

                    val data = db.where(FavoriteRealm::class.java).equalTo("username", account.username).findFirst()
                    if (data == null){
                        val fav = db.createObject(FavoriteRealm::class.java)
                        fav.username = account.username
                        fav.img = account.img
                         fav.status = true
                        emitter.onNext(Resource.Success("${account.username} berhasil disimpan"))

                        val d = db.where(FavoriteRealm::class.java).findAll()
                        Log.d("NSKNSFS", "DATA ${d}")
                    }
                }

//                realm.executeTransactionAsync ({ db ->
//                    val data = db.where(FavoriteRealm::class.java).equalTo("username", account.username).findFirst()
//                    data?.apply {
//                        username = data.username
//                        img = data.img
//                        status = true
//                    }
//                    if (data != null) {
//                    db.copyToRealm(data)
//                        emitter.onNext(Resource.Success("${data.username} berhasil disimpan"))
//                    }
//                },{
//                    val data = realm.where(FavoriteRealm::class.java).findAll()
//                    Log.d("TAG", "saveFavorite: $data")
//                },{error ->
//                    Log.d("TAG", "saveFavorite: ${error.message}")
//                })
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

    override fun getFavorites(): Observable<Resource<ArrayList<FavoriteRealm>>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { db ->
                    val favoriteLocal = db.where(FavoriteRealm::class.java).findAll()
                    val favorites = db.copyToRealm(favoriteLocal)
                    emitter.onNext(Resource.Success(ArrayList(favorites)))
                }
            }catch (e: Exception){
                emitter.onError(e)
            } finally {
                realm.close()
                emitter.onComplete()
            }
        }
    }
}