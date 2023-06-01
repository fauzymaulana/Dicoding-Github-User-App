package com.papero.gituser.presentation.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.databinding.ItemUserBinding
import com.papero.gituser.domain.data.Favorite

class UserAdapter(private val slug: String = "", private val dataUser: ArrayList<UserResponse>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var onItemClickDetail: OnItemClickCallBack
    private lateinit var onItemClickShare: OnItemClickCallBack
    private lateinit var onItemClickFavorite: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickDetail = onItemClickCallBack
        this.onItemClickShare = onItemClickCallBack
        this.onItemClickFavorite = onItemClickCallBack
    }

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    fun setDataUser(items: List<UserResponse>) {
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
                when(slug){
                    "follow" -> {
                        binding.imgDetail.visibility = View.GONE
                    }
                    else -> {
                        binding.imgDetail.visibility = View.VISIBLE
//                        binding.checkedFavorite.setOnClickListener { onItemClickFavorite.onItemFavorite(this) }
                    }
                }

//                binding.checkedFavorite.isChecked = this.status

                Glide.with(itemView.context)
                    .load(this.avatarUrl)
                    .centerCrop()
                    .into(binding.imgUser)

                binding.txtName.text = this.username
                binding.txtType.text = this.type
                itemView.setOnClickListener { onItemClickDetail.onItemClicked(this) }
            }

        }
    }

    override fun getItemCount(): Int = dataUser.size

    interface OnItemClickCallBack {
        fun onItemClicked(data: UserResponse)
        fun onItemShared(data: UserResponse)
        fun onItemFavorite(data: UserResponse)
    }

}