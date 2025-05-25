package com.example.yemeksiparisuygulamasi.viewmodel

import android.util.Log // Loglama için
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yemeksiparisuygulamasi.data.model.CRUDCevap
import com.example.yemeksiparisuygulamasi.data.model.SepetYemek
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import com.example.yemeksiparisuygulamasi.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
// NumberFormat ve Locale importları artık burada gerekmeyebilir, Fragment'ta kullanılacak.
// import java.text.NumberFormat
// import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class SepetViewModel @Inject constructor(
    private val repository: YemeklerRepository
) : ViewModel() {

    private val _sepetListesi = MutableLiveData<List<SepetYemek>>()
    val sepetListesi: LiveData<List<SepetYemek>> get() = _sepetListesi

    // Bu LiveData'yı Fragment'ta spesifik bir işlem için kullanmıyorsak kaldırabiliriz.
    // Silme geri bildirimi _toastMesaji ile yapılıyor.
    // private val _sepettenSilmeSonucu = MutableLiveData<Pair<CRUDCevap?, Int>>()
    // val sepettenSilmeSonucu: LiveData<Pair<CRUDCevap?, Int>> get() = _sepettenSilmeSonucu

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _toastMesaji = MutableLiveData<String?>() // Snackbar mesajları için
    val toastMesaji: LiveData<String?> get() = _toastMesaji

    private val _yemeklerAraToplamInternal = MutableLiveData<Double>(0.0)
    val yemeklerAraToplam: LiveData<Double> get() = _yemeklerAraToplamInternal

    private val _gonderimUcretiInternal = MutableLiveData<Double>(0.0)
    val gonderimUcreti: LiveData<Double> get() = _gonderimUcretiInternal

    private val _genelToplamInternal = MutableLiveData<Double>(0.0)
    val genelToplam: LiveData<Double> get() = _genelToplamInternal

    private val _bedavaGonderimeKalanInternal = MutableLiveData<Double>(Constants.BEDAVA_GONDERIM_LIMITI)
    val bedavaGonderimeKalan: LiveData<Double> get() = _bedavaGonderimeKalanInternal

    // Siparişin başarıyla verilip verilmediğini Fragment'a bildirmek için
    private val _siparisVerildiEvent = MutableLiveData<Boolean?>()
    val siparisVerildiEvent: LiveData<Boolean?> get() = _siparisVerildiEvent

    init {
        Log.d("SepetViewModel", "ViewModel başlatılıyor (init).")
        // sepetiYukle() onResume'da çağrılacak.
    }

    fun sepetiYukle() {
        viewModelScope.launch {
            Log.d("SepetViewModel", "sepetiYukle() çağrıldı.")
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val liste = repository.sepettekiYemekleriGetir() // API'den sepeti al
                if (liste != null) {
                    Log.d("SepetViewModel", "API'den ${liste.size} sepet ürünü alındı.")
                    _sepetListesi.value = liste
                    hesaplaTutarVeGonderim(liste)
                } else {
                    Log.e("SepetViewModel", "Sepet API yanıtı null.")
                    _errorMessage.value = "Sepet yüklenirken bir sorun oluştu (API'den veri alınamadı)."
                    _sepetListesi.value = emptyList()
                    hesaplaTutarVeGonderim(emptyList())
                }
            } catch (e: Exception) {
                Log.e("SepetViewModel", "sepetiYukle Exception: ${e.message}", e)
                _errorMessage.value = "Sepet yükleme hatası: ${e.localizedMessage}"
                _sepetListesi.value = emptyList()
                hesaplaTutarVeGonderim(emptyList())
            } finally {
                _isLoading.value = false
                Log.d("SepetViewModel", "sepetiYukle() tamamlandı. isLoading: ${_isLoading.value}")
            }
        }
    }

    fun sepettenYemekSil(sepetYemek: SepetYemek, position: Int) {
        viewModelScope.launch {
            Log.d("SepetViewModel", "${sepetYemek.yemekAdi} siliniyor...")
            _isLoading.value = true // Silme işlemi için de loading gösterilebilir
            val sepetYemekIdInt = try {
                sepetYemek.sepetYemekId.toInt()
            } catch (e: NumberFormatException) {
                _toastMesaji.value = "Geçersiz ürün ID'si."
                _isLoading.value = false
                return@launch
            }

            try {
                val cevap = repository.sepettenSil(sepetYemekIdInt)
                if (cevap?.success == 1) {
                    _toastMesaji.value = "${sepetYemek.yemekAdi} sepetten silindi."
                    // Sepeti yeniden yükle (bu, tutarları ve listeyi güncelleyecektir)
                    // Alternatif olarak, listeden manuel çıkarıp tutarı yeniden hesaplayabilirsiniz
                    // ama API'den tekrar çekmek daha senkronize bir durum sağlar.
                    val guncelListe = _sepetListesi.value?.toMutableList()
                    guncelListe?.removeAll { it.sepetYemekId == sepetYemek.sepetYemekId } // Aynı ID'li tümünü sil
                    _sepetListesi.value = guncelListe ?: emptyList()
                    hesaplaTutarVeGonderim(guncelListe ?: emptyList())

                } else {
                    _toastMesaji.value = cevap?.message ?: "Sepetten silinirken bir sorun oluştu."
                }
            } catch (e: Exception) {
                Log.e("SepetViewModel", "sepettenYemekSil Exception: ${e.message}", e)
                _toastMesaji.value = "Silme işlemi sırasında bir hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun hesaplaTutarVeGonderim(liste: List<SepetYemek>) {
        var araToplamHesaplanan = 0.0
        liste.forEach { sepetUrunu ->
            val fiyat = sepetUrunu.yemekFiyat.toDoubleOrNull() ?: 0.0
            val adet = sepetUrunu.yemekSiparisAdet.toIntOrNull() ?: 0
            araToplamHesaplanan += fiyat * adet
        }
        _yemeklerAraToplamInternal.value = araToplamHesaplanan

        val guncelGonderimUcreti: Double
        val kalanTutar: Double

        if (liste.isEmpty()) {
            guncelGonderimUcreti = 0.0
            kalanTutar = Constants.BEDAVA_GONDERIM_LIMITI
        } else if (araToplamHesaplanan >= Constants.BEDAVA_GONDERIM_LIMITI) {
            guncelGonderimUcreti = 0.0
            kalanTutar = 0.0
        } else {
            guncelGonderimUcreti = Constants.GONDERIM_UCRETI
            kalanTutar = max(0.0, Constants.BEDAVA_GONDERIM_LIMITI - araToplamHesaplanan)
        }

        _gonderimUcretiInternal.value = guncelGonderimUcreti
        _bedavaGonderimeKalanInternal.value = kalanTutar
        _genelToplamInternal.value = araToplamHesaplanan + guncelGonderimUcreti
        Log.d("SepetViewModel", "Tutarlar hesaplandı: AraToplam=${_yemeklerAraToplamInternal.value}, Gonderim=${_gonderimUcretiInternal.value}, GenelToplam=${_genelToplamInternal.value}, Kalan=${_bedavaGonderimeKalanInternal.value}")
    }

    fun toastMesajiGosterildi() {
        _toastMesaji.value = null
    }

    // YENİ FONKSİYON: Sepeti onayla ve Room'a kaydet
    fun sepetiOnaylaVeKaydet() {
        val mevcutSepet = _sepetListesi.value
        val mevcutGenelToplam = _genelToplamInternal.value

        if (mevcutSepet.isNullOrEmpty() || mevcutGenelToplam == null) {
            _toastMesaji.value = "Sipariş vermek için sepetinizde ürün olmalıdır."
            _siparisVerildiEvent.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("SepetViewModel", "${mevcutSepet.size} ürün ve toplam ${mevcutGenelToplam} TL tutarında sipariş veriliyor.")
                repository.siparisVer(Constants.KULLANICI_ADI, mevcutSepet, mevcutGenelToplam)

                // Sipariş verildikten sonra API'deki sepeti de temizlememiz gerekiyor.
                // Bu, her bir ürünü sepetten sil API'si ile tek tek yaparak olur.
                // Veya API'nin tüm sepeti temizleyen bir endpoint'i varsa o kullanılır.
                // Şimdilik bu projede API'deki sepeti temizlemeyi basitleştirelim ve
                // sadece yerel listeyi ve tutarları sıfırlayalım.
                // Gerçek bir uygulamada bu senkronizasyon çok önemli olurdu.
                for (sepetUrunu in mevcutSepet) {
                    val sepetYemekIdInt = sepetUrunu.sepetYemekId.toIntOrNull()
                    if (sepetYemekIdInt != null) {
                        repository.sepettenSil(sepetYemekIdInt) // API'den de sil
                    }
                }

                _sepetListesi.value = emptyList() // Yerel listeyi boşalt
                hesaplaTutarVeGonderim(emptyList()) // Tutarları sıfırla
                _toastMesaji.value = "Siparişiniz başarıyla oluşturuldu!"
                _siparisVerildiEvent.value = true // Event'i tetikle
            } catch (e: Exception) {
                Log.e("SepetViewModel", "sepetiOnaylaVeKaydet Exception: ${e.message}", e)
                _errorMessage.value = "Sipariş oluşturulurken hata: ${e.localizedMessage}"
                _siparisVerildiEvent.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun siparisVerildiEventiKullanildi() {
        _siparisVerildiEvent.value = null
    }
}