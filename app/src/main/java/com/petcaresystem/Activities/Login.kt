package com.petcaresystem.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.mukesh.OnOtpCompletionListener
import com.petcaresystem.Fragments.FLogin
import com.petcaresystem.Fragments.FRegister
import com.petcaresystem.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.authenticate.*
import kotlinx.android.synthetic.main.authenticate.view.*
import kotlinx.android.synthetic.main.fragment_flogin.*
import kotlinx.android.synthetic.main.fragment_fregister.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    private val fManager = supportFragmentManager
    private val fLogin = FLogin()
    private val fRegister = FRegister()
    private lateinit var activeFragment : Fragment
    private lateinit var fAuth : FirebaseAuth
    private lateinit var fStore : FirebaseFirestore
    private lateinit var verificationId : String
    private lateinit var forceResendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var context : Context
    private lateinit var credential: PhoneAuthCredential
    private var uID : FirebaseUser? = null
    private var verificationInProgress = false
    private var OTP : String? = ""
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var phoneNumber : String
    private lateinit var passWord : String
    private lateinit var confirmPassword : String
    private lateinit var ccP : String
    private var NUMBER_ALREADY_REGISTERED = false
    private var completeNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this@Login
        activeFragment = fLogin
        fAuth = FirebaseAuth.getInstance()
        uID = fAuth.currentUser
        if (uID!=null){
            baseIntent()
        }
        fStore = FirebaseFirestore.getInstance()
        fragment_heading.setText("Log In")

        fManager.beginTransaction().apply {
            add(R.id.login_frame,fLogin,"login").show(fLogin)
            add(R.id.login_frame,fRegister,"register").hide(fRegister)
        }.commit()

        left_shift.setOnClickListener {
            if (activeFragment == fRegister){
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).hide(activeFragment).show(fLogin).commit()
                activeFragment = fLogin
                left_arrow_green.visibility = View.GONE
                left_arrow_gray.visibility = View.VISIBLE
                right_arrow_gray.visibility = View.GONE
                right_arrow_green.visibility = View.VISIBLE
                fragment_heading.setText("Log In")
            }
        }

        right_shift.setOnClickListener {
            if(activeFragment==fLogin){
                right_arrow_green.visibility = View.GONE
                right_arrow_gray.visibility = View.VISIBLE
                left_arrow_gray.visibility = View.GONE
                left_arrow_green.visibility = View.VISIBLE
                fragment_heading.setText("Register")
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).hide(activeFragment).show(fRegister).commit()
                activeFragment = fRegister
            }
        }

        card_button.setOnClickListener {
            try{
                if (fragment_heading.text == "Register"){
                    userName = user_name_register_text.text.toString()
                    userEmail = email_register_text.text.toString()
                    phoneNumber = phone_number_register_text.text.toString()
                    passWord = password_register_text.text.toString()
                    confirmPassword = confirm_password_register_text.text.toString()
                    ccP = ccp.getSelectedCountryCode()

                    if (userName.isNullOrEmpty() ||
                        userEmail.isNullOrEmpty() ||
                        phoneNumber.isNullOrEmpty()||
                        passWord.isNullOrEmpty()||
                        confirmPassword.isNullOrEmpty()){
                        makeToast("please fill all entries")
                    }else{
                        if (phoneNumber.length < 10){
                            makeToast("invalid number")
                        }else{
                            try{
                                completeNumber = "+"+ccP+phoneNumber
                                if (PasswordValidator(passWord)){
                                    if (passWord.equals(confirmPassword)){
                                        isNumberRegistered()
                                    }else{
                                        fRegister.progressText("Password don't match")
                                    }
                                }else{
                                    makeToast("please follow password guidelines")
                                }
                            }catch(e : Exception) {
                                Log.d("login otp build",e.toString())
                            }
                        }
                    }

                }

                if (fragment_heading.text.toString() == "Log In"){
                    val loginUserEmail = user_name_login_text.text.toString()
                    val loginUserPassword = user_password_login_text.text.toString()
                    fLogin.loginProgressTab("Logging in...",true,"")
                    fAuth.signInWithEmailAndPassword(loginUserEmail,loginUserPassword)
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                            override fun onComplete(task: Task<AuthResult>) {
                                if (task.isSuccessful){
                                    fLogin.loginProgressTab("login successful",false,"")
                                    baseIntent()
                                }else{
                                    fLogin.loginProgressTab("login successful",false,task.exception.toString())
                                    Log.d("login_task","failed")
                                }
                            }
                        })
                }
            }catch (e : Exception){
                Log.d("login",e.toString())
            }
        }

    }

    override fun onResume() {
        super.onResume()
        permission_call()
    }

    private fun makeToast(msg : String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    private fun requestOTP(phoneNumber : String){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("otp","onVerificationCompleted called")
                fRegister.progressText("number already registered please use other one")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("otp",e.toString())
                fRegister.SomethingWrong()
            }

            override fun onCodeSent(Id: String, token: PhoneAuthProvider.ForceResendingToken){
                super.onCodeSent(Id, token)
                verificationInProgress = true
                fRegister.sendOTP()
                inflateAuth()
                fRegister.closeProgress()
                verificationId = Id
                forceResendToken = token
                Log.d("otp","oncodesent called")
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                verificationInProgress = false
                Log.d("otp","oncodesent called")
            }
        }

        val options = PhoneAuthOptions.newBuilder(fAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun inflateAuth(){
        val otpScreen = LayoutInflater.from(context).inflate(R.layout.authenticate,null)
        val build = AlertDialog.Builder(context).setView(otpScreen)
        val screen = build.create()
        val otpValidateButton = otpScreen.otp_verify_button
        val countDown = otpScreen.countdown
        val resend = otpScreen.resend
        otpValidateButton.setOnClickListener {
            verifyAuth()
        }
        val otpBox = otpScreen.otp_view
        otpBox.setOtpCompletionListener( object : OnOtpCompletionListener{

            override fun onOtpCompleted(otp: String?) {
                otpValidateButton.setEnabled(true)
                OTP = otp
            }
        })
        screen.window!!.setBackgroundDrawable(object : ColorDrawable(Color.TRANSPARENT){})
        screen.show()

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDown.text = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString()
            }
            override fun onFinish() {
                resend.setTextColor(Color.BLUE)
            }
        }.start()
    }

    private fun verifyAuth(){
        try{
            credential = PhoneAuthProvider.getCredential(verificationId,OTP.toString())
            val emailPasswordCredentialLink = EmailAuthProvider.getCredential(userEmail,passWord)
            fAuth.signInWithCredential(credential).addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(task: Task<AuthResult>) {
                    fAuth.currentUser!!.linkWithCredential(emailPasswordCredentialLink)
                    if (task.isSuccessful){
                        Log.d("otp","successfully verified")
                        registerData(fAuth.currentUser!!.uid)
                    }else{
                        Log.d("otp","invalid OTP")
                    }
                }
            } )
        }catch (e : Exception){
            Log.d("otp",e.toString())
            Log.d("otp",verificationId)
        }
    }

    private fun registerData(id : String){
        val docReference = fStore.collection("USER").document(id)
        val user = HashMap<String,String>()
        user.put("USER_NAME",userName)
        user.put("USER_EMAIL",userEmail)
        user.put("USER_PHONE",phoneNumber)
        user.put("USER_PASSWORD",passWord)

        docReference.set(user).addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(task: Task<Void>) {
                if ( task.isSuccessful){
                    registryIntent()
                }else{
                    Log.d("register","task failed")
                }
            }
        })
    }

    private fun PasswordValidator(password : String):Boolean{
        val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@_.]).*$"
        val pattern : Pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return !password.matches(".*\\\\d.*".toRegex()) || !matcher.matches()
    }

    private fun isNumberRegistered(){
        Log.d("Number_Registered","function called")
        val NUMBER_SNAPSHOT = fStore.collection("REGISTERED_PHONE").document(completeNumber)
        NUMBER_SNAPSHOT.addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                if(value !=null && value.exists()){
                    NUMBER_ALREADY_REGISTERED = true
                    Log.d("Number_Registered",NUMBER_ALREADY_REGISTERED.toString())
                    fRegister.progressText("Number Already Registered")
                }else{
                    if (!verificationInProgress){
                        fRegister.sendOTP()
                        fAuth.signOut()
                        requestOTP(completeNumber)
                    }else{
                        inflateAuth()
                        fRegister.progressText("can't inflate")
                    }
                }
            }
        })
    }

    private fun permission_call(){
        val permission = listOf(Manifest.permission.CAMERA,
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
        for (x in  permission){
            if (ContextCompat.checkSelfPermission(this,x)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(x),0)
            }
        }
    }

    private fun baseIntent(){
        startActivity(Intent(this@Login, Base::class.java))
        finish()
    }

    private fun registryIntent(){
        startActivity(Intent(this@Login,Register::class.java))
        finish()
    }
}
