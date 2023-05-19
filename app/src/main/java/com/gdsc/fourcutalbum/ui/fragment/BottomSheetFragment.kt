package com.gdsc.fourcutalbum.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.isEmpty
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.service.OnDataSelectedListener
import com.gdsc.fourcutalbum.databinding.FragmentBottomSheetBinding
import com.gdsc.fourcutalbum.service.HttpService
import com.gdsc.fourcutalbum.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.*

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var listener: OnDataSelectedListener? = null

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    lateinit var context_: Context
    private var util = Util()
    // 검색 파라미터
    private var people : String = "전체"
    private var studio : String = "전체"
    private var hashtags : MutableList<String> = mutableListOf()

    override fun getTheme(): Int = R.style.BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        context?.let { context_ = it }
        initSearchStudioSpinner()
        initSearchPeopleSpinner()
        initSearchHashtagSpinner()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as OnDataSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDataSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun sendData(people: Int, studio: String, hashtags: ArrayList<String>) {
        listener?.onDataSelected(people, studio, hashtags)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.reset.setOnClickListener {  // hashtag clear
            binding.searchStudioSpinner.setSelection(0)
            binding.searchPeopleSpinner.setSelection(0)
            binding.searchHashtagSpinner.setSelection(0)
            if(!hashtags.isEmpty()) hashtags.clear()
            if(!binding.searchHashtagGroup.isEmpty()){
                while(binding.searchHashtagGroup.childCount>0)
                    binding.searchHashtagGroup.removeView(binding.searchHashtagGroup.getChildAt(binding.searchHashtagGroup.childCount-1))
            }
        }
        binding.searchBtn.setOnClickListener {
            // 서버로 검색요청 날리기
            // 검색파라미터: peopleValue, studio, hashtagsList
            var peopleValue = 0
            if(!people.equals("전체"))
                peopleValue = util.peopleToValue(people) // String --> Int
            val hashtagsList = hashtags.toList()

            // "전체"인 경우 파라미터x, 아닌것만 처리...? 흠 어렵다
            Log.d("params:::", peopleValue.toString())
            Log.d("params:::", studio.toString())
            Log.d("params:::", hashtagsList.toString())

            sendData(peopleValue, studio, hashtags as ArrayList<String>)
            dismiss()
        }

    }
    private fun initSearchStudioSpinner() {
        val spinner : Spinner = binding.searchStudioSpinner
        requireContext()?.let {
            util.makeSpinner(spinner, R.array.sstudio, it)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    studio = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }

    private fun initSearchPeopleSpinner() {
        val spinner : Spinner = binding.searchPeopleSpinner
        requireContext()?.let {
            util.makeSpinner(spinner, R.array.speople, it)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    people = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }
    private fun initSearchHashtagSpinner(){
        var hashtag : String? = ""
        val spinner : Spinner = binding.searchHashtagSpinner
        val group = binding.searchHashtagGroup
        val hashtagList : MutableList<String> = mutableListOf()
        fun makeChip(str: String, group: ChipGroup, context: Context): Chip {
            val chip = Chip(context)
            chip.apply {
                text = "# " + str
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    group.removeView(this)
                    hashtagList.remove(str) // 검색 파라미터에서 삭제
                    if(hashtags.isEmpty()) binding.searchHashtagSpinner.setSelection(0)
                }
                setCloseIconSize(30f)
                setCloseIconTintResource(R.color.main_color)
                chipStrokeWidth = 2.5f
                setTextAppearance(R.style.chipText)
                setTextSize(11.1f)
                setChipMinHeight(200f)
                setChipBackgroundColorResource(R.color.white)
                setChipStrokeColorResource(R.color.gray)
            }
            return chip
        }
        requireContext()?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val deferredList: Deferred<ArrayList<String>> = async(Dispatchers.IO) {
                    val data = HttpService.create("http://3.34.96.254:8080/").getHashtags()
                    val dataList = data.execute().body()!!.hashtags
                    dataList
                }
                launch{
                    val dataList = deferredList.await()
                    dataList.add(0, "전체")
                    Log.d("DBG:RETRO", dataList.toString())
                    if(dataList.isEmpty()) util.makeSpinner(spinner, R.array.shashtag, it)
                    else util.makeDynamicSpinner(spinner, dataList, it)
                }
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    hashtag = parent.getItemAtPosition(position).toString()
                    if(!hashtag.equals("전체")){
                        Log.d("해시태그:::", hashtags.toString())
                        Log.d("해시태그:::", hashtags.contains(hashtag).toString())
                        if(hashtags.contains(hashtag)){
                            Toast.makeText(context, "같은 해시태그는 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            group.addView(hashtag?.let { it1 -> makeChip(it1, group, it) })
                            hashtag?.let { it1 -> hashtagList.add(it1) }
                        }
                    }else{
                        if(!hashtags.isEmpty() && !binding.searchHashtagGroup.isEmpty())
                            hashtags.clear()
                        while(binding.searchHashtagGroup.childCount>0)
                            binding.searchHashtagGroup.removeView(binding.searchHashtagGroup.getChildAt(binding.searchHashtagGroup.childCount-1))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }

        hashtags = hashtagList

    }

}
