package com.example.infood

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.infood.Entity.food
import com.example.infood.Entity.ware
import kotlinx.coroutines.*

class usefoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide();
        setContentView(R.layout.usefood)

        //上一頁(回到index)
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setBackgroundColor(Color.TRANSPARENT)
        backToIndex.setOnClickListener(View.OnClickListener() {
            Intent(this, indexActivity::class.java).apply {
                startActivity(this)
            }
        })


        var mContext = this

        val job: Job = CoroutineScope(Dispatchers.Default).launch {
            val database =
                Room.databaseBuilder(
                    super.getBaseContext(),
                    infoodDatabase::class.java,
                    "infood.db"
                ).build()
            val wareList =  database.wareDAO().queryName()
            var checkList = listOf<String>("哈嘎灣土雞", "鮭魚", "哈嘎灣水蜜桃", "哈嘎灣甜柿", "哈嘎灣高麗菜", "哈嘎灣大白菜", "哈嘎灣地瓜", "哈嘎灣牛奶", "哈嘎灣山雞", "哈嘎灣豬肉", "哈嘎灣芋頭", "哈嘎灣鮭魚")
            //Log.i( "usefoodActivity called", "chicken name: ${chicken.foodName}")
            val recommendSpinner = findViewById<Spinner>(R.id.RecommendSpinner)
            val wareSpinner = findViewById<Spinner>(R.id.wareSpinner)

            val check = fun(_checkList:List<String>): MutableList<food> {
                val result = mutableListOf<food>()
                for(element in _checkList)
                    result.add( database.foodDAO().getByfoodName(element) )
                return result
            }
            val resultList = check(checkList)


            withContext(Dispatchers.Main)
            {
                //set RecommendSpinner
                recommendSpinner.adapter = ArrayAdapter<CharSequence>(
                    super.getApplicationContext() ,R.layout.spinner_text,R.id.spinner_text,  mContext.resources.getStringArray( R.array.recommendType )
                )
                wareSpinner.adapter = ArrayAdapter<CharSequence>(
                    super.getApplicationContext() ,R.layout.spinner_text,R.id.spinner_text,  wareList
                )

                var intent = Intent(super.getApplicationContext(), recipeActivity::class.java)
                intent.putExtra("before", "usefood")
                if (resultList[0] != null) {

                    var card2 = findViewById<ImageView>(R.id.card2)
                    card2.visibility = View.VISIBLE
                    card2.setOnClickListener(View.OnClickListener {
                        intent.putExtra("page", "chicken")
                        Log.i("usefood onclick", "into fish page")
                        startActivity(intent)
                    })
                }
                if (resultList[1] != null) {
                    var card1 = findViewById<ImageView>(R.id.card1)
                    card1.visibility = View.VISIBLE
                    card1.setOnClickListener(View.OnClickListener {
                        intent.putExtra("page", "fish")
                        Log.i("usefood onclick", "into chicken page")
                        startActivity(intent)
                    })
                }

                for( i in 2 until resultList.size) {
                    if (resultList[i] != null) {
                        findViewById<ImageView>(getString("card_" + (i-1))).visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    fun AppCompatActivity.getString(name: String): Int {
        return resources.getIdentifier(name,"id", packageName)
    }

    fun toTimeout(view: View) {
        Intent(this, timeoutActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun toStock(view: View) {
        Intent(this, stockActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun toUsefood(view: View) {
        Intent(this, usefoodActivity::class.java).apply {
            startActivity(this)
        }
    }


}