package com.papero.gituser.presentation.activities.content.favorite

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papero.gituser.R
import com.papero.gituser.data.local.realm.FavoriteRealm
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.data.repository.FavoriteRepositoryImpl
import com.papero.gituser.databinding.FragmentFavoriteBinding
import com.papero.gituser.domain.data.Favorite
import com.papero.gituser.domain.usecase.GetFavoritesLocalUseCase
import com.papero.gituser.presentation.activities.adapter.UserAdapter
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.realm.Realm
import java.util.ArrayList

class FavoriteFragment : BaseFragment() {

    private var bottomNav: BottomNavigationView? = null
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter<Favorite>

    private val realm = Realm.getDefaultInstance()
    private val favoriteRepo = FavoriteRepositoryImpl(realm)
    private val getFavoritesUseCase = GetFavoritesLocalUseCase(favoriteRepo)
    private val viewModel: FavoriteViewModel by viewModels { FavoriteViewModelFactory(getFavoritesUseCase) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = activity?.findViewById(R.id.bottom_nav)
        viewModel.getFavorites()
        showData()
    }

    private fun showData() {
        viewModel.favorites.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Error -> {
                    showSnackBarWithAction(R.color.color_error, resource.message.toString(), null) {}
                }
                is Resource.Loading -> { binding.placeholderRoot.isShimmerStarted}
                is Resource.Success -> {
                    if (resource.data == null || resource.data.isNullOrEmpty()){
                        binding.txtisEmpty.visibility = View.VISIBLE
                        binding.rvFavorite.visibility = View.GONE
                        binding.placeholderRoot.visibility = View.GONE
                        binding.txtisEmpty.text = "Data masih kosong"
                    }else{
                        binding.txtisEmpty.visibility = View.GONE
                        binding.placeholderRoot.visibility = View.GONE
                        binding.rvFavorite.visibility = View.VISIBLE
                        setupRecycler(resource.data)
                    }
                }
            }
        }
    }

    private fun setupRecycler(data: ArrayList<Favorite>) {
        binding.rvFavorite.layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter(dataUser = data)
        binding.rvFavorite.adapter = userAdapter
        binding.rvFavorite.itemAnimator = DefaultItemAnimator()
        userAdapter.notifyDataSetChanged()
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallBack {
            override fun <T: Any> onItemClicked(data: T) = selectedUser(data as Favorite)
            override fun <T: Any> onItemFavorite(data: T) {}

        })
    }

    private fun selectedUser(data: Favorite) {
        val bundle = Bundle()
        bundle.putString(USERNAME_KEY, data.username.toString())
        view?.findNavController()?.navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        bottomNav?.visibility = View.VISIBLE
        binding.placeholderRoot.isShimmerStarted
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        const val USERNAME_KEY = "username_key"
    }
}

class FavoriteViewModelFactory(
    private val getFavorites: GetFavoritesLocalUseCase
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(getFavorites) as T
    }
}