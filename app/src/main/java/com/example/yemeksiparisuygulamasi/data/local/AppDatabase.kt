package com.example.yemeksiparisuygulamasi.data.local // Kendi paket adınla değiştir

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yemeksiparisuygulamasi.data.model.FavoriYemek
import com.example.yemeksiparisuygulamasi.data.model.Siparis // YENİ ENTITY İMPORTU
import com.example.yemeksiparisuygulamasi.data.model.SiparisUrun // YENİ ENTITY İMPORTU

@Database(
    entities = [
        FavoriYemek::class,
        Siparis::class,      // YENİ ENTITY EKLENDİ
        SiparisUrun::class   // YENİ ENTITY EKLENDİ
    ],
    version = 2, // VERİTABANI VERSİYONU ARTIRILDI (Şema değiştiği için)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriYemekDao(): FavoriYemekDao
    abstract fun siparisDao(): SiparisDao // YENİ DAO METODU

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yemek_siparis_database" // Veritabanı dosyasının adı
                )
                    // Veritabanı şeması değiştiği için (yeni tablolar eklendi),
                    // bir migration stratejisi belirlememiz gerekiyor.
                    // Geliştirme aşamasında en basit yöntem, eski veritabanını silip
                    // yenisini oluşturmaktır. Bu, mevcut tüm verileri siler.
                    .fallbackToDestructiveMigration() // DİKKAT: Veri kaybına neden olur!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}