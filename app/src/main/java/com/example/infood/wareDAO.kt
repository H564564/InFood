package com.example.infood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.infood.Entity.ware

@Dao
interface wareDAO {
    @Query("Select wareName From ware where  wareID LIKE :wareID")
    fun queryNamebyID( wareID: String ): String

    @Query( "Select * From ware")
    fun queryAll(): MutableList<ware>

    @Query( "Select wareName From ware")
    fun queryName(): List<String>

    @Insert
    fun add(newware: ware)

    @Query("Delete From ware")
    fun DeleteAll()
}