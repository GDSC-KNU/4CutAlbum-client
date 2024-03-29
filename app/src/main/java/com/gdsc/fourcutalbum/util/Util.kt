package com.gdsc.fourcutalbum.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.gdsc.fourcutalbum.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.ByteArrayOutputStream

class Util {
    fun peopleToValue(people: String) : Int{
        return when(people){
            "혼자서" -> 1
            "둘이서" -> 2
            "셋이서" -> 3
            "넷이서" -> 4
            "다섯명 이상" -> 5
            else -> 1
        }
    }

    fun makeSpinner(spinner : Spinner, array: Int, context : Context){ // 스피너 뷰, 배열 ID, Context
//        val spinner : Spinner = binding.searchStudioSpinner
        val spinnerAdapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context,
            array,
            R.layout.item_spinner_transparent
        );
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = spinnerAdapter
    }

    fun makeDynamicSpinner(spinner : Spinner, dataList: ArrayList<String>, context : Context){ // 스피너 뷰, 배열 ID, Context
//        val spinner : Spinner = binding.searchStudioSpinner
        val spinnerAdapter : ArrayAdapter<String> = ArrayAdapter(context,
            R.layout.item_spinner_transparent,
            dataList
        );
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = spinnerAdapter
    }

    fun makeChipList(group: ChipGroup): ArrayList<String> {
        val chipList = ArrayList<String>()
        for (i: Int in 1..group.childCount) {
            val chip: Chip = group.getChildAt(i - 1) as Chip
            chipList.add(chip.text.toString())
        }
        return chipList
    }

    fun makeChip(str: String, group: ChipGroup, context: Context): Chip {
        val chip = Chip(context)
        chip.apply {
            text = str
            isCloseIconVisible = true
            setOnCloseIconClickListener { group.removeView(this) }
            setCloseIconSize(30f)
            setCloseIconTintResource(R.color.main_color)
            chipStrokeWidth = 2.5f
            setTextAppearance(R.style.chipText)
            setChipMinHeight(100f)
            setChipBackgroundColorResource(R.color.white)
            setChipStrokeColorResource(R.color.gray)
        }
        return chip
    }

    fun bitmapToBase64(bitmap: Bitmap?) : String{

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}