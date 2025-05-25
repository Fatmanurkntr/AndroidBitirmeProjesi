package com.example.yemeksiparisuygulamasi.data.remote


import com.example.yemeksiparisuygulamasi.data.model.CRUDCevap
import com.example.yemeksiparisuygulamasi.data.model.SepetYemeklerCevap
import com.example.yemeksiparisuygulamasi.data.model.YemeklerCevap
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    companion object {
        const val BASE_URL_RESIMLER = "http://kasimadalan.pe.hu/yemekler/resimler/"
    }

    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun getYemekler(): Response<YemeklerCevap>

    @POST("yemekler/sepeteYemekEkle.php")
    @FormUrlEncoded
    suspend fun sepeteYemekEkle(
        @Field("yemek_adi") yemekAdi: String,
        @Field("yemek_resim_adi") yemekResimAdi: String,
        @Field("yemek_fiyat") yemekFiyat: Int,
        @Field("yemek_siparis_adet") yemekSiparisAdet: Int,
        @Field("kullanici_adi") kullaniciAdi: String
    ): Response<CRUDCevap>

    @POST("yemekler/sepettekiYemekleriGetir.php")
    @FormUrlEncoded
    suspend fun getSepetYemekler(
        @Field("kullanici_adi") kullaniciAdi: String
    ): Response<SepetYemeklerCevap>

    @POST("yemekler/sepettenYemekSil.php")
    @FormUrlEncoded
    suspend fun sepettenYemekSil(
        @Field("sepet_yemek_id") sepetYemekId: Int,
        @Field("kullanici_adi") kullaniciAdi: String
    ): Response<CRUDCevap>
}