package com.example.infood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.infood.Entity.food
@Dao
interface foodDAO {
    @Insert
    fun add(newFood:food)

    @Query("Select * From food")
    fun getAll():List<food>

    @Query("Select * From food Where foodName LIKE :foodName")
    fun getByfoodName( foodName: String ): food

    @Query("Select * from food Where wareID LIKE :wareID")
    fun getByWareID( wareID: String ): List<food>

    @Query("Select count(*) from food Where wareID LIKE :wareID")
    fun getCountByWareID( wareID: String ): Int

    @Query("Delete From food")
    fun deleteAll()

    @Query( "Delete From food Where wareID LIKE :wareID")
    fun deleteByWareID( wareID: String )

    @Query( "Delete From food Where foodName LIKE :foodName")
    fun deleteByName( foodName: String )
}