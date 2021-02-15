package com.petcaresystem.Fragments


import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petcaresystem.Activities.Base
import com.petcaresystem.Activities.Login
import com.petcaresystem.Activities.Register

import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_fprofile.*
import kotlinx.android.synthetic.main.profile_menu.*
import kotlinx.android.synthetic.main.profile_menu.view.*


class FProfile : Fragment(R.layout.fragment_fprofile), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fAuth : FirebaseAuth
    private lateinit var uID : String
    private val fStore = FirebaseFirestore.getInstance()
    private val fBucket = FirebaseStorage.getInstance()

    lateinit var drawer : DrawerLayout

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fAuth = FirebaseAuth.getInstance()
        drawer = drawer_layout

        profile_menu_nav_view.setNavigationItemSelectedListener(this)
        uID = fAuth.currentUser!!.uid
        if (uID!=null){
            val profileDocRef = fStore.collection("USER").document(uID)
            profileDocRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot>{
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if(task.isSuccessful){
                        val profile = task.result
                        val fullName = profile.getString("USER_FIRST_NAME")+" "+profile.getString("USER_LAST_NAME")
                        profile_full_name.text = fullName
                        profile_user_name.text = profile.getString("USER_NAME")
                        profile_phone.text = profile.getString("USER_PHONE")
                        profile_mail.text = profile.getString("USER_EMAIL")
                        profile_bio.text = profile.getString("USER_BIO")
                        val location = profile.getString("USER_COUNTRY")+", "+profile.getString("USER_STATE")+", "+profile.getString("USER_PIN_CODE")
                        profile_location.text = location

                        val avatarReference = fBucket.reference.child("USER_AVATAR/"+uID+".png")
                        avatarReference.getBytes(1024*1024).addOnSuccessListener(object : OnSuccessListener<ByteArray>{
                            override fun onSuccess(p0: ByteArray?) {
                                val bitmap = BitmapFactory.decodeByteArray(p0,0,p0!!.size)
                                profile_avatar.setImageBitmap(bitmap)
                            }
                        })

                    }
                }
            })

        }


        profile_settings_menu_button.setOnClickListener{
            //inflateSettingsMenu()
            drawer.openDrawer(Gravity.RIGHT)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.edit_profile_menu ->{
                registerIntent()
                drawer.closeDrawer(Gravity.RIGHT)
            }

            R.id.logout_profile_menu ->{
                fAuth.signOut()
                loginIntent()
                drawer.closeDrawer(Gravity.RIGHT)
            }
        }
        return true
    }

    private fun loginIntent(){
        startActivity(Intent(activity, Login::class.java))
        activity?.finish()
    }

    private fun inflateSettingsMenu(){
        val menuLayout = LayoutInflater.from(context).inflate(R.layout.profile_menu,null)
        val build = AlertDialog.Builder(context).setView(menuLayout)
        val menu = build.create()
        menu.window!!.setBackgroundDrawable(object : ColorDrawable(Color.TRANSPARENT){})


        val editProfileButton = menuLayout.edit_profile
        editProfileButton.setOnClickListener {
            registerIntent()
            menu.cancel()
        }
        menu.show()
    }

    private fun registerIntent(){
        startActivity(Intent(activity, Register::class.java))
    }

}
