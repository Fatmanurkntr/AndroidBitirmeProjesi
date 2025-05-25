package com.example.yemeksiparisuygulamasi.data.model // Veya data/local/entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "siparisler")
data class Siparis(
    @PrimaryKey(autoGenerate = true)
    val siparisId: Long = 0, // Otomatik artan ID
    val kullaniciAdi: String, // Hangi kullanıcıya ait olduğu
    val siparisTarihi: Long = System.currentTimeMillis(), // Sipariş zamanı (milisaniye)
    val toplamTutar: Double // Siparişin gönderim dahil toplam tutarı
) {
    // Tarihi okunabilir formatta almak için yardımcı fonksiyon
    fun getFormattedSiparisTarihi(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr", "TR"))
        return sdf.format(Date(siparisTarihi))
    }
}