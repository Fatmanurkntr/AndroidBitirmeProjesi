package com.example.yemeksiparisuygulamasi // Kendi paket adınla değiştir

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YemekSiparisApplication : Application() {
    // Genellikle burası boş kalır veya uygulama genelinde başlatılması gereken
    // kütüphaneler (Timber gibi) burada initialize edilir.
}