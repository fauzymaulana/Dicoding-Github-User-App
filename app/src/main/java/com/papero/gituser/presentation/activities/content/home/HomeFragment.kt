package com.papero.gituser.presentation.activities.content.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papero.gituser.CoreApplication
import com.papero.gituser.R
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.data.repository.FavoriteRepositoryImpl
import com.papero.gituser.data.repository.HomeRepositoryImpl
import com.papero.gituser.databinding.FragmentHomeBinding
import com.papero.gituser.domain.usecase.AllUserUseCase
import com.papero.gituser.domain.usecase.SaveFavoriteUseCase
import com.papero.gituser.domain.usecase.SearchUsernameUseCase
import com.papero.gituser.presentation.activities.adapter.UserAdapter
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource
import io.realm.Realm


class HomeFragment : BaseFragment() {

    companion object {
        const val USERNAME_KEY = "username_key"
    }

    private var bottomNav: BottomNavigationView? = null
    private var checkFavorite: CheckBox? = null
    private lateinit var userAdapter: UserAdapter
    private var searchView: SearchView? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val lists = ArrayList<UserResponse>()
    private val requestClient: RequestClient = RequestClient()
    private val homeRepository = HomeRepositoryImpl(requestClient)
    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val favRepository = FavoriteRepositoryImpl(realm)
    private val allDatUseCase: AllUserUseCase = AllUserUseCase(homeRepository)
    private val searchUsernameUseCase = SearchUsernameUseCase(homeRepository)
    private val saveFavUseCase= SaveFavoriteUseCase(favRepository)

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            allDatUseCase,
            searchUsernameUseCase,
            saveFavUseCase
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = activity?.findViewById(R.id.bottom_nav)
//        val realm = (requireActivity().applicationContext as CoreApplication)
        checkFavorite = view.findViewById(R.id.checkedFavorite)
        (activity as AppCompatActivity).setSupportActionBar(binding.contentToolbar.toolbar)
        viewModel.getAllData()
        showData()
        val realmThread = Thread.currentThread().name
        Log.d("Realm Thread", realmThread ?: "Unknown")
        setHasOptionsMenu(true)
        searchResult()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val toolbar = binding.contentToolbar.toolbar
        toolbar.inflateMenu(R.menu.main_menu)
        val searchItem = toolbar.menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun searchResult() {
        viewModel.searchResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.placeholderRoot.isShimmerStarted
                    isVisibleView(View.VISIBLE, binding.placeholderRoot, binding.txtSearchResult)
                    binding.rvListUser.visibility = View.GONE
                    binding.txtSearchResult.text = getString(R.string.label_searching)
                }
                is Resource.Success -> {
                    if (resource.data?.isEmpty() == true) {
                        isVisibleView(View.GONE, binding.placeholderRoot, binding.txtSearchResult)
                        binding.txtSearchResult.visibility = View.VISIBLE
                        binding.txtSearchResult.text = getString(R.string.label_message)
                    } else {
                        if (resource.data != null) {
                            userAdapter.setDataUser(resource.data)
                        }
                        binding.rvListUser.visibility = View.VISIBLE
                        binding.txtSearchResult.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    binding.rvListUser.visibility = View.GONE
                    binding.txtSearchResult.visibility = View.VISIBLE
                    binding.txtSearchResult.text = resource.message.toString()
                }
            }
        }
    }

    private fun searchByUsername() {
        searchView!!.queryHint = "Find by username"
        if (searchView != null) {
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.isNotEmpty()!!) {
                        viewModel.searchUsername(newText.toString().trim())
                    } else if (newText.isEmpty()) {
                        showData()
                    }
                    return true
                }
            })
        }
    }

    private fun showData() {
        viewModel.allDataResult.observe(requireActivity()) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.placeholderRoot.isShimmerStarted
                }
                is Resource.Success -> {
                    if (resource.data != null) {
                        setupRecyclerView(resource.data)
                        isVisibleView(View.GONE, binding.placeholderRoot)
                    }
                    isVisibleView(View.VISIBLE, binding.rvListUser)
                }
                is Resource.Error -> {
                    showSnackBarWithAction(
                        R.color.color_error,
                        resource.message.toString(),
                        null
                    ) {}
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                searchByUsername()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView(data: ArrayList<UserResponse>) {
        binding.rvListUser.layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter(dataUser = data)
        binding.rvListUser.adapter = userAdapter
        binding.rvListUser.itemAnimator = DefaultItemAnimator()
        userAdapter.notifyDataSetChanged()
        userAdapter.setOnItemClickCallback(object :
            UserAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: UserResponse) = selectedUser(data)
            override fun onItemFavorite(data: UserResponse) = saveFavorite(data)
            override fun onItemShared(data: UserResponse) {}
        })
    }

    private fun saveFavorite(data: UserResponse) {
        viewModel.saveFavorite(data)
        viewModel.saveFavorite.observe(viewLifecycleOwner){resource ->
            val message = when(resource){
                is Resource.Error -> resource.message.toString()
                is Resource.Loading -> ""
                is Resource.Success -> {
                    checkFavorite?.isChecked = true
                    resource.data.toString()
                }
            }
            Log.d("TAG", "saveFavorite: $message")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun selectedUser(username: UserResponse) {
        val bundle = Bundle()
        bundle.putString(USERNAME_KEY, username.username.toString())
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        bottomNav?.visibility = View.VISIBLE
        binding.placeholderRoot.isShimmerStarted
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        _binding = null
    }
}

class HomeViewModelFactory(
    private val allDatUseCase: AllUserUseCase,
    private val searchUsernameUseCase: SearchUsernameUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
            allDatUseCase,
            searchUsernameUseCase,
            saveFavoriteUseCase
        ) as T
    }
}