package com.gdsc.fourcutalbum

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.gdsc.fourcutalbum.databinding.FragmentSocialBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!
    lateinit var context_: Context

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

    private fun setLogin(){
        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context_, gso);

         binding.viewLogin.setOnClickListener { signIn() }
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

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()
            val photoUrl = account?.photoUrl.toString()

            Log.d("로그인한 유저의 이메일", email)
            Log.d("로그인한 유저의 성", familyName)
            Log.d("로그인한 유저의 이름", givenName)
            Log.d("로그인한 유저의 전체이름", displayName)
            Log.d("로그인한 유저의 프로필 사진의 주소", photoUrl)

            Toast.makeText(context_, "$givenName 님 환영합니다!", Toast.LENGTH_SHORT).show()
            binding.viewLogin.visibility = View.GONE

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




}