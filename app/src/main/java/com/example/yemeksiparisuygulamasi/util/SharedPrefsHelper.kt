package com.example.yemeksiparisuygulamasi.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Uygulama boyunca tek bir örneği olması için
class SharedPrefsHelper @Inject constructor(@ApplicationContext context: Context) {

    private val PREFS_NAME = "YemekAppPrefs"
    private val KEY_ADRES_SATIRI_1 = "adresSatiri1"
    private val KEY_ADRES_SATIRI_2 = "adresSatiri2" // Opsiyonel
    private val KEY_ADRES_ILCE = "adresIlce"
    private val KEY_ADRES_IL = "adresIl"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun kaydetAdres(adresSatiri1: String?, adresSatiri2: String?, ilce: String?, il: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_ADRES_SATIRI_1, adresSatiri1)
            putString(KEY_ADRES_SATIRI_2, adresSatiri2)
            putString(KEY_ADRES_ILCE, ilce)
            putString(KEY_ADRES_IL, il)
            apply()
        }
    }

    fun getAdresSatiri1(): String? = sharedPreferences.getString(KEY_ADRES_SATIRI_1, null)
    fun getAdresSatiri2(): String? = sharedPreferences.getString(KEY_ADRES_SATIRI_2, null)
    fun getAdresIlce(): String? = sharedPreferences.getString(KEY_ADRES_ILCE, null)
    fun getAdresIl(): String? = sharedPreferences.getString(KEY_ADRES_IL, null)

    fun adresVarMi(): Boolean {
        return !getAdresSatiri1().isNullOrEmpty() // Sadece bir alanı kontrol etmek yeterli olabilir
    }

    fun tumAdresiGetirFormatted(): String {
        val satir1 = getAdresSatiri1()
        val satir2 = getAdresSatiri2()
        val ilceStr = getAdresIlce()
        val ilStr = getAdresIl()

        val adresBuilder = StringBuilder()
        if (!satir1.isNullOrEmpty()) adresBuilder.append(satir1).append("\n")
        if (!satir2.isNullOrEmpty()) adresBuilder.append(satir2).append("\n")
        if (!ilceStr.isNullOrEmpty() && !ilStr.isNullOrEmpty()) {
            adresBuilder.append(ilceStr).append(" / ").append(ilStr)
        } else if (!ilceStr.isNullOrEmpty()) {
            adresBuilder.append(ilceStr)
        } else if (!ilStr.isNullOrEmpty()) {
            adresBuilder.append(ilStr)
        }
        return adresBuilder.toString().trim()
    }
}