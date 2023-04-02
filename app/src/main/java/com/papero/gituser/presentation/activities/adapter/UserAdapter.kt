package com.papero.gituser.presentation.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.papero.gituser.R
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.databinding.ItemUserBinding

class UserAdapter(private val dataUser: ArrayList<UserResponse>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

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
                Glide.with(itemView.context)
                    .load(this.avatarUrl)
                    .centerCrop()
                    .into(binding.imgUser)

                binding.txtName.text = this.username
                binding.txtType.text = this.type
            }

        }
    }

    override fun getItemCount(): Int = dataUser.size


}