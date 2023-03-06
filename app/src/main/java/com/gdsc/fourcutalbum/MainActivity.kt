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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gdsc.fourcutalbum.adapter.MainSampleAdapter
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModel
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModelProviderFactory
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

    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupJetpackNavigation()


        // Room db test
//        var intent : Intent = Intent(MainActivity@this, TestActivity::class.java)
//        startActivity(intent)

        val database = FourCutsDatabase.getInstance(this)
        val fourCutsRepository = FourCutsRepositoryImpl(database)

        val factory = MainViewModelProviderFactory(fourCutsRepository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupJetpackNavigation(){
        // 내비게이션 컨트롤러 인스턴스 취득
        val host = supportFragmentManager
            .findFragmentById(R.id.fourcutalbum_nav_host) as NavHostFragment? ?: return
        navController = host.navController
        // 내비게이션 뷰를 내비게이션 컨트롤러와 연결
        binding.bottomNavigationView.setupWithNavController(navController)
    }

}