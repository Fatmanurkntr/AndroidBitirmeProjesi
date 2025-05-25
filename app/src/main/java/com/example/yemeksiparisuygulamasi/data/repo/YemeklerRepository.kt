package com.example.yemeksiparisuygulamasi.data.repo

import androidx.lifecycle.LiveData
import com.example.yemeksiparisuygulamasi.data.local.FavoriYemekDao
import com.example.yemeksiparisuygulamasi.data.local.SiparisDao // YENİ: SiparisDao importu
import com.example.yemeksiparisuygulamasi.data.local.SiparisVeUrunleri // YENİ: SiparisVeUrunleri importu
import com.example.yemeksiparisuygulamasi.data.model.CRUDCevap
import com.example.yemeksiparisuygulamasi.data.model.FavoriYemek
import com.example.yemeksiparisuygulamasi.data.model.SepetYemek
import com.example.yemeksiparisuygulamasi.data.model.Siparis // YENİ: Siparis importu
import com.example.yemeksiparisuygulamasi.data.model.SiparisUrun // YENİ: SiparisUrun importu
import com.example.yemeksiparisuygulamasi.data.model.Yemek
import com.example.yemeksiparisuygulamasi.data.remote.ApiService
import com.example.yemeksiparisuygulamasi.util.Constants
import javax.inject.Inject

class YemeklerRepository @Inject constructor(
    private val apiService: ApiService,
    private val favoriYemekDao: FavoriYemekDao,
    private val siparisDao: SiparisDao // YENİ: SiparisDao constructor'a eklendi
) {

    // API İşlemleri (Mevcut olanlar)
    suspend fun tumYemekleriGetir(): List<Yemek>? {
        return try {
            val response = apiService.getYemekler()
            if (response.isSuccessful) {
                response.body()?.let { yemeklerCevap ->
                    if (yemeklerCevap.success == 1) {
                        yemeklerCevap.yemekler
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sepeteEkle(yemekAdi: String, yemekResimAdi: String, yemekFiyat: Int, yemekSiparisAdet: Int): CRUDCevap? {
        return try {
            val response = apiService.sepeteYemekEkle(
                yemekAdi,
                yemekResimAdi,
                yemekFiyat,
                yemekSiparisAdet,
                Constants.KULLANICI_ADI
            )
            response.body()
        } catch (e: Exception) {
            CRUDCevap(0, "İstek sırasında hata: ${e.localizedMessage}")
        }
    }

    suspend fun sepettekiYemekleriGetir(): List<SepetYemek>? {
        return try {
            val response = apiService.getSepetYemekler(Constants.KULLANICI_ADI)
            if (response.isSuccessful) {
                response.body()?.let { sepetCevap ->
                    if (sepetCevap.success == 1) {
                        sepetCevap.sepetYemekler
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sepettenSil(sepetYemekId: Int): CRUDCevap? {
        return try {
            val response = apiService.sepettenYemekSil(
                sepetYemekId,
                Constants.KULLANICI_ADI
            )
            response.body()
        } catch (e: Exception) {
            CRUDCevap(0, "İstek sırasında hata: ${e.localizedMessage}")
        }
    }

    // Favori İşlemleri (Mevcut olanlar)
    suspend fun favoriyeEkle(yemekId: String) {
        val favoriYemek = FavoriYemek(yemekId)
        favoriYemekDao.favoriEkle(favoriYemek)
    }

    suspend fun favoridenSil(yemekId: String) {
        val favoriYemek = FavoriYemek(yemekId)
        favoriYemekDao.favoriSil(favoriYemek)
    }

    fun getTumFavoriler(): LiveData<List<FavoriYemek>> {
        return favoriYemekDao.tumFavorileriGetir()
    }

    suspend fun isYemekFavori(yemekId: String): Boolean {
        return favoriYemekDao.isFavori(yemekId)
    }

    // YENİ SİPARİŞ METODLARI
    suspend fun siparisVer(kullaniciAdi: String, sepetUrunleri: List<SepetYemek>, genelToplam: Double) {
        val yeniSiparis = Siparis(kullaniciAdi = kullaniciAdi, toplamTutar = genelToplam)
        val siparisEdilenUrunler = sepetUrunleri.map { sepetUrun ->
            SiparisUrun(
                // anaSiparisId DAO içindeki transaction'da atanacak
                anaSiparisId = 0, // Geçici değer, DAO'da güncellenecek
                yemekAdi = sepetUrun.yemekAdi,
                yemekResimAdi = sepetUrun.yemekResimAdi,
                yemekFiyat = sepetUrun.yemekFiyat.toDoubleOrNull() ?: 0.0,
                yemekSiparisAdet = sepetUrun.yemekSiparisAdet.toIntOrNull() ?: 0
            )
        }
        siparisDao.yeniSiparisOlustur(yeniSiparis, siparisEdilenUrunler)
    }

    fun getSiparisGecmisi(kullaniciAdi: String): LiveData<List<SiparisVeUrunleri>> {
        return siparisDao.getKullanicininSiparisleriVeUrunleri(kullaniciAdi)
    }

    suspend fun siparisGecmisiniTemizle(kullaniciAdi: String) {
        siparisDao.kullanicininSiparisGecmisiniTemizle(kullaniciAdi)
    }
}