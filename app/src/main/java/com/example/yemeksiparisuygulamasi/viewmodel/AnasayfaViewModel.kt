package com.example.yemeksiparisuygulamasi.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.yemeksiparisuygulamasi.data.model.FavoriYemek
import com.example.yemeksiparisuygulamasi.data.model.Yemek
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class YemekItemUiState(
    val yemek: Yemek,
    var isFavori: Boolean
)

@HiltViewModel
class AnasayfaViewModel @Inject constructor(
    private val repository: YemeklerRepository
) : ViewModel() {

    private val _yemekListesiApi = MutableLiveData<List<Yemek>?>()
    private val _seciliKategoriAdiInternal = MutableLiveData<String?>(null)
    val seciliKategoriAdi: LiveData<String?> get() = _seciliKategoriAdiInternal

    // Favorilerden gelen LiveData'yı doğrudan kullanalım
    private val favorilerLiveData: LiveData<List<FavoriYemek>> = repository.getTumFavoriler()

    val yemekListesiUiState: LiveData<List<YemekItemUiState>> =
        MediatorLiveData<List<YemekItemUiState>>().apply {
            // Bu fonksiyon, dinlenen kaynak LiveData'lardan herhangi biri değiştiğinde çağrılır.
            fun updateCombinedList() {
                val currentFavoriler = favorilerLiveData.value ?: emptyList()
                val currentApiYemekler = _yemekListesiApi.value
                val currentKategori = _seciliKategoriAdiInternal.value

                Log.d("ViewModelUpdate", "updateCombinedList: FavoriSayisi=${currentFavoriler.size}, ApiYemekSayisi=${currentApiYemekler?.size ?: 0}, Kategori=$currentKategori")

                val newList = birlestirVeFiltrele(currentFavoriler, currentApiYemekler, currentKategori)
                // Sadece değer gerçekten değiştiyse post et (gereksiz UI güncellemelerini önleyebilir)
                if (value != newList) {
                    value = newList
                    Log.d("ViewModelUpdate", "yemekListesiUiState güncellendi. Yeni liste boyutu: ${newList.size}")
                } else {
                    Log.d("ViewModelUpdate", "yemekListesiUiState DEĞİŞMEDİ, aynı liste.")
                }
            }

            addSource(favorilerLiveData) {
                Log.d("ViewModelUpdate", "KAYNAK DEĞİŞTİ: favorilerLiveData")
                updateCombinedList()
            }
            addSource(_yemekListesiApi) {
                Log.d("ViewModelUpdate", "KAYNAK DEĞİŞTİ: _yemekListesiApi")
                updateCombinedList()
            }
            addSource(_seciliKategoriAdiInternal) {
                Log.d("ViewModelUpdate", "KAYNAK DEĞİŞTİ: _seciliKategoriAdiInternal")
                updateCombinedList()
            }
        }

    private fun birlestirVeFiltrele(
        favoriIdListesi: List<FavoriYemek>,
        apiYemekListesi: List<Yemek>?,
        kategoriAdi: String?
    ): List<YemekItemUiState> {
        val gecerliApiListesi = apiYemekListesi ?: return emptyList()
        Log.d("ViewModelFilter", "Filtreleme: Kategori='${kategoriAdi}', API Liste Boyutu=${gecerliApiListesi.size}, Gelen Favori Sayısı=${favoriIdListesi.size}")

        val filtrelenmisListe = if (kategoriAdi.isNullOrEmpty() || kategoriAdi.equals("Tümü", ignoreCase = true)) {
            gecerliApiListesi
        } else {
            gecerliApiListesi.filter { yemek ->
                val yemekAdiLower = yemek.yemekAdi.lowercase().trim()
                when (kategoriAdi.lowercase()) {
                    "yiyecekler" -> !listOf("ayran", "fanta", "kahve", "su", "kola", "limonata", "çay", "baklava", "sütlaç", "kadayıf", "tiramisu", "pasta", "kek", "dondurma").any { terim -> yemekAdiLower.contains(terim) }
                    "içecekler" -> listOf("ayran", "fanta", "kahve", "su", "kola", "limonata", "çay").any { terim -> yemekAdiLower.contains(terim) }
                    "tatlılar" -> listOf("baklava", "kadayıf", "sütlaç", "tiramisu", "pasta", "kek", "dondurma").any { terim -> yemekAdiLower.contains(terim) }
                    else -> true
                }
            }
        }

        return filtrelenmisListe.map { apiYemek ->
            val isFav = favoriIdListesi.any { favori -> favori.yemekId == apiYemek.yemekId }
            // Log.d("ViewModelFilter", "Yemek: ${apiYemek.yemekAdi}, Favori mi: $isFav") // Çok fazla log üretebilir
            YemekItemUiState(
                yemek = apiYemek,
                isFavori = isFav
            )
        }
    }

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        Log.d("AnasayfaViewModel", "ViewModel başlatılıyor (init).")
        if (_yemekListesiApi.value.isNullOrEmpty()) {
            yemekleriYukle()
        }
    }

    fun yemekleriYukle() {
        if (_isLoading.value == true) return
        viewModelScope.launch {
            Log.d("AnasayfaViewModel", "yemekleriYukle() başlatılıyor.")
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val apiYaniti = repository.tumYemekleriGetir()
                _yemekListesiApi.value = apiYaniti ?: emptyList()
                if (apiYaniti == null) {
                    _errorMessage.value = "Yemekler yüklenirken bir sorun oluştu (API'den veri alınamadı)."
                }
                Log.d("AnasayfaViewModel", "API'den ${apiYaniti?.size ?: 0} yemek alındı.")
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "yemekleriYukle Exception: ${e.message}", e)
                _errorMessage.value = "Hata: ${e.localizedMessage}"
                _yemekListesiApi.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavoriDurumu(yemekId: String, suankiFavoriMi: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("AnasayfaViewModel", "toggleFavoriDurumu: $yemekId, Mevcut Durum: $suankiFavoriMi. Repository çağrılacak.")
                if (suankiFavoriMi) {
                    repository.favoridenSil(yemekId)
                } else {
                    repository.favoriyeEkle(yemekId)
                }
                Log.d("AnasayfaViewModel", "toggleFavoriDurumu: DB işlemi tamamlandı (çağrıldı). Favori LiveData'sının tetiklenmesi bekleniyor.")
            } catch (e: Exception) { /* ... */ }
        }
    }

    fun kategoriSecildi(kategoriAdi: String?) {
        Log.d("AnasayfaViewModel", "kategoriSecildi çağrıldı. Yeni Kategori: $kategoriAdi")
        val mevcutKategori = _seciliKategoriAdiInternal.value
        if (mevcutKategori == kategoriAdi && kategoriAdi != null) {
            _seciliKategoriAdiInternal.value = null
        } else {
            _seciliKategoriAdiInternal.value = kategoriAdi
        }
        if (_yemekListesiApi.value.isNullOrEmpty() && _isLoading.value == false) {
            yemekleriYukle()
        }
    }
}