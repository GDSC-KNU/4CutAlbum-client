package com.gdsc.fourcutalbum.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.gdsc.fourcutalbum.adapter.DetailAdapter
import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FeedList
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.databinding.ActivityDetailBinding
import com.gdsc.fourcutalbum.service.HttpService
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModel
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModelProviderFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity: AppCompatActivity() {
    lateinit var imageUri: Uri
    lateinit var binding: ActivityDetailBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: DetailAdapter
    lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var fourCutsViewModel: FourCutsViewModel
    var postId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInitialize()
        setData()

    }

    fun setData(){
        val id = intent.getIntExtra("id",0)
        postId = id
        val database = FourCutsDatabase.getInstance(this)
        val fourCutsRepository = FourCutsRepositoryImpl(database)
        val factory = FourCutsViewModelProviderFactory(fourCutsRepository)
        fourCutsViewModel = ViewModelProvider(this, factory)[FourCutsViewModel::class.java]
        val fourCuts = fourCutsViewModel.getFourCutsWithId(id)
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fourCuts.collectLatest {
                    Log.d("TEST",it.toString())
                    it.apply {
                        binding.titleTv.text = title
                        binding.locationTv.text = place
                        binding.commentContentTv.text = comment
                        viewAdapter = fourCuts.value.friends?.let { DetailAdapter(it) }!!
                        recyclerView = binding.nameRv.apply {
                            layoutManager = viewManager
                            adapter = viewAdapter
                        }
                        Glide.with(binding.root.context).load(it.photo)
                            .override(Target.SIZE_ORIGINAL)
                            .apply(RequestOptions().override(500, 500))
                            .into(binding.imageIv)
                        if(public_yn.equals("Y")) binding.publicYNtext.text = "전체 공개"
                    }
                }
            }
        }
    }



    fun setInitialize(){
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backIb.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            finish()
        }

        binding.editIb.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("detail_id", postId)
            startActivity(intent)
        }

        binding.imageIv.setOnClickListener {
            clickEvent(it)
        }

    }

    private fun clickEvent(view: View) {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra("id", postId)
        val opt = ActivityOptions.makeSceneTransitionAnimation(this, view, "imgTrans")
        startActivity(intent, opt.toBundle())
    }
    // 이미지를 결과값으로 받는 변수

}