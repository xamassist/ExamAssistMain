package com.ravi.examassistmain.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ravi.examassistmain.R
import com.ravi.examassistmain.animation.animationtypes.ZoomInTransformer
import com.ravi.examassistmain.databinding.ActivityOnBoardingScreenBinding
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.hide
import com.ravi.examassistmain.utils.show
import com.ravi.examassistmain.utils.showToast
import com.ravi.examassistmain.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(){
    private var mAdapter: ViewsSliderAdapter? = null
    private lateinit var layouts: IntArray
    private lateinit var binding: ActivityOnBoardingScreenBinding
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private val rcSignCode = 1

    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        configureGoogleSignIn()
        initObservers()
    }

    private fun initObservers(){

        loginViewModel.userResponse.observe(this){
            when(it){
               is NetworkResult.Success->{
                   showToast(binding.root,"User info fetched")
                }
                is NetworkResult.Error->{
                    showToast(binding.root,"User info fetch failed")

                }
                is NetworkResult.Error->{

                }
            }
        }
        loginViewModel.userSaveResponse.observe(this){
            when(it){
                is NetworkResult.Success->{
                    showToast(binding.root,"User save info fetched")
                }
                is NetworkResult.Error->{
                    showToast(binding.root,"User save info fetch failed")

                }
                is NetworkResult.Error->{

                }
            }
        }
    }

    private fun configureGoogleSignIn(){
        mAuth = FirebaseAuth.getInstance();
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1036639846334-n65t7i0bp59fh1dinipqid0r33pt7dnp.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignIn.setOnClickListener { signIn() }

    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, rcSignCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acc = completedTask.getResult(ApiException::class.java)
            FirebaseGoogleAuth(acc)
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
            FirebaseGoogleAuth(null)
        }
    }

    private fun FirebaseGoogleAuth(acct: GoogleSignInAccount?) {
        //check if the account is null
        if (acct != null) {
            val authCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth?.signInWithCredential(authCredential)?.addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth?.currentUser
                    val isFirstUser = task.result.additionalUserInfo?.isNewUser
                    if (user != null) {
                        updateUI(isFirstUser)
                    }
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        } else {
            //Toast.makeText(this, "acc failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(isFirstTimeUser:Boolean?=false) {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            val userName = account.displayName

            val personEmail = account.email
            val userId = account.id
            val avatarUrl: Uri? = account.photoUrl
            var avatar =""
            if(avatarUrl!=null) {
                avatar = avatarUrl.toString()
            }

            if(userId!=null){
                val user = EAUsers(email =personEmail,userId =userId, userName =userName, userAvatar =avatar )
                createUserProfile(user,isFirstTimeUser?:false)
            }

        }
    }
    fun createUserProfile(eaUser: EAUsers, isFirstTimeUser:Boolean){
        if(isFirstTimeUser){
            loginViewModel.saveUserDataToServer(eaUser)
        }else{
            loginViewModel.insertUserDataInRoom(eaUser)
        }
    startActivity(Intent(this,UserPreferenceActivity::class.java))
    }
    private fun init() {

        layouts = intArrayOf(R.layout.onboarding_one, R.layout.onboarding_two, R.layout.onboarding_three)
        mAdapter = ViewsSliderAdapter()
        binding.onBoardingVP.adapter = mAdapter
        binding.onBoardingVP.registerOnPageChangeCallback(pageChangeCallback)

        binding.wormDotsIndicator.setViewPager2(binding.onBoardingVP)
        binding.onBoardingVP.setPageTransformer(ZoomInTransformer())
       // binding.btnNextScreen.setOnClickListener{startActivity(Intent(this, DashboardActivity::class.java))}

        binding.btnNextScreen.setOnClickListener { _ ->
            val current: Int = getItem(+1)
            if (current < layouts.size-1) {
                binding.onBoardingVP.currentItem = current
            } else {
                binding.btnNextScreen.hide()
                binding.googleSignIn.show()
                launchHomeScreen()
            }
        }
    }
/*
Shortlisted animations
CubeInScalingTransformation
CubeInDepthTransformation 2
CubeInRotationTransformation 3
DepthPageTransformer 4
FidgetSpinTransformation 7
AntiClockSpinTransformation 1
DepthTransformation 5
FanTransformation 6
 */

    private fun getItem(i: Int): Int {
        return binding.onBoardingVP.currentItem + i
    }
    private fun launchHomeScreen() {}

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }


    inner class ViewsSliderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            return SliderViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun getItemViewType(position: Int): Int {
            return layouts[position]
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        inner class SliderViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        }
    }
}
