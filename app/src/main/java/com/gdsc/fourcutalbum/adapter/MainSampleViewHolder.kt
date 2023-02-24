package com.gdsc.fourcutalbum.adapter

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.gdsc.fourcutalbum.DetailActivity
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.databinding.ListItemMainBinding
import com.gdsc.fourcutalbum.viewmodel.MainViewModel
import com.gdsc.fourcutalbum.viewmodel.MainViewModelProviderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainSampleViewHolder(private var binding: ListItemMainBinding ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: FourCuts) {
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, DetailActivity::class.java)
            intent.putExtra("id", data.id)
            binding.root.context.startActivity(intent)
        }

        binding.root.setOnLongClickListener {
            val database = FourCutsDatabase.getInstance(binding.root.context)
            val fourCutsRepository = FourCutsRepositoryImpl(database)

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("네컷 삭제")
                .setMessage("해당 네컷을 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        CoroutineScope(Dispatchers.IO).launch{
                            fourCutsRepository.deleteFourCutsWithId(data.id)
                        }
                        Toast.makeText(binding.root.context, "삭제 완료 되었습니다!", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            // 다이얼로그를 띄워주기
            builder.show()

            return@setOnLongClickListener true
        }

        data.photo?.let {
            try{
                if(it.equals("")){
                    Glide.with(binding.root.context).load(R.drawable.ic_baseline_broken_image_24)
                        .into(binding.ivItem)
                }else{
                    Glide.with(binding.root.context).load(it)
                        .override(SIZE_ORIGINAL)
                        .apply(RequestOptions().override(600, 600))
                        .into(binding.ivItem)
                }
            }catch(e:Exception){
                Glide.with(binding.root.context).load(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivItem)
            }
        }
    }
}