package com.example.yemeksiparisuygulamasi.viewmodel

import androidx.lifecycle.*
import com.example.yemeksiparisuygulamasi.data.model.Yemek
import com.example.yemeksiparisuygulamasi.data.repo.YemeklerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavorilerViewModel @Inject constructor(
    private val repository: YemeklerRepository
) : ViewModel() {

    private val _favoriYemekDetaylari = MediatorLiveData<List<YemekItemUiState>>()
    val favoriYemekDetaylari: LiveData<List<YemekItemUiState>> get() = _favoriYemekDetaylari

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Tüm yemekler API'den bir kez çekilir ve saklanır.
    private var tumYemeklerCache: List<Yemek>? = null

    init {
        favorileriVeYemekleriYukle()
    }

    private fun favorileriVeYemekleriYukle() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Önce API'den tüm yemekleri çek (eğer daha önce çekilmediyse)
            if (tumYemeklerCache == null) {
                try {
                    tumYemeklerCache = repository.tumYemekleriGetir()
                } catch (e: Exception) {
                    _errorMessage.value = "Yemek detayları yüklenemedi: ${e.localizedMessage}"
                    tumYemeklerCache = emptyList() // Hata durumunda boş liste
                }
            }

            // API'den yemekler geldikten sonra favorileri birleştir
            tumYemeklerCache?.let { apiYemekListesi ->
                // Favori ID'lerini (LiveData) ve API yemek listesini (cache) birleştirmek için MediatorLiveData kullanıyoruz.
                _favoriYemekDetaylari.addSource(repository.getTumFavoriler()) { favoriIdListesi ->
                    val favoriYemeklerListesi = apiYemekListesi.filter { apiYemek ->
                        favoriIdListesi.any { favori -> favori.yemekId == apiYemek.yemekId }
                    }.map { favoriYemekDetayi ->
                        YemekItemUiState(favoriYemekDetayi, true) // Favoriler sayfasında hepsi favori olacak
                    }
                    _favoriYemekDetaylari.value = favoriYemeklerListesi
                }
            } ?: run {
                _errorMessage.value = "Yemek listesi alınamadı, favoriler gösterilemiyor."
                _favoriYemekDetaylari.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    // Favori durumu değiştiğinde bu ViewModel'ı da güncellemek gerekebilir
    // veya Repository'deki LiveData'yı dinlemek yeterli olacaktır.
    // Şimdilik AnasayfaViewModel favori toggle işlemini yapıyor.
    // Eğer favoriler sayfasından da favori çıkarılacaksa, benzer bir toggle metodu eklenebilir.
    fun favoridenCikar(yemekId: String) {
        viewModelScope.launch {
            repository.favoridenSil(yemekId)
            // LiveData (repository.getTumFavoriler()) otomatik olarak tetiklenip listeyi güncelleyecektir.
        }
    }
}