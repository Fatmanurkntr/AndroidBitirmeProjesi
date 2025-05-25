package com.example.yemeksiparisuygulamasi.viewmodel

import androidx.lifecycle.*
import com.example.yemeksiparisuygulamasi.data.local.SiparisVeUrunleri
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import com.example.yemeksiparisuygulamasi.util.Constants
import com.example.yemeksiparisuygulamasi.util.SharedPrefsHelper // YENİ
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val repository: YemeklerRepository,
    private val sharedPrefsHelper: SharedPrefsHelper // YENİ: SharedPrefsHelper enjekte edildi
) : ViewModel() {

    val siparisGecmisi: LiveData<List<SiparisVeUrunleri>> =
        repository.getSiparisGecmisi(Constants.KULLANICI_ADI)

    private val _kayitliAdres = MutableLiveData<String?>()
    val kayitliAdres: LiveData<String?> get() = _kayitliAdres

    private val _adresDuzenlemeModunda = MutableLiveData<Boolean>(false)
    val adresDuzenlemeModunda: LiveData<Boolean> get() = _adresDuzenlemeModunda

    // SharedPreferences'tan alınacak ilk değerler için
    val adresSatiri1: MutableLiveData<String?> = MutableLiveData(sharedPrefsHelper.getAdresSatiri1())
    val adresSatiri2: MutableLiveData<String?> = MutableLiveData(sharedPrefsHelper.getAdresSatiri2())
    val ilce: MutableLiveData<String?> = MutableLiveData(sharedPrefsHelper.getAdresIlce())
    val il: MutableLiveData<String?> = MutableLiveData(sharedPrefsHelper.getAdresIl())


    init {
        yukleKayitliAdres()
    }

    private fun yukleKayitliAdres() {
        if (sharedPrefsHelper.adresVarMi()) {
            _kayitliAdres.value = sharedPrefsHelper.tumAdresiGetirFormatted()
            _adresDuzenlemeModunda.value = false // Adres varsa düzenleme modunda başlama
        } else {
            _kayitliAdres.value = null
            _adresDuzenlemeModunda.value = true // Adres yoksa direkt düzenleme modunda başla
        }
    }

    fun adresDuzenleModunuToggle() {
        val yeniMod = !(_adresDuzenlemeModunda.value ?: false)
        _adresDuzenlemeModunda.value = yeniMod
        if (yeniMod) { // Düzenleme moduna geçiliyorsa, mevcut değerleri EditText'lere yükle
            adresSatiri1.value = sharedPrefsHelper.getAdresSatiri1()
            adresSatiri2.value = sharedPrefsHelper.getAdresSatiri2()
            ilce.value = sharedPrefsHelper.getAdresIlce()
            il.value = sharedPrefsHelper.getAdresIl()
        }
    }

    fun adresiKaydet(yeniAdres1: String?, yeniAdres2: String?, yeniIlce: String?, yeniIl: String?) {
        sharedPrefsHelper.kaydetAdres(yeniAdres1, yeniAdres2, yeniIlce, yeniIl)
        yukleKayitliAdres() // UI'ı güncellemek için adresi yeniden yükle
        _adresDuzenlemeModunda.value = false // Düzenleme modunu kapat
    }

    fun gecmisiTemizle() {
        viewModelScope.launch {
            repository.siparisGecmisiniTemizle(Constants.KULLANICI_ADI)
        }
    }
}