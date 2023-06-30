package com.example.infood

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.infood.Entity.ware
import com.example.infood.connectDB.Companion.getWareListOnNet
import kotlinx.coroutines.*
import java.util.*

class stockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock)

        //上一頁(回到index)
        val backToIndex = findViewById<View>(R.id.imageView20)
        backToIndex.setOnClickListener(View.OnClickListener(){
            Intent(this, indexActivity::class.java).apply {
                startActivity(this)
            }
        })



        val job: Job = CoroutineScope(Dispatchers.Main).launch {


            //設定倉庫列表
            var stock = getWareList( super.getApplication() )


            val recycler = findViewById<RecyclerView>(R.id.stockRecycler)
            recycler.setHasFixedSize(true)
            recycler.layoutManager  = LinearLayoutManager(super.getApplication()).apply{
                orientation = LinearLayoutManager.VERTICAL;
            }
            recycler.itemAnimator = null
            val stockAdapter = stockAdapter( super.getApplication(), stock )
            recycler.adapter = stockAdapter

            val newList = getWareListOnNet( super.getApplication() )
            if (newList != null) {
                checkListChange( stockAdapter, stock,  newList )
            }
            delay(3000)
            findViewById<ProgressBar>(R.id.stockLoading).visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }



    //檢查newList 並使用 notifyItemChanged 更新
    @SuppressLint("NotifyDataSetChanged")
    suspend fun checkListChange(_stockAdapter: stockAdapter, stock:  MutableList<ware>?, newList: MutableList<ware>? ) = withContext(Dispatchers.Default){
        val database =
            Room.databaseBuilder(super.getApplication(), infoodDatabase::class.java, "infood.db")
                .build()
        database.wareDAO().DeleteAll()
        //先檢查newList查到的東西
        if( newList != null ) {
            val min = if (stock?.size!! <= newList?.size!!) stock.size else newList.size
            //取最小值 這邊只需要改原本的view
            for (i in 0 until min) {
                //用data class的equals特性 檢查屬性是否全部相同
                if (stock[i].equals(newList[i])) {
                    Log.i("checkListChange called", "stock[$i] equals")
                    database.wareDAO().add(newList[i])
                    continue
                }
                else {
                    stock[i] = newList[i]
                    database.wareDAO().add(newList[i])
                    getWareFoodCount(  super.getApplicationContext(), stock)
                    withContext(Dispatchers.Main) {
                        _stockAdapter.notifyItemChanged(i)
                    }
                }
            }
            //超過最小
            if (stock.size < newList.size) {
                for (i in min until newList.size) {
                    stock.add(newList[i])
                    database.wareDAO().add(newList[i])
                }
                getWareFoodCount(  super.getApplicationContext(), stock)
                withContext(Dispatchers.Main) {
                    _stockAdapter.notifyDataSetChanged()
                }
            } else {
                for (i in stock.size - 1 downTo min) {
                    stock.removeAt(i)
                    withContext(Dispatchers.Main) {
                        _stockAdapter.notifyItemRemoved(i)
                    }
                }
            }
        }
        else
        {
            if (stock != null) {
                stock.clear()
            }
            withContext(Dispatchers.Main) {
                _stockAdapter.notifyDataSetChanged()
            }
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


}

class stockViewHolder(view: View): RecyclerView.ViewHolder(view)
{
    val wareName: TextView = view.findViewById(R.id.wareName);
    val foodAmount: TextView = view.findViewById(R.id.foodAmount);
    val color: ImageView = view.findViewById(R.id.color);
    val lastSaw: TextView = view.findViewById(R.id.lastSaw);

    fun setOnClick(data: Int, listener: View.OnClickListener?)
    {
        itemView.setOnClickListener(listener)
        Log.i("setData Called", "into $data row")
    }

    
}

class stockAdapter(context: Context, private val stock:  MutableList<ware>?) : RecyclerView.Adapter<stockViewHolder>() {
    private var listener: View.OnClickListener? = null;
    //data
    //private val stock = listOf<ware>( ware("test", 4, "box1", "4/5 6:00") )
    private lateinit var wareIDList: Array<String>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): stockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_ware, parent, false)
        //Log.i("onCreateViewHolder called","onCreateViewHolder called")
        return stockViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (stock != null) {
            Log.i("getItemCount called", String.format("getItemCount called %s row" , stock.size ) )
            getWareIDList()
            return stock.size
        }
        return 0
    }

    private fun getWareIDList() {
        var mutableWareIDList: MutableList<String> = mutableListOf()
        if (stock != null) {
            for( i in 0 until stock.size)
                mutableWareIDList.add( stock[i].wareID )
        }
        wareIDList = Collections.unmodifiableList( mutableWareIDList ).toTypedArray()
    }

    override fun onBindViewHolder(holder: stockViewHolder, position: Int) {
        if( stock?.get(position) != null ) {
            holder.wareName.text = stock[position].wareName
            holder.foodAmount.text = stock[position].foodAmount.toString()

            Log.i("onBindViewHolder called", "${stock[position].wareName}'s foodCount = ${stock[position].foodAmount}")

            holder.color.setImageResource(
                getImageId(
                    holder.itemView.context,
                    stock[position].color
                )
            )
            holder.lastSaw.text =  String.format("上次瀏覽:%s ", stock[position].lastSaw)
        }
        bind(holder,position, stock)
        holder.setOnClick(position, listener)
        Log.i("onBindViewHolder called",String.format("onBindViewHolder called in $position row %s", stock?.get(position).toString()))
    }


    //每次更新listener, 以放入正確的position
    fun bind( holder: stockViewHolder, position: Int, _stock: MutableList<ware>? )
    {
        listener = View.OnClickListener {
            Intent( holder.itemView.context, wareActivity::class.java).apply {
                this.putExtra( "wareIDList", wareIDList )
                this.putExtra("position", position )
                holder.itemView.context.startActivity(this)
            }
        }
        //Log.i("bind called","bind position $position row")
    }

}

fun getImageId(context: Context, imageName: String): Int
{
    return context.resources.getIdentifier("drawable/$imageName", null, context.packageName)
}




suspend fun getWareList(context: Context): MutableList<ware> = withContext(Dispatchers.Default)
{
    val database =
        Room.databaseBuilder(context, infoodDatabase::class.java, "infood.db")
            .build()
    var stock = database.wareDAO().queryAll()
    getWareFoodCount(  context, stock)

    return@withContext stock
   /* var position = 0
    var wareContent: String? = context.getSharedPreferences("wareList", AppCompatActivity.MODE_PRIVATE)
        .getString( position.toString(), "null" )
    var wareList: MutableList<ware>  = mutableListOf()

    while( wareContent != "null" )
    {
        var ware = jacksonObjectMapper().readValue<ware>( wareContent.toString() )
        wareList.add( ware )

        position++
        wareContent = context.getSharedPreferences("wareList", AppCompatActivity.MODE_PRIVATE)
            .getString( position.toString(), "null" )
    }

    //Log.i("getWareList called", String.format("wareList have %s row", wareList.size ) )
    return if( wareList.size == 0 )
        null
    else
        wareList*/
}

suspend fun getWareFoodCount( context: Context, stock: MutableList<ware>) = withContext(Dispatchers.Default){
    val database =
        Room.databaseBuilder(context, infoodDatabase::class.java, "infood.db")
            .build()

    for( i in 0 until stock.size )
    {
        stock[i].foodAmount = database.foodDAO().getCountByWareID( stock[i].wareID )
        Log.i("getWareList called", "${stock[i].wareName}'s foodCount = ${stock[i].foodAmount}")
    }
}

