package com.papero.gituser.presentation.activities.content.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.papero.gituser.R
import com.papero.gituser.data.repository.DetailRepositoryImpl
import com.papero.gituser.data.repository.FavoriteRepositoryImpl
import com.papero.gituser.databinding.FragmentDetailBinding
import com.papero.gituser.domain.usecase.DetailUserUseCase
import com.papero.gituser.domain.usecase.SaveFavoriteUseCase
import com.papero.gituser.presentation.activities.adapter.ContentDetailUserAdapter
import com.papero.gituser.presentation.activities.content.home.HomeFragment
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.realm.Realm

class DetailFragment : BaseFragment(), View.OnClickListener {

    private var bottomNav: BottomNavigationView? = null
    private lateinit var detailAdapter: ContentDetailUserAdapter

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val requestClient: RequestClient = RequestClient()
    private val detailRepository = DetailRepositoryImpl(requestClient)
    private val favoriteRepository = FavoriteRepositoryImpl(Realm.getDefaultInstance())
    private val detailUserUseCase = DetailUserUseCase(detailRepository)
    private val saveFavoriteUseCase = SaveFavoriteUseCase(favoriteRepository)
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(detailUserUseCase, saveFavoriteUseCase) }

    private var totalFollowers = 0
    private var totalFollowing = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = activity?.findViewById(R.id.bottom_nav)
        detailAdapter = ContentDetailUserAdapter(parentFragmentManager, lifecycle)

        initListeners()
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

                        totalFollowers = resource.data.followers!!
                        totalFollowing = resource.data.following!!

                        binding.placeholderTopContent.visibility = View.GONE
                        binding.topRoot.visibility = View.VISIBLE
                    }
                    setupTabLayout()
                }
                is Resource.Error -> {}
            }
        }


    }

    private fun initListeners() {
        binding.floatingActionButton.setOnClickListener(this)
    }

    private fun setupTabLayout(){
        val tabLayout = binding.bottomContent.tabLayout
        val vpContent = binding.bottomContent.vpContent

        vpContent.adapter = detailAdapter

        val labelTabLayout = resources.getStringArray(R.array.label_tab_exams)
        TabLayoutMediator(tabLayout, vpContent){tab, position ->
            when(position){
                0 -> { tab.text = String.format(labelTabLayout[0], totalFollowers.toString())}
                else -> {tab.text = String.format(labelTabLayout[1], totalFollowing.toString())}
            }

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
        bottomNav?.visibility = View.GONE
        binding.placeholderTopContent.isShimmerStarted
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.floatingActionButton.id -> {
                binding.floatingActionButton.setImageResource(R.drawable.ic_favorite_active)
            }
        }
    }
}

class DetailViewModelFactory(
    private val detailUserUseCase: DetailUserUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(
            detailUserUseCase,
            saveFavoriteUseCase
        ) as T
    }
}