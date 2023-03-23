package com.gdsc.fourcutalbum.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.adapter.MainSampleAdapter
import com.gdsc.fourcutalbum.adapter.MainSocialSampleAdapter
import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.model.Feed
import com.gdsc.fourcutalbum.data.model.FeedList
import com.gdsc.fourcutalbum.data.model.SignupResponseModel
import com.gdsc.fourcutalbum.databinding.FragmentSocialBinding
import com.gdsc.fourcutalbum.service.HttpService
import com.gdsc.fourcutalbum.ui.activity.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!
    lateinit var context_: Context
    private var mainSocialAdapter: MainSocialSampleAdapter? = MainSocialSampleAdapter()
    private var recyclerViewState: Parcelable? = null

    // Google Login
    private var isLogined = false
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        context?.let { context_ = it }
        isLogined = getLoginState()
        setLogin()

        return binding.root
    }


    // onResume() 에서 저장해둔 리사이클러뷰 상태를 다시 set
    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            binding.rvSocial.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    override fun onPause() {
        super.onPause()
        // LayoutManager를 불러와 Parcelable 변수에 리사이클러뷰 상태를 Bundle 형태로 저장한다
        recyclerViewState = binding.rvSocial.layoutManager?.onSaveInstanceState()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    //TODO :: 로그인 성공 했을 때 (handleSigninResult 메소드 호출시에) 서버에 uid 정보 보내서 회원가입 여부 리턴받기
    //TODO :: 성공시에는 바로 피드 노출, 실패시에는 회원가입 화면 새로 만들어서 nickname과 함께 사용자정보 [회원]생성 API로 보내기


    /* --- 로그인 코드 --- */
    private fun getLoginState() : Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context_)
        account?.let {
            Toast.makeText(context_, "자동 로그인 완료!", Toast.LENGTH_SHORT).show()
            binding.viewLogin.visibility = View.GONE
            return true
        } ?: return false
    }

    private fun setLogin() {
        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context_, gso);

        binding.viewLogin.setOnClickListener { signIn() }

        binding.viewLogin.setOnLongClickListener {

            memberCheckAPI("TEST0004", "TEST0004@naver.com")
            return@setOnLongClickListener true
        }

    }

    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // 정상적으로 결과가 받아와진다면 조건문 실행
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
    }

    private fun memberCheckAPI(UID: String, EMAIL: String){
        Log.d("DBG:RETRO", UID + EMAIL)
        try {
            val data =  HttpService.create(Constants.SERVER_URL).getSignupCheck(UID)
            Log.d("DBG:RETRO", "SENDED")

            data.enqueue(object : Callback<SignupResponseModel?> {
                override fun onResponse(call: Call<SignupResponseModel?>, response: Response<SignupResponseModel?>) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DBG:RETRO", "response : " + response.body().toString())

                        if(response.body()!!.data){
                            Toast.makeText(context_, "환영합니다!", Toast.LENGTH_SHORT).show()
                            binding.viewLogin.visibility = View.GONE

                            setRecyclerView()   // 로그인 성공 시 리스트 생성

                        } else{ // 회원이 존재하지 않으면 false 전달
                            Toast.makeText(context_, "처음이시네요, 닉네임을 설정해 주세요!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(context_, RegisterActivity::class.java)
                            intent.putExtra("UID", UID)
                            intent.putExtra("EMAIL", EMAIL)
                            startActivity(intent)
                            activity?.finish()
                        }

                    }else{
                        Log.d("DBG:RETRO", "response : " + response.toString())
                    }
                }
                override fun onFailure(call: Call<SignupResponseModel?>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        } catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(context_,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()
            val photoUrl = account?.photoUrl.toString()
            val uid = account?.idToken.toString()

            memberCheckAPI(uid, email)

            Log.d("로그인한 유저의 이메일", email)
            Log.d("로그인한 유저의 성", familyName)
            Log.d("로그인한 유저의 이름", givenName)
            Log.d("로그인한 유저의 전체이름", displayName)
            Log.d("로그인한 유저의 프로필 사진의 주소", photoUrl)



        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }

//    private fun signOut() {
//        mGoogleSignInClient.signOut()
//            .addOnCompleteListener(context_) {
//                // ...
//            }
//    }
//
//    private fun revokeAccess() {
//        mGoogleSignInClient.revokeAccess()
//            .addOnCompleteListener(context_) {
//                // ...
//            }
//    }

    private fun GetCurrentUserProfile() {
        val curUser = GoogleSignIn.getLastSignedInAccount(context_)
        curUser?.let {
            val email = curUser.email.toString()
            val familyName = curUser.familyName.toString()
            val givenName = curUser.givenName.toString()
            val displayName = curUser.displayName.toString()
            val photoUrl = curUser.photoUrl.toString()
            val uid = curUser.idToken.toString()

            Log.d("유저의 고유 UID", uid)
            Log.d("유저의 이메일", email)
            Log.d("유저의 성", familyName)
            Log.d("유저의 이름", givenName)
            Log.d("유저의 전체이름", displayName)
            Log.d("유저의 프로필 사진의 주소", photoUrl)
        }
    }
    /* --- 로그인 코드 --- */

    fun setRecyclerView() {
        try {
            val data =  HttpService.create(Constants.SERVER_URL).getFeedList("인생네컷", 2, "test1,test2", 0)
            var `res` : FeedList? = null
            Log.d("DBG:RETRO", "SENDED")

            data.enqueue(object : Callback<FeedList?> {
                override fun onResponse(call: Call<FeedList?>, response: Response<FeedList?>) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DBG:RETRO", "response success: " + response.body().toString())

                        `res` = response.body()

                        mainSocialAdapter?.let {
                            it.setListInit(res!!.feedList)
                        }

                        binding.rvSocial.apply {
                            setHasFixedSize(true)
                            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            adapter = mainSocialAdapter
                        }

                        mainSocialAdapter!!.notifyDataSetChanged()

                    }else{
                        Log.d("DBG:RETRO", "response else: " + response.toString())
                    }
                }
                override fun onFailure(call: Call<FeedList?>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        } catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(context_,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }

    }




}