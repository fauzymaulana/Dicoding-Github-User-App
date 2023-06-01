package com.papero.gituser.presentation.activities.content.favorite

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.papero.gituser.R
import com.papero.gituser.data.repository.FavoriteRepositoryImpl
import com.papero.gituser.databinding.FragmentFavoriteBinding
import com.papero.gituser.domain.usecase.GetFavoritesLocalUseCase
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient

class FavoriteFragment : BaseFragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteRepo = FavoriteRepositoryImpl()
    private val getFavoritesUseCase = GetFavoritesLocalUseCase(favoriteRepo)
    private val viewModel: FavoriteViewModel by viewModels { FavoriteViewModelFactory(getFavoritesUseCase) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavorites()
        showData()
    }

    private fun showData() {
        TODO("Not yet implemented")
    }

}

class FavoriteViewModelFactory(
    private val getFavorites: GetFavoritesLocalUseCase
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(getFavorites) as T
    }
}