package com.example.yemeksiparisuygulamasi.data.model


import com.google.gson.annotations.SerializedName

data class SepetYemeklerCevap(
    @SerializedName("sepet_yemekler")
    val sepetYemekler: List<SepetYemek>,
    @SerializedName("success")
    val success: Int
)