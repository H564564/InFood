package com.example.infood

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.infood.Entity.food
import kotlinx.coroutines.*


import androidx.recyclerview.widget.GridLayoutManager
import com.example.infood.Entity.ware
import com.example.infood.connectDB.Companion.getFoodAndAdd
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern


class wareActivity : AppCompatActivity() {

    lateinit var food: List<food>
    lateinit var wareName:String
    var positon: Int = -1
    lateinit var wareIDList: Array<String>

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stockbox1)

        //上一頁
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener() {
            Intent(this, stockActivity::class.java).apply {
                startActivity(this)
            }
        })

        wareIDList = intent.getStringArrayExtra("wareIDList") as Array<String>
        positon = intent.getIntExtra("position", -1)
        Log.i(
            "wareActivity onCreate called:",
            " into $positon row and wareID is ${wareIDList[positon]} "
        )

        if ((positon - 1) < 0)
            findViewById<ImageView>(R.id.frontPage).visibility = View.GONE
        if ((positon + 1) >= wareIDList.size)
            findViewById<ImageView>(R.id.nextPage).visibility = View.GONE



        val job: Job = CoroutineScope(Dispatchers.Main).launch {
            getFoodAndAdd(super.getApplicationContext(), wareIDList[positon])
            getFoodList(wareIDList[positon])
            findViewById<TextView>(R.id.ware).text = wareName

            for (i in 0 until food.size)
                Log.i("wareAct onCreate called", "foodlist is ${food[i].foodName}")

            val recycler = findViewById<RecyclerView>(R.id.boxRecycler)
            recycler.setHasFixedSize(true)
            recycler.layoutManager = GridLayoutManager(super.getApplicationContext(), 4)
            val boxAdapter = boxAdapter(super.getApplicationContext(), food)
            recycler.adapter = boxAdapter
        }



        //拖曳功能

        /*findViewById<ImageView>(R.id.food1).setOnLongClickListener(View.OnLongClickListener {
            it.startDragAndDrop(null, View.DragShadowBuilder(it), it, 0)
            it.visibility = View.INVISIBLE
            return@OnLongClickListener true
        })
        findViewById<ImageView>(R.id.food2).apply {

            setOnLongClickListener(View.OnLongClickListener {
                it.startDragAndDrop(null, View.DragShadowBuilder(it), it, 0)
                it.visibility = View.INVISIBLE
                return@OnLongClickListener true
            })
            setOnDragListener(View.OnDragListener { v, event ->
                when (event.action) {
                    DragEvent.ACTION_DROP -> {
                        val a = event.localState as ImageView
                        //val parent = a.parent as ViewGroup
                        //parent.removeView(a)
                        val ll = v as ImageView
                        ll.setImageResource(a.getTag())
                        a.setImageResource()
                        a.visibility = View.VISIBLE
                    }
                }
                return@OnDragListener true
            })

        }*/




    }

    suspend fun getFoodList(wareID: String) = withContext(Dispatchers.Default) {

            val database =
                Room.databaseBuilder(super.getApplicationContext(), infoodDatabase::class.java, "infood.db")
                    .build()
            food = database.foodDAO().getByWareID(wareID)
            wareName = database.wareDAO().queryNamebyID(wareID)
    }

    fun nextPage(view: View) {
        var newPage = positon + 1
        var maxPage = wareIDList.size

        if (newPage < maxPage) {
            Intent(this, wareActivity::class.java).apply {
                this.putExtra("position", newPage)
                this.putExtra("wareIDList", wareIDList)
                startActivity(this)
            }
        } else
            view.visibility = View.GONE
    }

    fun frontPage(view: View) {
        var newPage = positon - 1

        if (newPage >= 0) {
            Intent(this, wareActivity::class.java).apply {
                this.putExtra("position", newPage)
                this.putExtra("wareIDList", wareIDList)
                startActivity(this)
            }
        } else
            view.visibility = View.GONE
    }


}

