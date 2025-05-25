package com.example.yemeksiparisuygulamasi.viewmodel

import androidx.lifecycle.* // LiveData ve transformasyonlar için gerekli
import com.example.yemeksiparisuygulamasi.data.model.CRUDCevap
import com.example.yemeksiparisuygulamasi.data.model.Yemek // FavoriYemek import'u burada doğrudan gerekmeyebilir
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetayViewModel @Inject constructor(
    private val repository: YemeklerRepository
) : ViewModel() {

    private val _sepeteEklemeSonucu = MutableLiveData<CRUDCevap?>()
    val sepeteEklemeSonucu: LiveData<CRUDCevap?>
        get() = _sepeteEklemeSonucu

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _toastMesaji = MutableLiveData<String?>()
    val toastMesaji: LiveData<String?>
        get() = _toastMesaji

    // Detayları gösterilen yemeğin ID'sini tutar.
    // Bu LiveData, Fragment'tan setYemek() ile ayarlandığında isFavori LiveData'sını tetikler.
    private val _secilenYemek = MutableLiveData<Yemek?>()

    // Seçilen yemeğin favori durumunu anlık olarak takip eden LiveData
    val isFavori: LiveData<Boolean> = _secilenYemek.switchMap { yemek ->
        yemek?.let { y ->
            // repository.getTumFavoriler() tüm favori ID'lerini LiveData olarak döndürür.
            // Bu LiveData'yı mapleyerek mevcut yemeğin favori olup olmadığını kontrol ederiz.
            repository.getTumFavoriler().map { favoriListesi ->
                favoriListesi.any { it.yemekId == y.yemekId }
            }
        } ?: MutableLiveData<Boolean>().apply { value = false } // Eğer _secilenYemek null ise favori değil.
    }

    // Fragment'tan detayları gösterilecek yemeği ayarlamak için
    fun setSecilenYemek(yemek: Yemek) {
        _secilenYemek.value = yemek
    }

    fun sepeteYemekEkle(yemek: Yemek, adet: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val yemekFiyatInt = try { yemek.yemekFiyat.toInt() } catch (e: NumberFormatException) { 0 }

            val cevap = repository.sepeteEkle(
                yemek.yemekAdi,
                yemek.yemekResimAdi,
                yemekFiyatInt,
                adet
            )
            _sepeteEklemeSonucu.value = cevap
            _isLoading.value = false

            if (cevap?.success == 1) {
                _toastMesaji.value = "${yemek.yemekAdi} sepete eklendi!"
            } else {
                _toastMesaji.value = cevap?.message ?: "Sepete eklenirken bir sorun oluştu."
            }
        }
    }

    fun toastMesajiGosterildi() {
        _toastMesaji.value = null
    }

    // Seçili yemeğin favori durumunu değiştirmek için
    fun toggleFavoriDurumu() {
        _secilenYemek.value?.let { currentYemek ->
            viewModelScope.launch {
                try {
                    val suankiFavoriMi = isFavori.value ?: false // Mevcut favori durumunu al
                    if (suankiFavoriMi) {
                        repository.favoridenSil(currentYemek.yemekId)
                        _toastMesaji.value = "${currentYemek.yemekAdi} favorilerden çıkarıldı."
                    } else {
                        repository.favoriyeEkle(currentYemek.yemekId)
                        _toastMesaji.value = "${currentYemek.yemekAdi} favorilere eklendi."
                    }
                    // Favori durumu değiştiğinde isFavori LiveData'sı otomatik olarak güncellenecektir.
                } catch (e: Exception) {
                    _toastMesaji.value = "Favori işlemi sırasında hata: ${e.localizedMessage}"
                }
            }
        }
    }
}