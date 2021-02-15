package com.petcaresystem.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.petcaresystem.Fragments.*
import com.petcaresystem.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.left_arrow_gray
import kotlinx.android.synthetic.main.activity_register.left_arrow_green
import kotlinx.android.synthetic.main.activity_register.right_arrow_gray
import kotlinx.android.synthetic.main.activity_register.right_arrow_green
import kotlinx.android.synthetic.main.fragment_freg1.*
import kotlinx.android.synthetic.main.fragment_freg3.*
import kotlinx.android.synthetic.main.fragment_freg4.*
import java.io.ByteArrayOutputStream

class Register : AppCompatActivity() {
    private val fReg1 = FReg1()
    private val fReg2 = FReg2()
    private val fReg3 = FReg3()
    private val fReg4 = FReg4()
    private val fReg5 = FReg5()
    private val fReg6 = FReg6()

    private lateinit var activeFragment : Fragment

    private val fManager = supportFragmentManager
    private val fList = arrayListOf<Fragment>(fReg1,fReg2,fReg3,fReg4,fReg5,fReg6)
    private var fFlag = 0

    private lateinit var profileAvatar  : Bitmap
    private lateinit var hospitalID : Bitmap
    private lateinit var ngoID      : Bitmap

    private lateinit var fAuth  : FirebaseAuth
    private lateinit var fStorage : FirebaseStorage
    private lateinit var fData  : FirebaseFirestore

    private lateinit var userID : String

    private lateinit var USER_FIRST_NAME : String
    private lateinit var USER_LAST_NAME  : String
    private lateinit var USER_COUNTRY : String
    private lateinit var USER_STATE : String
    private lateinit var USER_PIN_CODE : String
    private var USER_AVATAR_LINK : Uri? = null
    private var USER_PET_OWNER : Boolean = false
    private lateinit var USER_PET_CATEGORY : String
    private lateinit var USER_PET_SPECIES : String
    private var USER_BELONG_VETERNARY : Boolean = false
    private lateinit var USER_VETERNARY_HOSPITAL_NAME : String
    private lateinit var USER_VETERNARY_HOSPITAL_COUNTRY : String
    private lateinit var USER_VETERNARY_HOSPITAL_STATE : String
    private lateinit var USER_VETERNARY_HOSPITAL_PINCODE : String
    private lateinit var USER_VETERNARY_HOSPITAL_ID_LINK : String
    private var USER_VETERNARY_HOSPITAL_AMBULANCE_AVAILABLE :Boolean = false
    private var USER_BELONGS_NGO : Boolean = false
    private lateinit var USER_VETERNARY_NGO_NAME : String
    private lateinit var USER_VETERNARY_NGO_COUNTRY : String
    private lateinit var USER_VETERNARY_NGO_STATE : String
    private lateinit var USER_VETERNARY_NGO_PINCODE : String
    private lateinit var USER_VETERNARY_NGO_ID_LINK : String
    private var USER_VETERNARY_NGO_AMBULANCE_AVAILABLE : Boolean = false

    private val baos = ByteArrayOutputStream()

    private lateinit var dataReference : StorageReference
    private lateinit var docReference : DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fAuth = FirebaseAuth.getInstance()
        fData = FirebaseFirestore.getInstance()
        fStorage = FirebaseStorage.getInstance()

        userID=fAuth.currentUser!!.uid

        docReference = fData.collection("USER").document(userID)
        dataReference = fStorage.reference.child("USER_AVATARS/")


        setContentView(R.layout.activity_register)
        activeFragment = fReg1

        fManager.beginTransaction().apply {
            add(R.id.register_activity_frame, fReg1, "Reg1").show(fReg1)
            add(R.id.register_activity_frame, fReg2, "Reg2").hide(fReg2)
            add(R.id.register_activity_frame, fReg3, "Reg3").hide(fReg3)
            add(R.id.register_activity_frame, fReg4, "Reg4").hide(fReg4)
            add(R.id.register_activity_frame, fReg5, "Reg5").hide(fReg5)
            add(R.id.register_activity_frame, fReg6, "Reg6").hide(fReg6)
        }.commit()

