package com.example.infood.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class food(
    var wareID: String,
    var foodID: String,
    var foodName: String,
    var foodAmount: Int,
    var foodUnit: String,
    var foodData: String,
    var foodTime: String,
    var buyTime: String,
    var foodFrom: String,
    var buyFrom: String,
    var foodType: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}