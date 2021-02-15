package com.petcaresystem.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import androidx.fragment.app.Fragment
import com.fxn.OnBubbleClickListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.petcaresystem.Fragments.*
import com.petcaresystem.R
import kotlinx.android.synthetic.main.activity_base.*
import java.io.File

class Base : AppCompatActivity() {

    val fMap = FMap()
    val fBlog = FBlog()
    val fChat = FChat()
    val fProfile = FProfile()
    val fAsk = FAsk()

    lateinit  var activeFragment : Fragment
    private lateinit var analyticsFirebase : FirebaseAnalytics
    val fManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        analyticsFirebase = FirebaseAnalytics.getInstance(this)
        MainDirectory()

        activeFragment = fBlog

        fManager.beginTransaction().apply {
            add(R.id.base_frag_frame, fBlog, "Blog").show(fBlog)
            add(R.id.base_frag_frame, fMap, "Map").hide(fMap)
            add(R.id.base_frag_frame, fChat, "Chat").hide(fChat)
            add(R.id.base_frag_frame,fAsk,"Ask").hide(fAsk)
            add(R.id.base_frag_frame,fProfile,"Profile").hide(fProfile)

        }.commit()


        main_navigation.addBubbleListener(object : OnBubbleClickListener {

            override fun onBubbleClick(item: Int) {
                when (item) {
                    R.id.nav_map -> {
                        fManager.beginTransaction().hide(activeFragment).show(fMap).commit()
                        activeFragment = fMap
                    }
                    R.id.nav_blog -> {
                        fManager.beginTransaction().hide(activeFragment).show(fBlog).commit()
                        activeFragment = fBlog
                    }
                    R.id.nav_chat -> {
                        fManager.beginTransaction().hide(activeFragment).show(fChat).commit()
                        activeFragment = fChat
                    }
                    R.id.nav_ask -> {
                        fManager.beginTransaction().hide(activeFragment).show(fAsk).commit()
                        activeFragment = fAsk
                    }
                    R.id.nav_profile -> {
                        fManager.beginTransaction().hide(activeFragment).show(fProfile).commit()
                        activeFragment = fProfile
                    }
                }
            }
        })
    }

    private fun MainDirectory(){
        val rootdir = Environment.getExternalStorageDirectory().toString()
        val dir = File(rootdir+"/PCS/Image/Test")
        if (!dir.isDirectory){
            dir.mkdirs()
        }

    }

    override fun onBackPressed() {
        if(fProfile.drawer.isDrawerOpen(Gravity.RIGHT)){
            fProfile.drawer.closeDrawer(Gravity.RIGHT)
        }else{
            super.onBackPressed()
        }
    }
}
