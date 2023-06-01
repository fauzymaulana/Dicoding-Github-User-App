package com.papero.gituser.data.repository

import android.util.Log
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserDetail
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.repository.DetailRepository
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import retrofit2.HttpException
import java.io.IOException

class DetailRepositoryImpl(private var requestClient: RequestClient) : DetailRepository {

    private var realm: Realm = Realm.getDefaultInstance()

    override fun getDetailUser(username: String): Observable<Resource<UserDetail>> {
        return requestClient.user().getDetailUsers(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap<Resource<UserDetail>> { response ->
                Observable.just(Resource.Success(response))
            }.onErrorReturn {
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
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
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
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
                when(it){
                    is IOException -> {Resource.Error("Your network is offline")}
                    is Exception -> {Resource.Error("Something went wrong")}
                    else -> {Resource.Error(it.message.toString())}
                }
            }
            .startWith(Resource.Loading())
    }

    override fun saveFavorite(favorite: Favorite): Observable<Resource<FavoriteRealm>> {
        return Observable.create { emitter ->
            try {
                realm.executeTransactionAsync ({ db ->
                    val data = db.where(FavoriteRealm::class.java).equalTo("username", favorite.username).findFirst()
                    data?.apply {
                        username = favorite.username
//                        img = favorite.img
                    }
                    db.copyToRealm(data)
                },{
                    val data = realm.where(FavoriteRealm::class.java).findAll()
                    Log.d("TAG", "saveFavorite: $data")
                },{error ->
                    Log.d("TAG", "saveFavorite: ${error.message}")
                })
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

}