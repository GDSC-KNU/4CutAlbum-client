package com.gdsc.fourcutalbum.adapter

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.gdsc.fourcutalbum.ui.activity.DetailActivity
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.databinding.ListItemMainBinding
import com.gdsc.fourcutalbum.service.HttpService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// AlbumFragment
class MainSampleViewHolder(private var binding: ListItemMainBinding ) : RecyclerView.ViewHolder(binding.root) {
    private var deleteFeedFail : Boolean = false

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
                        // 전체 공개 피드였다면 서버에 삭제 요청
                        if (data.public_yn.equals("Y")) {
                            try {
                                val data = HttpService.create("http://3.34.96.254:8080/")
                                    .deleteFeed(data.feed_id!!)
                                data.enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Log.d(
                                                "DBG:RETRO",
                                                "response success: " + response.body().toString()
                                            )
                                        } else {
                                            Log.d(
                                                "DBG:RETRO",
                                                "response else: " + response.toString()
                                            )
                                            deleteFeedFail = true
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        t.printStackTrace()
                                        deleteFeedFail = true
                                    }
                                })

                            } catch (e: Exception) {
                                e.printStackTrace()
                                deleteFeedFail = true
                                Toast.makeText(binding.root.context,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                            }
                        }

                        if (!deleteFeedFail){
                            CoroutineScope(Dispatchers.IO).launch {
                                fourCutsRepository.deleteFourCutsWithId(data.id)
                            }
                            Toast.makeText(binding.root.context, "삭제 완료 되었습니다!", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(binding.root.context, "서버와의 연결이 불안정하여 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()
                        }
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
                        .apply(RequestOptions().override(500, 500))
                        .into(binding.ivItem)
                }
            }catch(e:Exception){
                Glide.with(binding.root.context).load(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivItem)
            }
        }
    }
}