package com.gdsc.fourcutalbum.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.*
import com.gdsc.fourcutalbum.data.repository.FourCutsRepositoryImpl
import com.gdsc.fourcutalbum.databinding.ActivityEditBinding
import com.gdsc.fourcutalbum.service.HttpService
import com.gdsc.fourcutalbum.util.Util
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModel
import com.gdsc.fourcutalbum.viewmodel.FourCutsViewModelProviderFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.magical.near.common.util.SharedManager
import kotlinx.coroutines.flow.collectLatest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class EditActivity : AppCompatActivity() {
    private val binding: ActivityEditBinding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    lateinit var fourCutsViewModel: FourCutsViewModel
    lateinit var context_: Context
    private var imageUri: Uri = Uri.EMPTY
    private var studio: String? = null // for Spinner
    private var people: String? = null // for Spinner
    private var public_yn : String = "N"
    private var util = Util()
    lateinit var base64Image : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context_ = applicationContext
        SharedManager.init(applicationContext)

        val database = FourCutsDatabase.getInstance(this)
        val fourCutsRepository = FourCutsRepositoryImpl(database)

        val factory = FourCutsViewModelProviderFactory(fourCutsRepository)
        fourCutsViewModel = ViewModelProvider(this, factory)[FourCutsViewModel::class.java]

        val id = intent.getIntExtra("detail_id", 0)
        if (id > 0) setData(id)

        initStudioSpinner()
        initPeopleSpinner()

        binding.imageIv.setOnClickListener {
            selectGallery()
        }
        binding.editPlusBtn.setOnClickListener {
            makeDialog(binding.editFriendGroup)
        }
        binding.editPublicSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                View.VISIBLE.also {
                    public_yn = "Y"
                    binding.editPeopleDes.visibility = it
                    binding.editPeopleSpinner.visibility = it
                    binding.editHashtagDes.visibility = it
                    binding.editHashtagLayout.visibility = it }
            }else{
                View.GONE.also {
                    public_yn = "N"
                    binding.editPeopleDes.visibility = it
                    binding.editPeopleSpinner.visibility = it
                    binding.editHashtagDes.visibility = it
                    binding.editHashtagLayout.visibility = it }
            }
        }

        binding.editPlusBtn2.setOnClickListener {
            if (binding.editHashtagGroup.childCount < 3) makeDialog2(binding.editHashtagGroup)
            else Toast.makeText(this, "해시태그는 3개까지만 등록할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.editSaveButton.setOnClickListener {
            val chipList = util.makeChipList(binding.editFriendGroup)
            val hashtagList = util.makeChipList(binding.editHashtagGroup)
            var isSuccessCreateFeed = true
            if (imageUri == Uri.EMPTY)
                Snackbar.make(it, "사진을 등록해주세요!", Snackbar.LENGTH_SHORT).show()
            else { // 사진이 있는 경우 저장 진행

                // 전체 공개 여부 관련없이 Room DB저장
                val fourCuts =
                    FourCuts(
                        binding.editTitle.text.toString(),
                        imageUri,
                        chipList.toList(),
                        studio,
                        binding.editComment.text.toString(),
                        public_yn,
                        people?.let { it1 -> util.peopleToValue(it1) },
                        hashtagList
                    )
                if (id > 0) fourCutsViewModel.updateFourCuts(
                    fourCuts.title,
                    fourCuts.photo,
                    fourCuts.friends,
                    fourCuts.place,
                    fourCuts.comment,
                    fourCuts.public_yn,
                    fourCuts.people,
                    fourCuts.hashtag,
                    id)
                else fourCutsViewModel.saveFourCuts(fourCuts)

                if(public_yn.equals("Y")){
                    // 전체공개 하는 경우 - 로그인 체크
                    val loginUID = SharedManager.read(SharedManager.LOGIN_ID, "1")!!
                    if(loginUID == "1"){
                        // 로그인 되어있지 않은 경우
                        // dialog --> 로그인이 필요합니다. 로그인 하시겠습니까?
                        // N -> 그냥 dialog 닫기, 저장 진행 x
                        val builder = AlertDialog.Builder(binding.root.context)
                        builder.setTitle("로그인이 필요합니다.")
                        builder.setMessage("로그인 하시겠습니까?")
                        builder.setPositiveButton("예") { dialog, which ->
                            //login
                            finish()
                            Toast.makeText(applicationContext, "둘러보기 탭을 클릭하여 로그인을 해주세요!", Toast.LENGTH_SHORT).show()
                        }
                        builder.setNegativeButton("아니오") { _, _ -> }
                        builder.show()

                    }else{
                        // 로그인 되어 있는 경우 - 피드 생성 요청
                        try {
                            // TODO ::: UID 받아오기, Image 코드 넣기
                            // TODO ::: UID 받아오려면 Member Check도 해야하고, 회원가입 여부도 check해야하고... ---> Social에 있는 코드를 밖으로 빼는건 어떤지

                            // TODO ::: GLACIER -> 로그인시에 UID와 Email을 SharedPreference에 저장하여 사용하는 방식은 어떤지? 일단 적용해놨습니다
                            val model = CreateFeedRequestModel(loginUID,
                                base64Image, "test.jpeg",
                            hashtagList, util.peopleToValue(people!!), studio!!, binding.editComment.text.toString())
                            Log.d("Model.image:::", model.image)
                            val data =  HttpService.create(Constants.SERVER_URL).createFeed(model)
                            var url : CreateFeedResponseModel? = null
                            Log.d("DBG:RETRO", "SENDED ${model.toString()}")

                            data.enqueue(object : Callback<CreateFeedResponseModel?> {
                                override fun onResponse(call: Call<CreateFeedResponseModel?>, response: Response<CreateFeedResponseModel?>) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.d("DBG:RETRO", "response success: " + response.body().toString())
                                        url = response.body()

                                    }else{
                                        Log.d("DBG:RETRO", "response else: " + response.toString())
                                    }
                                    finish()
                                }
                                override fun onFailure(call: Call<CreateFeedResponseModel?>, t: Throwable) {
                                    t.printStackTrace()
                                    isSuccessCreateFeed = false
                                }
                            })

                        } catch (e:Exception){
                            e.printStackTrace()
                            isSuccessCreateFeed = false
                            Toast.makeText(context_,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                        }

                    }

                }

                // 서버 저장 실패시 전체 여부를 N으로 하고 나만보기로 저장
                if(public_yn.equals("Y")&&!isSuccessCreateFeed){
                    // 전체공개인데 서버 전송 실패
                    Toast.makeText(this, "서버 전송에 실패하였습니다. 나만 보기로 저장됩니다.", Toast.LENGTH_SHORT).show()
                    public_yn="N"
                    finish()
                }


            }

        }

    }

    private fun initStudioSpinner() {
        val spinner : Spinner = binding.editStudioSpinner
        util.makeSpinner(spinner, R.array.studio, this)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                studio = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun initPeopleSpinner() {
        val spinner : Spinner = binding.editPeopleSpinner
        util.makeSpinner(spinner, R.array.people, this)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                people = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun setData(id: Int) {
        //Log.d(":::", "set data")
        // val fourCuts = fourCutsViewModel.getFourCutsWithId(id)
        val fourCuts = fourCutsViewModel.getFourCutsWithID(id)
        var position = 0

        lifecycleScope.launchWhenCreated {
            fourCuts.collectLatest {
                it.apply {
                    for(x : String in resources.getStringArray(R.array.studio)){
                        if(place.equals(x))
                            position = resources.getStringArray(R.array.studio).indexOf(x)
                    }

                    binding.editTitle.setText(title)
                    binding.editStudioSpinner.setSelection(position)
                    binding.editComment.setText(comment)
                    binding.editPublicSwitch.isChecked = public_yn.equals("Y")
                    binding.editPeopleSpinner.setSelection(people?.minus(1) ?: 0)

                    // setting
                    val cnt = binding.editFriendGroup.childCount
                    for (i: Int in 1..cnt) { // clear
                        binding.editFriendGroup.removeView(binding.editFriendGroup.getChildAt(0) as Chip)

                    }
                    for (friend: String in friends!!) binding.editFriendGroup.addView(util.makeChip(
                        friend, binding.editFriendGroup, this@EditActivity))

                    val cnt2 = binding.editHashtagGroup.childCount
                    for (i: Int in 1..cnt2) { // clear
                        binding.editHashtagGroup.removeView(binding.editHashtagGroup.getChildAt(0) as Chip)

                    }
                    if(!hashtag.isNullOrEmpty()) { // 기존 데이터 대비
                        for (tag: String in hashtag!!) binding.editHashtagGroup
                            .addView(util.makeChip(tag, binding.editHashtagGroup, this@EditActivity)
                        )
                    }

                    Glide.with(binding.root.context).load(it.photo)
                        .override(Target.SIZE_ORIGINAL)
                        .into(binding.imageIv)

                    binding.imageIv.background = null
                    imageUri = it.photo

                    // 이미지 uri를 bitmap으로 변경한다.
                    var bitmap : Bitmap? = null
                    try{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context_.contentResolver, imageUri))
                            Log.d("bitmap:::", bitmap.toString())
                        }else{ // API 29 이하
                            bitmap = MediaStore.Images.Media.getBitmap(context_.contentResolver, imageUri)
                        }
                    }catch(e : IOException){
                        e.printStackTrace()
                    }
                    // bitmap 이미지를 서버 전송을 위한 base64로 인코딩한다.
                    base64Image = util.bitmapToBase64(bitmap)
                }
            }
        }
    }

    private fun makeDialog(group: ChipGroup) {
        val et = EditText(this)
        et.maxLines = 1
        et.inputType = InputType.TYPE_CLASS_TEXT
        et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        et.gravity = Gravity.CENTER
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 100
        params.rightMargin = 100
        params.height = 200
        et.layoutParams = params
        container.addView(et)
        val builder = AlertDialog.Builder(this)
            .setTitle("친구 이름을 적어주세요")
            .setView(container)
            .setPositiveButton("추가") { dialog, which ->
                val string = et.text.toString()
                if (string.isEmpty()) {
                    Toast.makeText(this, "chip 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    group.addView(util.makeChip(string, group, this))
                }
            }
            .setNegativeButton("취소") { dialog, which ->
            }
        builder.show()
    }

    private fun makeDialog2(group: ChipGroup) { // Dialog - Spinner
        val sp = Spinner(this)
        var hashtag : String? = ""
        sp.setBackgroundResource(R.drawable.spinner)
        util.makeSpinner(sp, R.array.hashtag, this)
        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hashtag = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        Log.d("hashtag::", hashtag.toString())
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 100
        params.rightMargin = 100
        params.topMargin = 50
        params.height = 150
        sp.layoutParams = params
        container.addView(sp)
        val builder = AlertDialog.Builder(this)
            .setTitle("해시태그를 선택해주세요")
            .setView(container)
            .setPositiveButton("추가") { dialog, which ->
                val string = hashtag
                if (string!!.isEmpty()) {
                    Toast.makeText(this, "해시태그를 선택해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    group.addView(util.makeChip(string, group, this))
                }
            }
            .setNegativeButton("취소") { dialog, which ->
            }
        builder.show()
    }


    companion object {
        const val REVIEW_MIN_LENGTH = 10

        // 갤러리 권한 요청
        const val REQ_GALLERY = 1

        // API 호출시 Parameter key값
        const val PARAM_KEY_IMAGE = "image"
        const val PARAM_KEY_PRODUCT_ID = "product_id"
        const val PARAM_KEY_REVIEW = "review_content"
        const val PARAM_KEY_RATING = "rating"
    }

    private fun selectGallery() {
        val writePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        //권한 확인
        if (writePermission == PackageManager.PERMISSION_DENIED ||
            readPermission == PackageManager.PERMISSION_DENIED
        ) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQ_GALLERY
            )

        } else {
            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // uri realpath 권한 이슈 해결
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            // intent의 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )

            imageResult.launch(intent)
        }
    }


    // 이미지를 결과값으로 받는 변수
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            var bitmap : Bitmap? = null
            // 이미지를 받으면 ImageView에 적용한다
            val temp = result.data?.data
            if (temp != null) {
                imageUri = temp
            }
            imageUri?.let {
                // 이미지를 불러온다
                Glide.with(this)
                    .load(temp)
                    .fitCenter()
                    .apply(RequestOptions().override(500, 500))
                    .into(binding.imageIv)
                // 이미지 uri를 bitmap으로 변경한다.
                try{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context_.contentResolver, it))
                    }else{ // API 28 미만
                        bitmap = MediaStore.Images.Media.getBitmap(context_.contentResolver, it)
                    }
                }catch(e : IOException){
                    e.printStackTrace()
                }

            }
            // bitmap이미지를 서버 전송을 위한 base64로 인코딩한다.
            base64Image = util.bitmapToBase64(bitmap)
            binding.imageIv.background = null
        }
    }

    // 키보드 바깥을 누르면 키보드를 숨김
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
