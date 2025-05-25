package com.example.yemeksiparisuygulamasi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.yemeksiparisuygulamasi.data.model.Siparis
import com.example.yemeksiparisuygulamasi.data.model.SiparisUrun

// Sipariş ve ürünlerini bir arada tutacak data class
data class SiparisVeUrunleri(
    @androidx.room.Embedded val siparis: Siparis,
    @androidx.room.Relation(
        parentColumn = "siparisId",
        entityColumn = "anaSiparisId"
    )
    val urunler: List<SiparisUrun>
)

@Dao
interface SiparisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun siparisEkle(siparis: Siparis): Long // Eklenen siparişin ID'sini döndürür

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun siparisUrunleriEkle(urunler: List<SiparisUrun>)

    // Sipariş ve ürünlerini birlikte çekmek için Transaction kullanılır
    @Transaction
    @Query("SELECT * FROM siparisler WHERE kullaniciAdi = :kullaniciAdi ORDER BY siparisTarihi DESC")
    fun getKullanicininSiparisleriVeUrunleri(kullaniciAdi: String): LiveData<List<SiparisVeUrunleri>>

    @Transaction
    suspend fun yeniSiparisOlustur(siparis: Siparis, urunler: List<SiparisUrun>) {
        val siparisId = siparisEkle(siparis)
        urunler.forEach { it.anaSiparisId = siparisId } // Her ürüne ana sipariş ID'sini ata
        siparisUrunleriEkle(urunler)
    }

    // Tüm siparişleri silmek için (opsiyonel, test için veya kullanıcı isterse)
    @Query("DELETE FROM siparisler WHERE kullaniciAdi = :kullaniciAdi")
    suspend fun kullanicininTumSiparisleriniSil(kullaniciAdi: String)

    @Query("DELETE FROM siparis_urunleri WHERE anaSiparisId IN (SELECT siparisId FROM siparisler WHERE kullaniciAdi = :kullaniciAdi)")
    suspend fun kullanicininTumSiparisUrunleriniSil(kullaniciAdi: String)

    @Transaction
    suspend fun kullanicininSiparisGecmisiniTemizle(kullaniciAdi: String){
        kullanicininTumSiparisUrunleriniSil(kullaniciAdi) // Önce ürünler (foreign key constraint)
        kullanicininTumSiparisleriniSil(kullaniciAdi)
    }
}