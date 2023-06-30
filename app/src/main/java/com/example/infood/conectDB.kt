package com.example.infood

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.infood.Entity.Recommendation
import com.example.infood.Entity.food
import com.example.infood.Entity.ware
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class connectDB {

    companion object {
        fun getRequest(_url: String, method: String ): BufferedReader  {
            val url = URL( _url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = method
            val inputStreamReader = InputStreamReader(conn.getInputStream(), "UTF-8")
            return BufferedReader( inputStreamReader )
        }

        //get WareList Json and return a MutableList
        suspend fun getWareListOnNet(context: Context): MutableList<ware>? {
            var newList: MutableList<ware> = mutableListOf()

            withContext(Dispatchers.IO) {
                val user =  context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    .getString("UserID", "null")
                val buffer = getRequest("http://10.0.2.2:8080/user/$user/ware/", "GET")

                while (true) {
                    var data = buffer.readLine()?:break

                    if (data != "[]")
                    {
                        //data = data.replace("[","").replace("]","")
                        Log.i("getWareListOnNet called", "data = $data ")
                        newList = jacksonObjectMapper().readValue(data)
                        // 雖然發現json傳過來只會有一行, 但是懶得改mutableList, 其實可以改成唯讀的list
                    }
                    else
                        Log.i("getWareListOnNet called", "empty data")
                }
            }


            return if ( newList.isEmpty() )
                null
            else
                newList
        }

        //get WareList Json and add to sql
        suspend fun getFoodAndAdd(context: Context, wareID: String) = withContext(Dispatchers.Default) {
            var newFood: List<food> = listOf()

            withContext(Dispatchers.IO) {
                val user =  context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    .getString("UserID", "null")
                val buffer = getRequest("http://10.0.2.2:8080/user/$user/ware/$wareID","GET")

                while (true) {
                    var data = buffer.readLine()?:break

                    if (data != "[]")
                    {
                        //data = data.replace("[","").replace("]","")
                        Log.i("getFoodAndAdd called", "data = $data ")
                        newFood = jacksonObjectMapper().readValue(data)
                    }
                    else
                        Log.i("getFoodAndAdd called", "empty data")
                }
            }

            val database =
                Room.databaseBuilder(context, infoodDatabase::class.java, "infood.db")
                    .build()
            if( !newFood.isEmpty())
            {
                var oldList = database.foodDAO().getByWareID(wareID)
                //if( oldList.size != newFood.size ) {
                //原本是用SIZE檢查有沒有新增,不過還是全更新掉比較不會漏
                    database.foodDAO().deleteByWareID( wareID )
                    for (i in 0 until newFood.size) {
                        database.foodDAO().add(newFood[i])
                    }
                //}
            }else
                database.foodDAO().deleteByWareID(wareID)
        }

        //get recommendation Json and return a List
        suspend fun getRecommendationList(context: Context): List<Recommendation>? {
            var newList: List<Recommendation> = listOf()

            withContext(Dispatchers.IO) {
                val user =  context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    .getString("UserID", "null")
                val buffer = getRequest("http://10.0.2.2:8080/user/$user/recommend", "GET")
                Log.i("getRecommendationList called", "http://10.0.2.2:8080/user/$user/recommend")

                while (true) {
                    var data = buffer.readLine()?:break

                    if (data != "[]")
                    {
                        //data = data.replace("[","").replace("]","")
                        Log.i("getRecommendationList called", "data = $data ")
                        newList = jacksonObjectMapper().readValue(data)
                    }
                    else
                        Log.i("getRecommendationList called", "empty data")
                }
            }

            return if ( newList.isEmpty() )
                null
            else
                newList
        }
    }




}