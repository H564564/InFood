package com.example.infood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class recipeInexActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide();
        setContentView(R.layout.recipe_index)

        //上一頁(回到index)
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener(){
            Intent(this, indexActivity::class.java).apply {
                startActivity(this)
            }
        })

        findViewById<ImageView>(R.id.imageView2).setOnClickListener(View.OnClickListener {
            Intent(this, recipeActivity::class.java).apply {
                putExtra("page", "chicken")
                putExtra("before", "recipe_index")
                startActivity(this)
            }
        })
    }
}