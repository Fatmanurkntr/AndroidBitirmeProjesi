package com.example.yemeksiparisuygulamasi.di

import android.content.Context
import com.example.yemeksiparisuygulamasi.data.local.AppDatabase
import com.example.yemeksiparisuygulamasi.data.local.FavoriYemekDao
import com.example.yemeksiparisuygulamasi.data.local.SiparisDao // YENİ: SiparisDao importu
import com.example.yemeksiparisuygulamasi.data.remote.ApiService
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Bağımlılıkların uygulama ömrü boyunca yaşamasını sağlar
object AppModule {

    private const val BASE_URL = "http://kasimadalan.pe.hu/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideFavoriYemekDao(appDatabase: AppDatabase): FavoriYemekDao {
        return appDatabase.favoriYemekDao()
    }

    // YENİ DAO İÇİN PROVIDER EKLENDİ
    @Provides
    @Singleton
    fun provideSiparisDao(appDatabase: AppDatabase): SiparisDao {
        return appDatabase.siparisDao()
    }

    // YEMEKLERREPOSITORY PROVIDER'I GÜNCELLENDİ
    @Provides
    @Singleton
    fun provideYemeklerRepository(
        apiService: ApiService,
        favoriYemekDao: FavoriYemekDao,
        siparisDao: SiparisDao // YENİ: SiparisDao parametre olarak eklendi
    ): YemeklerRepository {
        // YemeklerRepository constructor'ı artık 3 parametre almalı:
        // apiService, favoriYemekDao, ve siparisDao
        return YemeklerRepository(apiService, favoriYemekDao, siparisDao)
    }
}