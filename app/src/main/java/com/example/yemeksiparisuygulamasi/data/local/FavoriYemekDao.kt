package com.example.yemeksiparisuygulamasi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yemeksiparisuygulamasi.data.model.FavoriYemek

@Dao
interface FavoriYemekDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE) // Aynı ID'li yemek eklenirse üzerine yazar
    suspend fun favoriEkle(favoriYemek: FavoriYemek)

    @Delete
    suspend fun favoriSil(favoriYemek: FavoriYemek)

    @Query("SELECT * FROM favori_yemekler")
    fun tumFavorileriGetir(): LiveData<List<FavoriYemek>> // LiveData ile UI otomatik güncellenir

    @Query("SELECT EXISTS(SELECT 1 FROM favori_yemekler WHERE yemekId = :yemekId)")
    suspend fun isFavori(yemekId: String): Boolean // Belirli bir yemeğin favori olup olmadığını kontrol eder
}