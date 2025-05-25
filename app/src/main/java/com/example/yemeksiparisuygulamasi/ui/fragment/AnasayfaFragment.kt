package com.example.yemeksiparisuygulamasi.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.adapter.KategorilerAdapter
import com.example.yemeksiparisuygulamasi.adapter.YemeklerAdapter
import Kategori
import com.example.yemeksiparisuygulamasi.databinding.FragmentAnasayfaBinding
import com.example.yemeksiparisuygulamasi.viewmodel.AnasayfaViewModel
import com.example.yemeksiparisuygulamasi.viewmodel.DetayViewModel
import com.example.yemeksiparisuygulamasi.viewmodel.YemekItemUiState // Bu importun doğru olduğundan emin olun
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnasayfaFragment : Fragment() {

    private var _binding: FragmentAnasayfaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnasayfaViewModel by viewModels()
    private val detayViewModel: DetayViewModel by viewModels()

    private lateinit var yemeklerAdapter: YemeklerAdapter
    private lateinit var kategorilerAdapter: KategorilerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AppLifecycle", "AnasayfaFragment onCreateView BAŞLADI.")
        _binding = FragmentAnasayfaBinding.inflate(inflater, container, false)
        Log.d("AppLifecycle", "AnasayfaFragment onCreateView TAMAMLANDI.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AppLifecycle", "AnasayfaFragment onViewCreated BAŞLADI.")

        setupBanner()
        setupKategorilerRecyclerView()
        setupYemeklerRecyclerView()
        observeViewModel()
        setupSearchView()

        detayViewModel.toastMesaji.observe(viewLifecycleOwner) { mesaj ->
            mesaj?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                detayViewModel.toastMesajiGosterildi()
            }
        }
        Log.d("AppLifecycle", "AnasayfaFragment onViewCreated TAMAMLANDI.")
    }

    private fun setupBanner() {
        // fragment_anasayfa.xml'de imageViewOzelTeklifBanner ID'li ImageView olmalı
        Glide.with(this)
            .load(R.drawable.sample_offer_banner) // drawable'a örnek bir banner resmi ekleyin
            .centerCrop()
            .into(binding.imageViewOzelTeklifBanner)

        binding.cardViewOzelTeklifler.setOnClickListener {
            Snackbar.make(binding.root, "Özel Teklifler Tıklandı!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupKategorilerRecyclerView() {
        // fragment_anasayfa.xml'de recyclerViewKategoriler ID'li RecyclerView olmalı
        val kategoriler = listOf(
            Kategori("Tümü", R.drawable.ic_kategori_tum), // ic_kategori_tum drawable'ı ekleyin
            Kategori("Yiyecekler", R.drawable.ic_kategori_yemek),
            Kategori("İçecekler", R.drawable.ic_kategori_icecek),
            Kategori("Tatlılar", R.drawable.ic_kategori_tatli)
        )

        kategorilerAdapter = KategorilerAdapter(kategoriler) { secilenKategori ->
            val filtreKategoriAdi = if (secilenKategori.ad.equals("Tümü", ignoreCase = true)) null else secilenKategori.ad
            viewModel.kategoriSecildi(filtreKategoriAdi)
            val mesaj = if (filtreKategoriAdi == null) "Tüm yemekler gösteriliyor." else "${secilenKategori.ad} kategorisi seçildi."
            Snackbar.make(binding.root, mesaj, Snackbar.LENGTH_SHORT).show()
        }
        binding.recyclerViewKategoriler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = kategorilerAdapter
        }
    }

    private fun setupYemeklerRecyclerView() {
        // fragment_anasayfa.xml'de recyclerViewYemekler ID'li RecyclerView olmalı
        yemeklerAdapter = YemeklerAdapter(
            yemekUiStateListesi = emptyList(),
            onItemClicked = { yemek ->
                val action = AnasayfaFragmentDirections.actionAnasayfaFragmentToDetayFragment(yemek)
                findNavController().navigate(action)
            },
            onSepeteEkleClicked = { yemek ->
                detayViewModel.sepeteYemekEkle(yemek, 1)
            },
            onFavoriClicked = { yemekItemState ->
                viewModel.toggleFavoriDurumu(yemekItemState.yemek.yemekId, yemekItemState.isFavori)
            }
        )
        binding.recyclerViewYemekler.apply {
            // GridLayoutManager XML'de app:layoutManager ve app:spanCount ile tanımlı
            // XML'de tanımlı değilse: layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = yemeklerAdapter
            isNestedScrollingEnabled = false
            // Eğer fragment_anasayfa.xml'in en dışı NestedScrollView ise bu satır gerekli:
            // isNestedScrollingEnabled = false
            // Eğer en dışı ConstraintLayout ise bu satır gereksiz.
            // Mevcut fragment_anasayfa.xml NestedScrollView kullandığı için bu satırı
            // XML'de recyclerViewYemekler'e android:nestedScrollingEnabled="false" olarak eklemek daha iyi.
        }
    }

    private fun observeViewModel() {
        Log.d("AppLifecycle", "AnasayfaFragment observeViewModel BAŞLADI.")
        // ViewModel'daki yemekListesiUiState (filtrelenmiş veya tüm liste) observe ediliyor
        viewModel.yemekListesiUiState.observe(viewLifecycleOwner) { yemekUiStateList ->
            Log.d("AnasayfaViewModel", "Yemek UI State Listesi güncellendi: ${yemekUiStateList.size} adet")
            val seciliKategori = viewModel.seciliKategoriAdi.value
            val gosterilecekListeBosMu = yemekUiStateList.isEmpty()

            if (!gosterilecekListeBosMu) {
                yemeklerAdapter.submitList(yemekUiStateList)
                binding.recyclerViewYemekler.visibility = View.VISIBLE
                binding.textViewYemeklerBaslik.visibility = View.VISIBLE
                binding.textViewAnasayfaHata.visibility = View.GONE
            } else if (viewModel.errorMessage.value == null && viewModel.isLoading.value == false) {
                binding.recyclerViewYemekler.visibility = View.GONE
                binding.textViewYemeklerBaslik.visibility = View.GONE
                val hataMesaji = if (seciliKategori != null && !seciliKategori.equals("Tümü", ignoreCase = true)) {
                    "'$seciliKategori' kategorisinde yemek bulunamadı."
                } else {
                    "Gösterilecek yemek bulunamadı."
                }
                binding.textViewAnasayfaHata.text = hataMesaji
                binding.textViewAnasayfaHata.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("AnasayfaViewModel", "isLoading durumu: $isLoading")
            binding.progressBarAnasayfa.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.textViewAnasayfaHata.visibility = View.GONE
                binding.textViewYemeklerBaslik.visibility = View.GONE // Yüklenirken başlığı da gizleyebiliriz
            } else {
                // Yükleme bittiğinde ve liste varsa başlığı göster
                if (viewModel.yemekListesiUiState.value?.isNotEmpty() == true) {
                    binding.textViewYemeklerBaslik.visibility = View.VISIBLE
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("AnasayfaViewModel", "Hata mesajı alındı: $it")
                binding.recyclerViewYemekler.visibility = View.GONE
                binding.textViewYemeklerBaslik.visibility = View.GONE
                binding.textViewAnasayfaHata.text = it
                binding.textViewAnasayfaHata.visibility = View.VISIBLE
            } ?: run {
                if (viewModel.yemekListesiUiState.value?.isNotEmpty() == true) {
                    binding.textViewAnasayfaHata.visibility = View.GONE
                }
            }
        }
        Log.d("AppLifecycle", "AnasayfaFragment observeViewModel TAMAMLANDI.")
    }

    private fun setupSearchView() {
        binding.searchViewYemekler.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewYemekler.clearFocus()
                viewModel.kategoriSecildi(null) // Arama yapıldığında kategori filtresini sıfırla
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim() ?: ""
                // ViewModel'daki yemekListesiUiState (zaten kategoriye göre filtrelenmiş olabilir)
                // üzerinden arama yap.
                val listeFiltrelenecek = viewModel.yemekListesiUiState.value ?: emptyList()

                if (query.isEmpty()) {
                    // Arama boşsa, ViewModel'dan gelen (kategoriye göre filtrelenmiş olabilecek) listeyi göster
                    // Bu durumda adapter'a tekrar aynı listeyi göndermek yerine,
                    // ViewModel'daki kategoriSecildi(null) ile tüm listenin gelmesini sağlayabiliriz
                    // veya mevcut filtrelenmiş listeyi (kategoriye göre) göstermeye devam edebiliriz.
                    // Şimdilik mevcut filtrelenmiş listeyi gösteriyoruz.
                    // Eğer arama kutusu boşaltıldığında tüm yemeklerin (kategorisiz) gelmesi isteniyorsa,
                    // viewModel.kategoriSecildi(null) çağrılabilir.
                    yemeklerAdapter.submitList(listeFiltrelenecek)
                    binding.textViewAnasayfaHata.visibility = if (listeFiltrelenecek.isEmpty() && viewModel.isLoading.value == false) View.VISIBLE else View.GONE
                    if (listeFiltrelenecek.isEmpty() && viewModel.isLoading.value == false) {
                        val seciliKategori = viewModel.seciliKategoriAdi.value
                        binding.textViewAnasayfaHata.text = if (seciliKategori != null && !seciliKategori.equals("Tümü", ignoreCase = true)) "'$seciliKategori' kategorisinde yemek bulunamadı." else "Gösterilecek yemek bulunamadı."
                    }
                } else {
                    val aramaSonucuListe = listeFiltrelenecek.filter { yemekItemState ->
                        yemekItemState.yemek.yemekAdi.contains(query, ignoreCase = true)
                    }
                    yemeklerAdapter.submitList(aramaSonucuListe)

                    if (aramaSonucuListe.isEmpty()) {
                        binding.textViewAnasayfaHata.text = "'$query' için sonuç bulunamadı."
                        binding.textViewAnasayfaHata.visibility = View.VISIBLE
                    } else {
                        binding.textViewAnasayfaHata.visibility = View.GONE
                    }
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("AppLifecycle", "AnasayfaFragment onResume çağrıldı.")
        // İsteğe bağlı: Sayfa her göründüğünde verileri veya seçili kategoriyi güncelle
        // viewModel.yemekleriYukle() // Zaten init'te yükleniyor
        // viewModel.kategoriSecildi(viewModel.seciliKategoriAdi.value) // Mevcut filtreyi koru veya sıfırla
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("AppLifecycle", "AnasayfaFragment onDestroyView çağrıldı.")
        _binding = null
    }
}