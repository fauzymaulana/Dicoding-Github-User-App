package com.papero.gituser.presentation.activities.content.detail.content

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.papero.gituser.R
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.data.repository.DetailRepositoryImpl
import com.papero.gituser.databinding.FragmentFollowBinding
import com.papero.gituser.databinding.FragmentHomeBinding
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.domain.usecase.FollowerUseCase
import com.papero.gituser.domain.usecase.FollowingUseCase
import com.papero.gituser.presentation.activities.adapter.UserAdapter
import com.papero.gituser.presentation.activities.content.detail.DetailViewModel
import com.papero.gituser.presentation.activities.content.home.HomeFragment
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource

class FollowFragment : BaseFragment() {

    companion object {
        fun newInstance() = FollowFragment()
        const val ARG_POSITION = "position"
        const val USERNAME_KEY = "username_key"
    }

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter
    private val lists = ArrayList<UserResponse>()
    private val requestClient: RequestClient = RequestClient()
    private val detailRepository = DetailRepositoryImpl(requestClient)
    private val followingUseCase = FollowingUseCase(detailRepository)
    private val followerUseCase = FollowerUseCase(detailRepository)
    private val viewModel: FollowViewModel by viewModels { FollowViewModelFactory(followingUseCase = followingUseCase, followerUseCase = followerUseCase) }

    private var position: Int = 0
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(HomeFragment.USERNAME_KEY).toString()
        }

        if (position == 1){
            showFollower()
            viewModel.getFollowerUser(username)

        }else {
            showFollowing()
            viewModel.getFollowingUser(username)
        }
    }

    private fun setupRecyclerView() {
        binding.rvContentFollow.layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter(lists)
        binding.rvContentFollow.adapter = userAdapter
        binding.rvContentFollow.itemAnimator = DefaultItemAnimator()
        userAdapter.notifyDataSetChanged()
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallBack{
            override fun onItemClicked(data: UserResponse) {}
            override fun onItemShared(data: UserResponse) {
            }
        })
    }

    private fun showFollowing(){
        viewModel.followingDataResult.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    if (resource.data != null){
                        if (resource.data.isEmpty()){
                            binding.rvContentFollow.visibility = View.GONE
                            binding.txtErrorResult.text = "Following is Empty"
                        }else {
                            binding.rvContentFollow.visibility = View.VISIBLE
                            userAdapter.setDataUser(resource.data)
                        }
                    }

                }
                is Resource.Error -> {
                    showSnackBarwithAction(
                        R.color.color_error,
                        resource.message.toString(),
                        null
                    ) {}
                }
            }
        }
    }

    private fun showFollower(){
        viewModel.followerDataResult.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    if (resource.data != null){
                        if (resource.data.isEmpty()){
                            binding.rvContentFollow.visibility = View.GONE
                            binding.txtErrorResult.text = "Follower is Empty"
                        }else {
                            binding.rvContentFollow.visibility = View.VISIBLE
                            userAdapter.setDataUser(resource.data)
                        }
                    }
                }
                is Resource.Error -> {
                    showSnackBarwithAction(
                        R.color.color_error,
                        resource.message.toString(),
                        null
                    ) {}
                }
            }
        }
    }

}

class FollowViewModelFactory(
    private val followingUseCase: FollowingUseCase,
    private val followerUseCase: FollowerUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowViewModel(
            followingUseCase = followingUseCase,
            followerUseCase = followerUseCase
        ) as T
    }
}