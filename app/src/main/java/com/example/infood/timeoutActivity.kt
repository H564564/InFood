package com.example.infood

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.*
import java.time.*
import java.time.format.DateTimeFormatter

class timeoutActivity : AppCompatActivity() {
    var timeoutFood: MutableList<timeoutList> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timeout)

        //上一頁(回到index)
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener() {
            Intent(this, indexActivity::class.java).apply {
                startActivity(this)
            }
        })

        val job: Job = CoroutineScope(Dispatchers.Main).launch {
            val recycler = findViewById<RecyclerView>(R.id.timeoutRecycler)
            recycler.setHasFixedSize(true)
            recycler.layoutManager = LinearLayoutManager(super.getApplication()).apply {
                orientation = LinearLayoutManager.VERTICAL;
            }
            getTimeoutList()
            recycler.adapter = timeoutAdapter(super.getApplication(), timeoutFood )
        }
    }

    fun toTimeout(view: View){
        Intent(this, timeoutActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun toStock(view: View){
        Intent(this, stockActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun toUsefood( view: View ){
        Intent( this, usefoodActivity::class.java).apply {
            startActivity(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTimeoutList() = withContext(Dispatchers.Default) {
        val database =
            Room.databaseBuilder(super.getApplication(), infoodDatabase::class.java, "infood.db")
                .build()
        var allfood = database.foodDAO().getAll()
        var tempTime: ZonedDateTime

        Log.i("getTimeoutList called", "allfood size: ${allfood.size}")
        for (i in 0 until allfood.size) {
            tempTime =
                LocalDateTime.parse(allfood[i].foodTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.of("+8"))
            var differ = Duration.between(Instant.now().atZone(ZoneId.of("+8")), tempTime).toDays()

            if (differ < 7) {
                Log.i(
                    "getTimeoutList called",
                    "${allfood[i].foodName}[$i] add to timeoutList ${timeoutFood.size} differ: $differ"
                )
                timeoutFood.add(
                    timeoutList(
                        database.wareDAO().queryNamebyID(allfood[i].wareID),
                        allfood[i].foodID,
                        allfood[i].foodName,
                        LocalDateTime.parse(allfood[i].foodTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .atZone(ZoneId.of("+8")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd ")),
                        differ
                    )
                )
            } else
                Log.i(
                    "getTimeoutList called",
                    "${allfood[i].foodName}[$i] not in timeout differ: $differ"
                )
        }

        Log.i("getTimeoutList called", "now time: ${Instant.now().atZone(ZoneId.of("+8"))}")
    }



    data class timeoutList(
        var wareName: String,
        var foodID: String,
        var foodName: String,
        var foodTime: String,
        var differ: Long
    )
}

class timeoutAdapter(context: Context, private val timeoutList: List<timeoutActivity.timeoutList>) :
    RecyclerView.Adapter<timeoutAdapter.timeoutViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): timeoutAdapter.timeoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timeout_ware, parent, false)
        return timeoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: timeoutAdapter.timeoutViewHolder, position: Int) {
        var duration = timeoutList[position].differ
        when{
            duration > 3 -> holder.status.setImageResource(R.drawable.error1)

            duration > 0 -> holder.status.setImageResource(R.drawable.error2)

            else -> holder.status.setImageResource( R.drawable.error3 )
        }

        holder.timeout_ware.text = timeoutList[position].wareName
        holder.timeout_foodName.text = timeoutList[position].foodName
        holder.timeout_foodID.text = "編號 : ${timeoutList[position].foodID}"
        holder.timeout_foodTime.text = timeoutList[position].foodTime
    }

    override fun getItemCount(): Int {
        Log.i("getItemCount called", String.format("getItemCount called %s row", timeoutList.size))
        return timeoutList.size
    }

    inner class timeoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val status = view.findViewById<ImageView>(R.id.status)
        val timeout_ware = view.findViewById<TextView>(R.id.timeout_ware)
        val timeout_foodName = view.findViewById<TextView>(R.id.timeout_foodName)
        val timeout_foodID = view.findViewById<TextView>(R.id.timeout_foodID)
        val timeout_foodTime = view.findViewById<TextView>(R.id.timeout_foodTime)
    }
}