package com.example.infood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.infood.connectDB.Companion.getRecommendationList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class memberActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.member)
        var mContext = this
        //mContext.findViewById<ImageView>(getString("recommendation1" )).visibility = View.VISIBLE

        //上一頁(回到index)
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener(){
            Intent(this, indexActivity::class.java).apply {
                startActivity(this)
            }
        })


        val job: Job = CoroutineScope(Dispatchers.Main).launch {
            val recommendList = getRecommendationList(mContext)

            if( !recommendList.isNullOrEmpty() )
            {
                for( i in recommendList.indices)
                {
                    mContext.findViewById<ImageView>(getString("recommendation" + recommendList[i].foodid)).visibility = View.VISIBLE
                }
            }
        }
    }

    fun AppCompatActivity.getString(name: String): Int {
        return resources.getIdentifier(name,"id", packageName)
    }

}