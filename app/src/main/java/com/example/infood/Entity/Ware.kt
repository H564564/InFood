package com.example.infood.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 class ware(
    var wareID: String,
    var wareName: String,
    var foodAmount: Int,
    var color: String,
    var lastSaw: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}