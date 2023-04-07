package com.papero.gituser.presentation.activities.content.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.papero.gituser.R
import com.papero.gituser.data.repository.DetailRepositoryImpl
import com.papero.gituser.data.repository.HomeRepositoryImpl
import com.papero.gituser.databinding.FragmentDetailBinding
import com.papero.gituser.databinding.FragmentHomeBinding
import com.papero.gituser.domain.usecase.AllUserUseCase
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.domain.usecase.SearchUsernameUseCase
import com.papero.gituser.presentation.activities.adapter.ContentDetailUserAdapter
import com.papero.gituser.presentation.activities.content.home.HomeFragment
import com.papero.gituser.presentation.activities.content.home.HomeViewModel
import com.papero.gituser.presentation.activities.content.home.HomeViewModelFactory
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource

class DetailFragment : BaseFragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var detailAdapter: ContentDetailUserAdapter

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val requestClient: RequestClient = RequestClient()
    private val detailRepository = DetailRepositoryImpl(requestClient)
    private val detailUserUseCase = DetailUserUseCase(detailRepository)
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(detailUserUseCase) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailAdapter = ContentDetailUserAdapter(parentFragmentManager, lifecycle)
        setupTabLayout()
        getSelectedUser()

        viewModel.detailResult.observe(viewLifecycleOwner){resource ->
            val data = resource.data
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.txtName.text = (if (data?.name == null) "Name is Empty" else  data.name).toString()
                    binding.txtUsername.text =
                        (if (data?.username == null) "Username is Empty" else  data.username).toString()
                    binding.txtJob.text = (if (data?.company == null) "Company is Empty" else  data.company).toString()
                    binding.txtLocation.text = (if (data?.location == null) "Location is Empty" else  data.location).toString()

                    Glide.with(this)
                        .load(resource.data?.avatarUrl)
                        .centerCrop()
                        .into(binding.profileImage)
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun setupTabLayout(){
        val tabLayout = binding.bottomContent.tabLayout
        val vpContent = binding.bottomContent.vpContent

        vpContent.adapter = detailAdapter

        val labelTabLayout = resources.getStringArray(R.array.label_tab_exams)
        if (tabLayout != null && vpContent != null){
            TabLayoutMediator(tabLayout, vpContent){tab, position ->
                tab.text = labelTabLayout[position]
            }.attach()
        }
    }

    private fun getSelectedUser(){
        val username = arguments?.getString(HomeFragment.USERNAME_KEY)
        if (username != null) {
            viewModel.getDetailUser(username)
            Log.d("BUNDLELELE", "getSelectedUser: $username")
            detailAdapter.username = username
        }
    }
}

class DetailViewModelFactory(
    private val detailUserUseCase: DetailUserUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(
            detailUserUseCase
        ) as T
    }
}