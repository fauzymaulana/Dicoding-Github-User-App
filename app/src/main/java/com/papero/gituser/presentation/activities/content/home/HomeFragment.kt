package com.papero.gituser.presentation.activities.content.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.papero.gituser.R
import com.papero.gituser.data.remote.UserResponse
import com.papero.gituser.data.repository.HomeRepositoryImpl
import com.papero.gituser.databinding.FragmentHomeBinding
import com.papero.gituser.domain.usecase.AllUserUseCase
import com.papero.gituser.presentation.activities.adapter.UserAdapter
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.network.RequestClient
import com.papero.gituser.utilities.stateHandler.Resource

class HomeFragment : BaseFragment(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var userAdapter: UserAdapter

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val lists = ArrayList<UserResponse>()
    private val requestClient: RequestClient = RequestClient()
    private val homeRepository: HomeRepositoryImpl = HomeRepositoryImpl(requestClient)
    private val allDatUseCase: AllUserUseCase = AllUserUseCase(homeRepository)
    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(allDatUseCase) }

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
    }

    private fun showData() {
        viewModel.allDataResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> { binding.pbCircular.visibility = View.VISIBLE }
                is Resource.Success -> {
                    if (resource.data != null){
                        showDataItems(resource.data)
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

    private fun setListeners() {
        binding.etUsername.setOnClickListener(this)
    }

    private fun setupRecyclerView() {
            binding.rvListUser.layoutManager = LinearLayoutManager(activity)
            userAdapter = UserAdapter(lists)
            binding.rvListUser.adapter = userAdapter
        binding.rvListUser.itemAnimator = DefaultItemAnimator()
            userAdapter.notifyDataSetChanged()
    }

    private fun showDataItems(users: ArrayList<UserResponse>){
        userAdapter.setDataUser(users)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.etUsername.id -> {
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_searchFragment)
//                userAdapter = UserAdapter(arrayListOf())
//                binding.rvListUser.adapter = userAdapter
//                userAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

class HomeViewModelFactory(
    private val allDatUseCase: AllUserUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
            allDatUseCase
        ) as T
    }
}