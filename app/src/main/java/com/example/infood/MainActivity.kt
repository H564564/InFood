package com.example.infood

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.infood.Entity.ware
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cover)
        showMessage()

        //點擊後redirect
        val cover = findViewById<ConstraintLayout>(R.id.cover)
        val intent = Intent(this, indexActivity::class.java)
        cover.setOnClickListener(View.OnClickListener(){
            startActivity(intent)
        })

        getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).edit().clear().putString("UserID","1").commit()

    }

    //小視窗
    fun showMessage()
    {
        AlertDialog.Builder(this)
                   .setTitle("ver0.000000001")
                   .setMessage("這是demo版本 一切皆有可能變動")
                   .show()
    }



}



/*getSharedPreferences("wareList", MODE_PRIVATE ).edit()
            .clear()
            .putString("0", jacksonObjectMapper()
                .writeValueAsString( ware("w9", "kotlin", 777, "box1","10/7 2:02"))
            )
            .putString("1", jacksonObjectMapper()
                .writeValueAsString( ware("w8", "肉類", 6, "box2","10/7 2:02"))
            )
            .putString("2", jacksonObjectMapper()
                .writeValueAsString( ware("w6", "海鮮", 7, "box1","10/7 2:02"))
            )
            .putString("3", jacksonObjectMapper()
                .writeValueAsString( ware("w5", "test", 777, "box2","10/7 2:02"))
            )
            .putString("4", jacksonObjectMapper()
                .writeValueAsString( ware("w9", "test", 777, "box1","10/7 2:02"))
            )
            .putString("5", jacksonObjectMapper()
                .writeValueAsString( ware("w9", "test", 777, "box2","4/5 6:00"))
            )
            .commit()*/