class boxAdapter(context: Context, private val food: List<food>) :
    RecyclerView.Adapter<boxAdapter.boxViewHolder>() {
    private var listener: View.OnClickListener? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): boxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.box_content, parent, false)
        val viewgroup = parent.rootView
        //Log.i("onCreateViewHolder called","onCreateViewHolder called")
        return boxViewHolder(view, viewgroup)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: boxViewHolder, position: Int) {
        bind(holder,position, food)
        holder.setOnClick(position, listener)
        when
        {
            food[position].foodName.contains("雞") -> holder.foodImg.setImageResource(R.drawable.ham)

            food[position].foodName.contains("鮪魚") -> holder.foodImg.setImageResource(R.drawable.tuna)

            food[position].foodName.contains("番茄") -> holder.foodImg.setImageResource(R.drawable.tomato)

            food[position].foodName.contains("鮭魚") -> holder.foodImg.setImageResource(R.drawable.salmon)

            food[position].foodName.contains("牛肉") -> holder.foodImg.setImageResource(R.drawable.beef)

            food[position].foodName.contains("豬肉") -> holder.foodImg.setImageResource(R.drawable.chop)

            food[position].foodName.contains("肉") -> holder.foodImg.setImageResource(R.drawable.lamb)
        }

        Log.i("onBindViewHolder called",String.format("onBindViewHolder called in $position row %s", food.get(position).toString()))
    }

    override fun getItemCount(): Int {
        if (food != null) {
            Log.i("getItemCount called", String.format("getItemCount called %s row", food.size))
            return food.size
        }
        return 0
    }

    //每次更新listener
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(holder: boxViewHolder, position: Int, _food: List<food>) {
        listener = View.OnClickListener {
            holder.foodID.text = "物品編號: " + _food[position].foodID
            holder.foodName.text = "商品名稱: " + _food[position].foodName
            holder.foodAmount.text = "數量: " + _food[position].foodAmount

            var buyTime = LocalDateTime.parse(_food[position].buyTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("+8"))
            holder.buyTime.text = "購買日期: " + buyTime.format( ofPattern("yyyy-MM-dd HH:mm") )

            var foodTime = LocalDateTime.parse(_food[position].foodTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("+8"))
            holder.foodTime.text = "有效日期: " + foodTime.format( ofPattern("yyyy-MM-dd HH:mm") )

            var duration = Duration.between(Instant.now().atZone(ZoneId.of("+8")), foodTime).toDays()
            holder.foodstatue.text = Html.fromHtml("商品狀態: " + when{
                duration > 7 -> "<font color='green'>健康</font>"

                duration > 5 -> "<font color='yellow'>不太健康</font>"

                duration > 0 -> "<font color='red'>即將過期</font>"

                else -> "<font color='black'><b>已過期</b></font>"
            })

            holder.foodFrom.text = "產地: ${food[position].foodFrom}"
            holder.buyFrom.text =  "購入地點: ${food[position].buyFrom}"
            holder.foodData.text = "商品描述: ${food[position].foodData}"
            Log.i("boxOnClickListener called", "durtion is ${Duration.between(Instant.now().atZone(ZoneId.of("+8")), foodTime).toDays()} ")
        }
        //Log.i("bind called","bind position $position row")
    }

    inner class boxViewHolder(view: View, parent: View) : RecyclerView.ViewHolder(view) {
        var foodImg: ImageView = view.findViewById(R.id.food1)
        var foodID: TextView = parent.findViewById(R.id.box_foodid)
        var foodName: TextView = parent.findViewById(R.id.box_foodname)
        var foodAmount: TextView = parent.findViewById(R.id.foodname2)
        var buyTime: TextView = parent.findViewById(R.id.buytime)
        var foodTime: TextView = parent.findViewById(R.id.foodtime)
        var foodstatue: TextView = parent.findViewById(R.id.foodstatue)
        var foodFrom = parent.findViewById<TextView>(R.id.foodfrom)
        var buyFrom = parent.findViewById<TextView>(R.id.buyfrom)
        var foodData = parent.findViewById<TextView>(R.id.foodData)

        fun setOnClick(data: Int, listener: View.OnClickListener?)
        {
            itemView.setOnClickListener(listener)
            Log.i("setOnClick Called", "into $data item")
        }
    }


}




