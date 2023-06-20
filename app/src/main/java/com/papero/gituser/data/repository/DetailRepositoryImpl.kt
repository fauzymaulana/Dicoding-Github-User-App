package com.papero.gituser.data.repository

import android.util.Log
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.io.IOException

class DetailRepositoryImpl(private var requestClient: RequestClient) : DetailRepository {


    override fun getDetailUser(username: String): Observable<Resource<UserDetail>> {
        return requestClient.user().getDetailUsers(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<UserDetail>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when (it) {
                    is IOException -> {
                        Resource.Error("Your network is offline")
                    }
                    is Exception -> {
                        Resource.Error("Something went wrong")
                    }
                    else -> {
                        Resource.Error(it.message.toString())
                    }
                }
            }
            .startWith(Resource.Loading())
    }

    override fun getFollowers(username: String): Observable<Resource<ArrayList<UserResponse>>> {
        return requestClient.user().getFollowers(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<ArrayList<UserResponse>>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when (it) {
                    is IOException -> {
                        Resource.Error("Your network is offline")
                    }
                    is Exception -> {
                        Resource.Error("Something went wrong")
                    }
                    else -> {
                        Resource.Error(it.message.toString())
                    }
                }
            }
            .startWith(Resource.Loading())
    }

    override fun getFollowing(username: String): Observable<Resource<ArrayList<UserResponse>>> {
        return requestClient.user().getFollowing(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<ArrayList<UserResponse>>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when (it) {
                    is IOException -> {
                        Resource.Error("Your network is offline")
                    }
                    is Exception -> {
                        Resource.Error("Something went wrong")
                    }
                    else -> {
                        Resource.Error(it.message.toString())
                    }
                }
            }
            .startWith(Resource.Loading())
    }

    override fun saveFavorite(username: String): Observable<Resource<String>> {
        return requestClient.user().getDetailUsers(username)
            .subscribeOn(Schedulers.io())
            .flatMap<Resource<String>> { value ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction{ db ->
                    val info = db.where(FavoriteRealm::class.java).equalTo("username", username)
                        .findFirst()
                    if (info == null) {
                        val saveFav = db.createObject(FavoriteRealm::class.java)
                        saveFav.username = value.username
                        saveFav.img = value.avatarUrl
                        saveFav.type = value.type
                        saveFav.status = true
                    }
                }
                realm.close()
                Observable.just(Resource.Success("$username berhasil disimpan"))
            }
            .onErrorReturn { error ->
                when (error) {
                    is IOException -> {
                        Resource.Error("Your network is offline")
                    }
                    is Exception -> {
                        Resource.Error("Something went wrong")
                    }
                    else -> {
                        Resource.Error(error.message.toString())
                    }
                }
            }
            .startWith(Resource.Loading())
    }

    override fun getFavorite(username: String): Observable<Resource<Boolean>> {
        return Observable.create { emitter ->
            try {
                val realm = Realm.getDefaultInstance()
                val findFav = realm.where(FavoriteRealm::class.java).equalTo("username", username).findFirst()
                val saved = findFav != null
                realm.close()
                emitter.onNext(Resource.Success(saved))
            }catch (e: Exception){
                emitter.onError(e)
            } finally {
              emitter.onComplete()
            }
        }
    }

    override fun deleteFavorite(username: String): Observable<Resource<String>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { db ->
                    val findFav = db.where(FavoriteRealm::class.java).equalTo("username", username).findFirst()
                    findFav?.deleteFromRealm()
                    emitter.onNext(Resource.Success("Berhasil menghapus $username dari daftar disukai"))
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