package com.gdsc.fourcutalbum


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gdsc.fourcutalbum.databinding.ActivityLoginTestBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginTestActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Not Yet", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityLoginTestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        with(binding) {
            btnSignIn.setOnClickListener {
                signIn()
            }

            btnSignOut.setOnClickListener {
                signOut()
            }
            btnGetProfile.setOnClickListener {
                GetCurrentUserProfile()
            }
        }

        setContentView(binding.root)
    }

    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // ??????????????? ????????? ?????????????????? ????????? ??????
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
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

            Log.d("???????????? ????????? ?????????", email)
            Log.d("???????????? ????????? ???", familyName)
            Log.d("???????????? ????????? ??????", givenName)
            Log.d("???????????? ????????? ????????????", displayName)
            Log.d("???????????? ????????? ????????? ????????? ??????", photoUrl)
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

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    private fun GetCurrentUserProfile() {
        val curUser = GoogleSignIn.getLastSignedInAccount(this)
        curUser?.let {
            val email = curUser.email.toString()
            val familyName = curUser.familyName.toString()
            val givenName = curUser.givenName.toString()
            val displayName = curUser.displayName.toString()
            val photoUrl = curUser.photoUrl.toString()
            val uid = curUser.idToken.toString()

            Log.d("????????? ?????? UID", uid)
            Log.d("????????? ?????????", email)
            Log.d("????????? ???", familyName)
            Log.d("????????? ??????", givenName)
            Log.d("????????? ????????????", displayName)
            Log.d("????????? ????????? ????????? ??????", photoUrl)

        }
    }
}