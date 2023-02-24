package com.gdsc.fourcutalbum

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.gdsc.fourcutalbum.databinding.ActivityMainBinding
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gdsc.fourcutalbum.adapter.MainSampleAdapter
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModel
import com.gdsc.fourcutalbum.viewmodel.MainViewModel
import com.gdsc.fourcutalbum.viewmodel.MainViewModelProviderFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var fourCutsViewModel: FourCutsViewModel
    lateinit var mainViewModel: MainViewModel
    var dataList: ArrayList<FourCuts> = arrayListOf()
    private var mainAdapter: MainSampleAdapter? = null
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setInit()
        setAllData()

        // Room db test
//        var intent : Intent = Intent(MainActivity@this, TestActivity::class.java)
//        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()
        Log.d("TEST","START")
    }


    // onResume() 에서 저장해둔 리사이클러뷰 상태를 다시 set
    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            binding.rvMain.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    override fun onPause() {
        super.onPause()
        // LayoutManager를 불러와 Parcelable 변수에 리사이클러뷰 상태를 Bundle 형태로 저장한다
        recyclerViewState = binding.rvMain.layoutManager?.onSaveInstanceState()
    }


    fun setInit() {
        val database = FourCutsDatabase.getInstance(this)
        val fourCutsRepository = FourCutsRepositoryImpl(database)

        val factory = MainViewModelProviderFactory(fourCutsRepository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(
                    MainActivity@ this,
                    EditActivity::class.java
                )
            )
        }


        binding.svMain.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼 누를 때

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                newText?.let {
                    if (it == "") {
                        setAllData()
                        Log.d("room db get log", "getall")
                    } else {
                        setSearchData(it)
                        mainViewModel.searchWord.value?.let { it1 -> Log.d("QUERY", it1) }
                    }
                }
                return true
            }
        })
    }

    fun setSearchData(searchWord: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                mainViewModel.updateValue(searchWord)
                mainViewModel.searchFourCuts(searchWord).collectLatest {
                    Log.d("room db get log1", it.toString())
                    setRecyclerView(it)
                }
            }
        }
    }

    fun setAllData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.getFourCuts.collectLatest {
                    Log.d("room db get log2", it.toString())
                    setRecyclerView(it)
                }
            }
        }
    }

    fun setRecyclerView(data: List<FourCuts>) {
        // init recyclerview
        mainAdapter = MainSampleAdapter()

        mainAdapter?.let {
            it.setListInit(data)
        }

        binding.rvMain.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = mainAdapter
        }

        mainAdapter!!.notifyDataSetChanged()
    }
}