package com.example.yemeksiparisuygulamasi.data.model // Kendi paket adınla değiştir

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favori_yemekler")
data class FavoriYemek(
    @PrimaryKey
    val yemekId: String // API'den gelen yemekId'si ile aynı olacak
)