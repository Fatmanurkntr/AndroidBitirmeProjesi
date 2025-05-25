package com.example.yemeksiparisuygulamasi.data.model // Veya data/local/entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "siparis_urunleri",
    foreignKeys = [ForeignKey(
        entity = Siparis::class,
        parentColumns = ["siparisId"],
        childColumns = ["anaSiparisId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SiparisUrun(
    @PrimaryKey(autoGenerate = true)
    val siparisUrunId: Long = 0,
    var anaSiparisId: Long, // "val" yerine "var" YAPILDI
    val yemekAdi: String,
    val yemekResimAdi: String,
    val yemekFiyat: Double,
    val yemekSiparisAdet: Int
)