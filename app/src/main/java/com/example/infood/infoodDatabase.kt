package com.example.infood

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.infood.Entity.food
import com.example.infood.Entity.ware

@Database( entities = arrayOf( food::class, ware::class ), version = 1)
abstract class infoodDatabase: RoomDatabase(){
    abstract fun foodDAO(): foodDAO

    abstract fun wareDAO(): wareDAO
}