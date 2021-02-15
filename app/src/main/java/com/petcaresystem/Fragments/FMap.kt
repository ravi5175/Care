package com.petcaresystem.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.android.gms.clearcut.ClearcutLogger.API
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.internal.GoogleApiManager
import com.google.android.gms.location.*
import com.google.android.gms.location.ActivityRecognition.API
import com.google.android.gms.location.LocationServices.API
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.petcaresystem.Adapters.InfoWindowAdapter
import com.petcaresystem.Models.InfoWindowCache
import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.report.*
import kotlinx.android.synthetic.main.report.view.*
import java.io.File


class FMap : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap           : GoogleMap
    private lateinit var latLng              : LatLng
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var reportCamera        : PreviewView
    private lateinit var reportCapture       : ImageView
    private lateinit var reportPreview       : ImageView
    private lateinit var reportCaptureRetake : ImageView
    private lateinit var callForHelp         : Button
    private lateinit var reportDescription   : EditText
    private lateinit var googleApiClient     : GoogleApiClient

    var camera: androidx.camera.core.Camera? = null
    var preview: Preview? = null
    var imageCapture: ImageCapture? = null
    var cameraSelector: CameraSelector? = null

    var currentImage: File? = null

    private val data = ArrayList<InfoWindowCache>()




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        main_map.onCreate(savedInstanceState)
        main_map.onResume()
        main_map.getMapAsync(this)

            }

    override fun onMapReady(map: GoogleMap?) {
            googleMap= map!!
            googleMap.isMyLocationEnabled = true

            val locRequest = LocationRequest.create()
            locRequest.apply {
                interval = 60000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

        val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations){

                    }
                }
            }


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationClient.lastLocation.addOnSuccessListener(object: OnSuccessListener<Location?>{
                override fun onSuccess(location: Location?) {
                    if(location!=null){
                        Log.d("location",location!!.latitude.toString())
                        latLng = LatLng(location.latitude,location.longitude)
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                        googleMap.moveCamera(cameraUpdate)
                    }else{
                        fusedLocationClient.requestLocationUpdates(locRequest,locationCallback, Looper.getMainLooper())
                        latLng = LatLng(location!!.latitude,location!!.longitude)
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                        googleMap.moveCamera(cameraUpdate)
                    }
                }
            })

            googleMap.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener{
                override fun onMapLongClick(gmapIt: LatLng?) {
                    if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                        val report = LayoutInflater.from(context).inflate(R.layout.report,null)
                        val build = AlertDialog.Builder(activity).setView(report)
                        val dialog = build.create()
                        reportCamera = report.report_camera
                        reportPreview = report.report_preview
                        reportCapture = report.report_capture_button
                        reportCaptureRetake = report.report_capture_retake_button
                        reportDescription = report.report_description
                        callForHelp = report.call_for_help
                        reportCapture.setOnClickListener{
                            takePhoto()
                        }
                        reportCaptureRetake.setOnClickListener {
                            reportPreview.visibility=View.GONE
                            reportCaptureRetake.visibility=View.GONE
                            reportCapture.visibility=View.VISIBLE
                            reportCamera.visibility=View.VISIBLE
                            if (currentImage!!.exists()){
                                currentImage!!.delete()
                            }
                            startCamera()
                        }
                        callForHelp.setOnClickListener {
                            val bm: Bitmap = reportPreview.getDrawable().toBitmap()
                            val desc = reportDescription.toString()
                            googleMap.addMarker(MarkerOptions().position(gmapIt!!).icon(getBitmapDescriptor(R.drawable.ic_alert)))
                            googleMap.setInfoWindowAdapter(InfoWindowAdapter(activity!!,bm,desc))

                            dialog.cancel()
                        }
                        dialog.show()
                        startCamera()

                    }else{
                        ActivityCompat.requestPermissions(activity!!,
                            arrayOf(Manifest.permission.CAMERA),0)
                    }
                }
            })

    }

    private fun getBitmapDescriptor(@DrawableRes id: Int): BitmapDescriptor? {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            val report = LayoutInflater.from(context).inflate(R.layout.report,null)
            val build = AlertDialog.Builder(activity).setView(report)
            build.show()
            report_capture_button.setOnClickListener {
                takePhoto()
            }
        }else{
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.CAMERA),0)
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity!!)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            if (reportCamera != null){
                preview?.setSurfaceProvider(reportCamera.surfaceProvider)
            }else{
                Log.d("report_camera","null")
            }
            imageCapture = ImageCapture.Builder().setTargetRotation(view?.display!!.rotation).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
            cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(activity!!,cameraSelector!!,preview,imageCapture)
        },ContextCompat.getMainExecutor(activity!!))
    }

    private fun takePhoto(){
        if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            try{
                val path = File(Environment.getExternalStorageDirectory().toString() + "/PCS/Image")
                val photofile = File(path,"PCS-${System.currentTimeMillis()}.jpg")
                if (!path.exists()){
                    path.mkdirs()
                }
                photofile.createNewFile()
                Log.d("loc",photofile.toString())
                val output = ImageCapture.OutputFileOptions.Builder(photofile).build()
                imageCapture?.takePicture(output,ContextCompat.getMainExecutor(activity!!),object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        reportCamera.visibility = View.GONE
                        reportCapture.visibility = View.GONE
                        reportCaptureRetake.visibility = View.VISIBLE
                        val matrix: Matrix? = Matrix()
                        matrix!!.postRotate(90f)
                        val bitmap = BitmapFactory.decodeFile(photofile.toString())
                        reportPreview.setImageBitmap(Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true))
                        reportPreview.visibility = View.VISIBLE
                        currentImage = photofile
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d("saveExcept",exception.toString())
                    }
                })
            }catch (e : Exception){
                Log.d("create",e.toString())
            }
        }else{
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)

        }

    }
}
