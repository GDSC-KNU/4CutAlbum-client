package com.gdsc.fourcutalbum.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.databinding.ActivityMainBinding
import com.gdsc.fourcutalbum.viewmodel.MainViewModel
import com.gdsc.fourcutalbum.viewmodel.MainViewModelProviderFactory


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

        if(intent.getStringExtra("MODE") == "LOGIN") setLoginState()

    }

    private fun setupJetpackNavigation() {
        // 내비게이션 컨트롤러 인스턴스 취득
        val host = supportFragmentManager
            .findFragmentById(R.id.fourcutalbum_nav_host) as NavHostFragment? ?: return
        navController = host.navController
        // 내비게이션 뷰를 내비게이션 컨트롤러와 연결
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    fun setLoginState(){
        navController.navigate(R.id.fragment_social)
    }

}