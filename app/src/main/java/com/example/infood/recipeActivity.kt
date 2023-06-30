package com.example.infood

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.*

class recipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide();

        //透過extra判斷頁面
        var page = intent.getStringExtra("page")
        if (page == "chicken")
            setContentView(R.layout.recipe)
        else if(page == "fish")
            setContentView(R.layout.recipe2)
        Log.i("recipeActivity called", "page == $page")

        //上一頁
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener() {
            if (intent.getStringExtra("before") == "usefood") {
                Intent(this, usefoodActivity::class.java).apply {
                    startActivity(this)
                }
            } else if (intent.getStringExtra("before") == "recipe_index") {
                Intent(this, recipeInexActivity::class.java).apply {
                    startActivity(this)
                }
            }
        })

        val job: Job = CoroutineScope(Dispatchers.Default).launch {
            val database =
                Room.databaseBuilder(
                    super.getBaseContext(),
                    infoodDatabase::class.java,
                    "infood.db"
                ).build()
            if (database.foodDAO().getByfoodName("哈嘎灣土雞") != null && page == "chicken") {
                findViewById<TextView>(R.id.stuff1).setTextColor(Color.BLACK)
                findViewById<TextView>(R.id.stuff1_unit).setTextColor(Color.BLACK)
                findViewById<View>(R.id.useRecipe).setOnClickListener(View.OnClickListener {
                    useRecipe("哈嘎灣土雞", super.getApplicationContext())
                })
            }
            else if (database.foodDAO().getByfoodName("鮭魚") != null && page == "fish") {
                findViewById<TextView>(R.id.stuff1).setTextColor(Color.BLACK)
                findViewById<TextView>(R.id.stuff1_unit).setTextColor(Color.BLACK)
                findViewById<View>(R.id.useRecipe).setOnClickListener(View.OnClickListener {
                    useRecipe("鮭魚", super.getApplicationContext())
                })
            }else{
                findViewById<View>(R.id.useRecipe).setOnClickListener(View.OnClickListener {
                    Toast.makeText(super.getApplicationContext(), "食庫內無食材", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    fun useRecipe(foodName: String, context: Context )
    {
        CoroutineScope(Dispatchers.Default).launch {
            val database =
                Room.databaseBuilder(
                    super.getBaseContext(),
                    infoodDatabase::class.java,
                    "infood.db"
                ).build()
            database.foodDAO().deleteByName(foodName)
            withContext(Dispatchers.Main)
            {
                Toast.makeText(super.getApplicationContext(), "已使用食材: $foodName", Toast.LENGTH_SHORT)
                    .show()

                Intent(super.getApplication(), indexActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

    }

}