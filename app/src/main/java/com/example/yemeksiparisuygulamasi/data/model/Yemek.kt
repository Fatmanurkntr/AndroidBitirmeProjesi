package com.example.yemeksiparisuygulamasi.data.model // Kendi paket adınla değiştir

import android.os.Parcelable // Parcelable import etmeyi unutma
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize // Parcelize import etmeyi unutma

@Parcelize // @Parcelize anotasyonunu sınıfın başına ekle
data class Yemek(
    @SerializedName("yemek_id")
    val yemekId: String,
    @SerializedName("yemek_adi")
    val yemekAdi: String,
    @SerializedName("yemek_resim_adi")
    val yemekResimAdi: String,
    @SerializedName("yemek_fiyat")
    val yemekFiyat: String
) : Parcelable // : Parcelable ekle