package com.papero.gituser.presentation.activities.content.home

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.papero.gituser.R
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.data.repository.HomeRepositoryImpl
import com.papero.gituser.databinding.FragmentHomeBinding
import com.papero.gituser.domain.usecase.AllUserUseCase
import com.papero.gituser.domain.usecase.SearchUsernameUseCase
import com.papero.gituser.presentation.activities.adapter.UserAdapter
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource


class HomeFragment : BaseFragment(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var userAdapter: UserAdapter
    private var searchView: SearchView? = null
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val lists = ArrayList<UserResponse>()
    private val requestClient: RequestClient = RequestClient()
    private val homeRepository: HomeRepositoryImpl = HomeRepositoryImpl(requestClient)
    private val allDatUseCase: AllUserUseCase = AllUserUseCase(homeRepository)
    private val searchUsernameUseCase = SearchUsernameUseCase(homeRepository)
    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(allDatUseCase, searchUsernameUseCase) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllData()
        setListeners()
        setupRecyclerView()
        showData()
        setHasOptionsMenu(true)

        viewModel.sarchResult.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d("HOME", "onViewCreated: ${resource.data?.items}")
//                    val user = resource.data?.items as UserResponse
//                    val userList = ArrayList<UserResponse>()
//                    userList.add(user)
//                    userAdapter.setDataUser(userList)
                }
                is Resource.Error -> {}
            }
        }

//        searchByUsername()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val item: MenuItem = menu.findItem(R.id.search)
        val searchManager = requireActivity().getSystemService(SEARCH_SERVICE) as SearchManager
//         searchView = MenuItemCompat.getActionView(item) as SearchView

        if (item != null) {
            searchView = item.actionView as SearchView
        }
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

    }

    private fun searchByUsername(){
        searchView!!.queryHint = "Find by username"
        if (searchView != null){
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query?.isNotEmpty()!!){
                        viewModel.searchUsername(query.toString().trim())
                        Log.d("HOME", "onQueryTextChange: ${query.toString().trim()}")
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.isNotEmpty()!!){
                        viewModel.searchUsername(newText.toString().trim())
                        Log.d("HOME", "onQueryTextChange: ${newText.toString().trim()}")
                    }
                    return true
                }

            })
        }
    }

    private fun showData() {
        viewModel.allDataResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (resource.data != null) {
                        userAdapter.setDataUser(resource.data)
                    }
                    binding.pbCircular.visibility = View.GONE
                    binding.rvListUser.visibility = View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search -> {
                searchByUsername()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setListeners() {
//        binding.etUsername.setOnClickListener(this)
    }

    private fun setupRecyclerView() {
        binding.rvListUser.layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter(lists)
        binding.rvListUser.adapter = userAdapter
        binding.rvListUser.itemAnimator = DefaultItemAnimator()
        userAdapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            binding.etUsername.id -> {
//                view?.findNavController()?.navigate(R.id.action_homeFragment_to_searchFragment)
////                userAdapter = UserAdapter(arrayListOf())
////                binding.rvListUser.adapter = userAdapter
////                userAdapter.notifyDataSetChanged()
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

class HomeViewModelFactory(
    private val allDatUseCase: AllUserUseCase,
    private val searchUsernameUseCase: SearchUsernameUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
            allDatUseCase,
            searchUsernameUseCase
        ) as T
    }
}