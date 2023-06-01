package com.papero.gituser.presentation.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.databinding.ItemUserBinding
import com.papero.gituser.domain.data.Favorite

class UserAdapter<T: Any>(private val slug: String = "", private var dataUser: ArrayList<T>) :
    RecyclerView.Adapter<UserAdapter<T>.UserViewHolder>() {

    private lateinit var onItemClickDetail: OnItemClickCallBack
    private lateinit var onItemClickShare: OnItemClickCallBack
    private lateinit var onItemClickFavorite: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickDetail = onItemClickCallBack
        this.onItemClickShare = onItemClickCallBack
        this.onItemClickFavorite = onItemClickCallBack
    }

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    fun setDataUser(items: List<T>) {
        dataUser.clear()
        dataUser.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(holder) {
            with(dataUser[position]) {
                var avatar = ""
                var username = ""
                var type = ""

                when(slug){
                    "follow" -> { binding.imgDetail.visibility = View.GONE }
                    else -> { binding.imgDetail.visibility = View.VISIBLE }
                }
                when(this){
                    is UserResponse -> {
                        avatar = this.avatarUrl.toString()
                        username = this.username.toString()
                        type = this.type.toString()

                        itemView.setOnClickListener { onItemClickDetail.onItemClicked(this) }
                    }
                    is Favorite -> {
                        avatar = this.img.toString()
                        username = this.username.toString()
                        type = if (this.type != null) this.type!! else ""

                        itemView.setOnClickListener { onItemClickDetail.onItemClicked(this) }
                    }
                }
                Glide.with(itemView.context)
                    .load(avatar)
                    .centerCrop()
                    .into(binding.imgUser)

                binding.txtName.text = username
                binding.txtType.text = type
            }

        }
    }

    override fun getItemCount(): Int = dataUser.size

    interface OnItemClickCallBack {
        fun <T: Any> onItemClicked(data: T)
        fun <T: Any>onItemFavorite(data: T)
    }

}