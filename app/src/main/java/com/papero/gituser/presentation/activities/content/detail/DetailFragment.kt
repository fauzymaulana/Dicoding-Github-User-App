package com.papero.gituser.presentation.activities.content.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.papero.gituser.domain.usecase.*
import com.papero.gituser.presentation.activities.adapter.TabDetailAdapter
import com.papero.gituser.presentation.activities.content.home.HomeFragment
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.realm.Realm

class DetailFragment : BaseFragment(), View.OnClickListener {

    private var bottomNav: BottomNavigationView? = null
    private lateinit var detailAdapter: TabDetailAdapter

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val requestClient: RequestClient = RequestClient()
    private val detailRepository = DetailRepositoryImpl(requestClient)
    private val detailUserUseCase = DetailUserUseCase(detailRepository)
    private val saveFavUseCase = SaveFavoriteUseCase(detailRepository)
    private val getFavoriteLocalUseCase = GetFavoriteLocalUseCase(detailRepository)
    private val deleteUserUseCase = DeleteFavoriteUseCase(detailRepository)

    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(detailUserUseCase, saveFavUseCase, getFavoriteLocalUseCase, deleteUserUseCase) }

    private var totalFollowers = 0
    private var totalFollowing = 0
    private var username: String? = null
    private var isSaved: Boolean = false

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
        detailAdapter = TabDetailAdapter(parentFragmentManager, lifecycle)

        initListeners()
        getSelectedUser()
        detailResult()
        isFavorite()
    }

    private fun isFavorite() {
        viewModel.getFavoriteLocal(username.toString())
        viewModel.isSaved.observe(viewLifecycleOwner){state ->
            when(state){
                is Resource.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                is Resource.Loading -> {}
                is Resource.Success -> {
                    if (state.data == true){
                        isSaved = true
                        binding.fabFavorite.setImageResource(R.drawable.ic_favorite_active)
                    }else{
                        isSaved = false
                        binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
                    }
                }
            }
        }
    }

    private fun detailResult() {
        viewModel.detailResult.observe(viewLifecycleOwner){resource ->
            val data = resource.data
            when(resource){
                is Resource.Loading -> { binding.placeholderTopContent.isShimmerStarted }
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
        binding.fabFavorite.setOnClickListener(this)
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
        username = arguments?.getString(HomeFragment.USERNAME_KEY)
        if (username != null) {
            viewModel.getDetailUser(username!!)
            detailAdapter.username = username.toString()
        }
    }

    private fun saveFavorite(username: String) {
        viewModel.saveFavorite(username)
        viewModel.saveFavorite.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Error -> Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                is Resource.Loading -> {}
                is Resource.Success -> Toast.makeText(context, resource.data.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFavorite(username: String) {
        viewModel.deleteFavorite(username)
        viewModel.isDeleted.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Error -> Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                is Resource.Loading -> {}
                is Resource.Success -> Toast.makeText(context, resource.data.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.fabFavorite.id -> {
                val iconFab = if (isSaved){
                    deleteFavorite(username.toString())
                    R.drawable.ic_favorite_border
                }else{
                    saveFavorite(username.toString())
                    R.drawable.ic_favorite_active
                }
                binding.fabFavorite.setImageResource(iconFab)
                isSaved = !isSaved
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNav?.visibility = View.GONE
        binding.placeholderTopContent.isShimmerStarted
    }
}

class DetailViewModelFactory(
    private val detailUserUseCase: DetailUserUseCase, private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val getFavoriteLocalUseCase: GetFavoriteLocalUseCase, private val deleteFavoriteUseCase: DeleteFavoriteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(
            detailUserUseCase = detailUserUseCase, saveFavoriteUseCase = saveFavoriteUseCase,
            getFavoriteLocalUseCase = getFavoriteLocalUseCase, deleteFavoriteUseCase = deleteFavoriteUseCase
        ) as T
    }
}