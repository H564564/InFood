package com.example.infood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class indexActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index)

        //食庫按鈕
        val stock = findViewById<ImageButton>(R.id.imageButton2)
        stock.setOnClickListener(View.OnClickListener(){
            Intent(this, stockActivity::class.java).apply {
                startActivity(this)
            }
        })

        //食譜按鈕
        val recipe = findViewById<ImageButton>(R.id.imageButton4)
        recipe.setOnClickListener(View.OnClickListener(){
            Intent(this, recipeInexActivity::class.java).apply {
                startActivity(this)
            }
        })

        //會員按鈕
        val  member= findViewById<ImageButton>(R.id.member)
        member.setOnClickListener(View.OnClickListener(){
            Intent(this, memberActivity::class.java).apply {
                startActivity(this)
            }
        })
    }
}