        docReference.get().addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot>{
            override fun onSuccess(p0: DocumentSnapshot?) {
                if(p0!!.exists()){

                    fReg1.first_name_update_text.text = p0.get("USER_FIRST_NAME").toString().toEditable()
                    fReg1.last_name_update_text.text = p0.get("USER_LAST_NAME").toString().toEditable()
                    fReg1.country_update_text.text = p0.get("USER_COUNTRY").toString().toEditable()
                    fReg1.state_update_text.text = p0.get("USER_STATE").toString().toEditable()
                    fReg1.pin_code_update_text.text = p0.get("USER_PIN_CODE").toString().toEditable()
                }
            }
        })

        reg_left_shift.setOnClickListener {
            if (fFlag > 0){
                fFlag-=1
                fManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(activeFragment)
                    .show(fList[fFlag])
                    .commit()
                activeFragment = fList[fFlag]
                right_arrow_gray.visibility = View.GONE
                left_arrow_gray.visibility=View.GONE
                right_arrow_green.visibility=View.VISIBLE
                left_arrow_green.visibility=View.VISIBLE
            }

            if (fFlag == 0){
                right_arrow_gray.visibility = View.GONE
                left_arrow_gray.visibility=View.VISIBLE
                right_arrow_green.visibility=View.VISIBLE
                left_arrow_green.visibility=View.GONE
            }

        }

        reg_right_shift.setOnClickListener {
            if (fFlag < 5){
                fFlag+=1
                fManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(activeFragment)
                    .show(fList[fFlag])
                    .commit()
                activeFragment = fList[fFlag]
                right_arrow_gray.visibility = View.GONE
                left_arrow_gray.visibility=View.GONE
                right_arrow_green.visibility=View.VISIBLE
                left_arrow_green.visibility=View.VISIBLE
            }
            if (fFlag == 5){
                right_arrow_gray.visibility = View.VISIBLE
                left_arrow_gray.visibility=View.GONE
                right_arrow_green.visibility=View.GONE
                left_arrow_green.visibility=View.VISIBLE
            }
        }

        card_button_activity_register.setOnClickListener {
            USER_FIRST_NAME             =   fReg1.first_name_update_text.text.toString()
            USER_LAST_NAME              =   fReg1.last_name_update_text.text.toString()
            USER_COUNTRY                =   fReg1.country_update_text.text.toString()
            USER_STATE                  =   fReg1.state_update_text.text.toString()
            USER_PIN_CODE               =   fReg1.pin_code_update_text.text.toString()

            USER_AVATAR_LINK            =   fReg2.profileAvatarUpdate
            if(USER_AVATAR_LINK!=null){
                profileAvatar = MediaStore.Images.Media.getBitmap(this.contentResolver,USER_AVATAR_LINK as Uri)
                profileAvatar.compress(Bitmap.CompressFormat.PNG,100,baos)
                val binaryData = baos.toByteArray()
                val avatarPath = "USER_AVATAR/"+userID+".png"
                val uploadTask = fStorage.getReference(avatarPath).putBytes(binaryData)

                uploadTask.addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot>{
                    override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                        if (p0.isSuccessful){
                            Log.d("upload","success")
                        }else{
                            Log.d("upload","failed")
                        }
                    }
                })

            }

            USER_PET_OWNER              =   fReg3.petOwner
            if (USER_PET_OWNER){
                USER_PET_CATEGORY       =   fReg3.pet_category_update_text.text.toString()
                USER_PET_SPECIES        =   fReg3.pet_species_update_text.text.toString()
            }

            USER_BELONG_VETERNARY =   fReg4.expertToggleStatus
            if (USER_BELONG_VETERNARY){
                USER_VETERNARY_HOSPITAL_NAME                =   fReg4.hospital_name_update_text.text.toString()
                USER_VETERNARY_HOSPITAL_COUNTRY             =   fReg4.hospital_country_update_text.text.toString()
                USER_VETERNARY_HOSPITAL_STATE               =   fReg4.hospital_state_update_text.text.toString()
                USER_VETERNARY_HOSPITAL_PINCODE             =   fReg4.hospital_pin_code_update_text.text.toString()
                USER_VETERNARY_HOSPITAL_AMBULANCE_AVAILABLE =   fReg4.hospitalAmbulanceFacility
                USER_VETERNARY_HOSPITAL_ID_LINK             =   fReg4.hospitalProofId.toString()
            }

            USER_BELONGS_NGO     =   fReg4.ngoToggleStatus
            if(USER_BELONGS_NGO){
                USER_VETERNARY_NGO_NAME                =   fReg4.ngo_name_update_text.text.toString()
                USER_VETERNARY_NGO_COUNTRY             =   fReg4.ngo_country_update_text.text.toString()
                USER_VETERNARY_NGO_STATE               =   fReg4.ngo_state_update_text.text.toString()
                USER_VETERNARY_NGO_PINCODE             =   fReg4.ngo_pin_code_update_text.text.toString()
                USER_VETERNARY_NGO_AMBULANCE_AVAILABLE =   fReg4.ngoAmbulanceFacility
                USER_VETERNARY_NGO_ID_LINK             =   fReg4.ngoProofId.toString()
            }

            updateProfile()
        }
    }

    private fun updateProfile(){

        val userMap = HashMap<String,Any>()
        userMap.apply {
            put("USER_FIRST_NAME",USER_FIRST_NAME)
            put("USER_LAST_NAME",USER_LAST_NAME)
            put("USER_COUNTRY",USER_COUNTRY)
            put("USER_STATE",USER_STATE)
            put("USER_PIN_CODE",USER_PIN_CODE)
            if (USER_AVATAR_LINK != null){

            }
            put("USER_AVATAR_LINK",USER_AVATAR_LINK.toString())
            put("USER_PET_OWNER",USER_PET_OWNER.toString())
            if(USER_PET_OWNER){
                put("USER_PET_CATEGORY",USER_PET_CATEGORY)
                put("USER_PET_SPECIES",USER_PET_SPECIES)
            }

            put("USER_BELONG_VETERNARY",USER_BELONG_VETERNARY.toString())
            if(USER_BELONG_VETERNARY){
                put("USER_VETERNARY_HOSPITAL_NAME",USER_VETERNARY_HOSPITAL_NAME)
                put("USER_VETERNARY_HOSPITAL_COUNTRY",USER_VETERNARY_HOSPITAL_COUNTRY)
                put("USER_VETERNARY_HOSPITAL_STATE",USER_VETERNARY_HOSPITAL_STATE)
                put("USER_VETERNARY_HOSPITAL_PINCODE",USER_VETERNARY_HOSPITAL_PINCODE)
                put("USER_VETERNARY_HOSPITAL_ID_LINK",USER_VETERNARY_HOSPITAL_ID_LINK)
                put("USER_VETERNARY_HOSPITAL_AMBULANCE_AVAILABLE",USER_VETERNARY_HOSPITAL_AMBULANCE_AVAILABLE.toString())
            }

            put("USER_BELONGS_NGO",USER_BELONGS_NGO.toString())
            if(USER_BELONGS_NGO){
                put("USER_VETERNARY_NGO_NAME",USER_VETERNARY_NGO_NAME)
                put("USER_VETERNARY_NGO_COUNTRY",USER_VETERNARY_NGO_COUNTRY)
                put("USER_VETERNARY_NGO_STATE",USER_VETERNARY_NGO_STATE)
                put("USER_VETERNARY_NGO_PINCODE",USER_VETERNARY_NGO_PINCODE)
                put("USER_VETERNARY_NGO_ID_LINK",USER_VETERNARY_NGO_ID_LINK)
                put("USER_VETERNARY_NGO_AMBULANCE_AVAILABLE",USER_VETERNARY_NGO_AMBULANCE_AVAILABLE.toString())
            }
        }

        docReference.update(userMap).addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                baseIntent()
            }
        })
    }

    private fun baseIntent(){
        startActivity(Intent(this@Register, Base::class.java))
        finish()
    }

    data class userModel (
        val firstName             : String  =   "NOT_AVAILABLE",
        val lastName              : String  =   "NOT_AVAILABLE",
        val country               : String  =   "NOT_AVAILABLE",
        val state                 : String  =   "NOT_AVAILABLE",
        val pincode               : String  =   "NOT_AVAILABLE",
        val profileAvatar         : String  =   "NOT_AVAILABLE",
        val petOwnerToggleStatus  : String  =   "NOT_AVAILABLE",
        val petCategory           : String  =   "NOT_AVAILABLE",
        val petSpecies            : String  =   "NOT_AVAILABLE",
        val veternaryToggleStatus : String  =   "NOT_AVAILABLE",
        val hospitalName          : String  =   "NOT_AVAILABLE",
        val department            : String  =   "NOT_AVAILABLE",
        val hospitalId            : String  =   "NOT_AVAILABLE",
        val ngoToggleStatus       : String  =   "NOT_AVAILABLE",
        val ngoName               : String  =   "NOT_AVAILABLE",
        val ngoPinCode            : String  =   "NOT_AVAILABLE",
        val ambulanceToggleStatus : String  =   "NOT_AVAILABLE",
        val ngoProofId            : String  =   "NOT_AVAILABLE"
    )

    fun fillDetails(){
        val profiledocRef = fData.collection("USER").document(userID)
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
