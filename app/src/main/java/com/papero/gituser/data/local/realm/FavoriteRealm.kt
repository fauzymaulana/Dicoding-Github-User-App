package com.papero.gituser.data.local.realm

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class FavoriteRealm: RealmObject() {
    var username: String? = null
    var img: String? = null
    var status: Boolean = false
}