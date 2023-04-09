package com.papero.gituser.presentation.activities.content.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.papero.gituser.R
import com.papero.gituser.data.repository.DetailRepositoryImpl
import com.papero.gituser.databinding.FragmentDetailBinding
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.presentation.activities.adapter.ContentDetailUserAdapter
import com.papero.gituser.presentation.activities.content.home.HomeFragment
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource

class DetailFragment : BaseFragment() {

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
                is Resource.Loading -> {
                    binding.placeholderTopContent.isShimmerStarted
                }
                is Resource.Success -> {

                    if (data?.avatarUrl != null){
                        Glide.with(this)
                            .load(resource.data.avatarUrl)
                            .centerCrop()
                            .into(binding.profileImage)
                        binding.txtJob.isSelected = true
                        binding.txtLocation.isSelected = true
                        binding.txtName.text = (if (data.name == null) getString(R.string.label_content_is_empty, "Name") else  data.name).toString()
                        binding.txtUsername.text =
                            (if (data.username == null) getString(R.string.label_content_is_empty, "Username") else  data.username).toString()
                        binding.txtJob.text = (if (data.company == null) getString(R.string.label_content_is_empty, "Company") else  data.company).toString()
                        binding.txtLocation.text = (if (data.location == null) getString(R.string.label_content_is_empty, "Location") else  data.location).toString()
                        binding.placeholderTopContent.visibility = View.GONE
                        binding.topRoot.visibility = View.VISIBLE
                    }
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
        TabLayoutMediator(tabLayout, vpContent){tab, position ->
            tab.text = labelTabLayout[position]
        }.attach()
    }

    private fun getSelectedUser(){
        val username = arguments?.getString(HomeFragment.USERNAME_KEY)
        if (username != null) {
            viewModel.getDetailUser(username)
            detailAdapter.username = username
        }
    }

    override fun onStart() {
        super.onStart()
        binding.placeholderTopContent.isShimmerStarted
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