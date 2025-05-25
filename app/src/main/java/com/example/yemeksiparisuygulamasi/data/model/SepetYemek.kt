package com.example.yemeksiparisuygulamasi.data.model

import com.google.gson.annotations.SerializedName

data class SepetYemek(
    @SerializedName("sepet_yemek_id")
    val sepetYemekId: String,
    @SerializedName("yemek_adi")
    val yemekAdi: String,
    @SerializedName("yemek_resim_adi")
    val yemekResimAdi: String,
    @SerializedName("yemek_fiyat")
    val yemekFiyat: String,
    @SerializedName("yemek_siparis_adet")
    val yemekSiparisAdet: String,
    @SerializedName("kullanici_adi")
    val kullaniciAdi: String
)