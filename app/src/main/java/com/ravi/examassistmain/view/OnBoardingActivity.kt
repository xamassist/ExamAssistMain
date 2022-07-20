package com.ravi.examassistmain.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ravi.examassistmain.R
import com.ravi.examassistmain.animation.animationtypes.ZoomInTransformer
import com.ravi.examassistmain.databinding.ActivityOnBoardingScreenBinding
import com.ravi.examassistmain.utils.Constants
import com.ravi.examassistmain.utils.hide
import com.ravi.examassistmain.utils.show


//@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(){
    private lateinit var context: Context
    private var mAdapter: ViewsSliderAdapter? = null
    private lateinit var layouts: IntArray
    private lateinit var binding: ActivityOnBoardingScreenBinding


    private val signInButton: SignInButton? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val TAG = "OnBoardingActivity"
    private var mAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingScreenBinding.inflate(layoutInflater)
        val view = binding.root
        context = this
        setContentView(view)
        init()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn(){
        mAuth = FirebaseAuth.getInstance();

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1036639846334-n65t7i0bp59fh1dinipqid0r33pt7dnp.apps.googleusercontent.com")

            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInButton.setOnClickListener { signIn() }

    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acc = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                    val user = mAuth?.currentUser
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        } else {
            Toast.makeText(this, "acc failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(fUser: FirebaseUser?) {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            val personName = account.displayName
            val personGivenName = account.givenName
            val personFamilyName = account.familyName
            val personEmail = account.email
            val personId = account.id
            val personPhoto: Uri? = account.photoUrl
            Toast.makeText(this, personName + personEmail, Toast.LENGTH_SHORT).show()
        }
    }
    fun createUserProfile(){

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
            if (current < layouts.size) {
                binding.onBoardingVP.currentItem = current
            } else {
                binding.btnNextScreen.hide()
                binding.signInButton.show()
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
