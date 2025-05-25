package com.example.yemeksiparisuygulamasi.ui.fragment

import android.os.Bundle
import android.util.Log // Loglama için
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.adapter.SepetAdapter
import com.example.yemeksiparisuygulamasi.databinding.FragmentSepetBinding
import com.example.yemeksiparisuygulamasi.util.Constants
import com.example.yemeksiparisuygulamasi.viewmodel.SepetViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class SepetFragment : Fragment() {

    private var _binding: FragmentSepetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SepetViewModel by viewModels()
    private lateinit var sepetAdapter: SepetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSepetBinding.inflate(inflater, container, false)
        Log.d("SepetFragment", "onCreateView çağrıldı.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SepetFragment", "onViewCreated çağrıldı.")

        setupRecyclerView()
        observeViewModel()
        // viewModel.sepetiYukle() // onResume içinde çağrılıyor

        binding.buttonSepetiOnayla.setOnClickListener {
            Log.d("SepetFragment", "Sepeti Onayla butonuna tıklandı.")
            if (viewModel.sepetListesi.value.isNullOrEmpty()){
                Snackbar.make(binding.root, "Sepetiniz boş!", Snackbar.LENGTH_SHORT).show()
            } else {
                // Siparişi ViewModel üzerinden kaydet
                viewModel.sepetiOnaylaVeKaydet()
            }
        }
    }

    private fun setupRecyclerView() {
        sepetAdapter = SepetAdapter(
            sepetListesi = emptyList(),
            onSepettenSilClicked = { sepetYemek, position ->
                viewModel.sepettenYemekSil(sepetYemek, position)
            }
        )
        binding.recyclerViewSepet.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sepetAdapter
        }
    }

    private fun observeViewModel() {
        Log.d("SepetFragment", "observeViewModel çağrıldı.")
        viewModel.sepetListesi.observe(viewLifecycleOwner) { sepet ->
            Log.d("SepetFragment", "Sepet listesi güncellendi, ürün sayısı: ${sepet?.size ?: 0}")
            val sepetBos = sepet.isNullOrEmpty()
            val yukleniyor = viewModel.isLoading.value ?: false
            val hataVar = viewModel.errorMessage.value != null

            if (sepetBos && !yukleniyor && !hataVar) {
                binding.textViewSepetBos.text = "Sepetiniz henüz boş." // Varsayılan mesaj
                binding.textViewSepetBos.visibility = View.VISIBLE
                binding.recyclerViewSepet.visibility = View.GONE
                binding.cardViewOzets.visibility = View.GONE
                binding.textViewBedavaGonderimMesaji.visibility = View.GONE
            } else if (!sepetBos) {
                binding.textViewSepetBos.visibility = View.GONE
                binding.recyclerViewSepet.visibility = View.VISIBLE
                binding.cardViewOzets.visibility = View.VISIBLE
            } else if (hataVar && !yukleniyor) {
                binding.textViewSepetBos.text = viewModel.errorMessage.value // Hata mesajını göster
                binding.textViewSepetBos.visibility = View.VISIBLE
                binding.recyclerViewSepet.visibility = View.GONE
                binding.cardViewOzets.visibility = View.GONE
                binding.textViewBedavaGonderimMesaji.visibility = View.GONE
            }
            sepetAdapter.submitList(sepet ?: emptyList())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("SepetFragment", "isLoading durumu: $isLoading")
            binding.progressBarSepet.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.textViewSepetBos.visibility = View.GONE
                binding.textViewBedavaGonderimMesaji.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("SepetFragment", "Hata mesajı: $it")
                // Hata mesajı zaten sepetListesi observer'ında textViewSepetBos'a yazılıyor.
                // İsterseniz burada ek bir Snackbar da gösterebilirsiniz.
                // Snackbar.make(binding.root, "Hata: $it", Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.toastMesaji.observe(viewLifecycleOwner) { mesaj ->
            mesaj?.let {
                Log.d("SepetFragment", "Toast/Snackbar mesajı: $it")
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.toastMesajiGosterildi()
            }
        }

        val localeTR = Locale("tr", "TR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeTR)

        viewModel.yemeklerAraToplam.observe(viewLifecycleOwner) { araToplam ->
            Log.d("SepetFragment", "Ara toplam güncellendi: ${currencyFormat.format(araToplam)}")
            // Eğer fragment_sepet.xml'de textViewAraToplam ID'li bir TextView varsa:
            // binding.textViewAraToplam.text = currencyFormat.format(araToplam)
            // binding.layoutAraToplam.visibility = if (araToplam > 0 && viewModel.sepetListesi.value?.isNotEmpty() == true) View.VISIBLE else View.GONE
        }

        viewModel.gonderimUcreti.observe(viewLifecycleOwner) { ucret ->
            Log.d("SepetFragment", "Gönderim ücreti güncellendi: ${currencyFormat.format(ucret)}")
            binding.textViewGonderimUcreti.text = currencyFormat.format(ucret)
        }

        viewModel.genelToplam.observe(viewLifecycleOwner) { toplam ->
            Log.d("SepetFragment", "Genel toplam güncellendi: ${currencyFormat.format(toplam)}")
            binding.textViewToplamTutar.text = currencyFormat.format(toplam)
            binding.buttonSepetiOnayla.isEnabled = toplam > 0.0 && (viewModel.sepetListesi.value?.isNotEmpty() == true)
        }

        viewModel.bedavaGonderimeKalan.observe(viewLifecycleOwner) { kalanTutar ->
            val araToplam = viewModel.yemeklerAraToplam.value ?: 0.0
            Log.d("SepetFragment", "Bedava gönderime kalan güncellendi: ${currencyFormat.format(kalanTutar)}, Ara Toplam: ${currencyFormat.format(araToplam)}")

            if (kalanTutar > 0 && araToplam > 0 && araToplam < Constants.BEDAVA_GONDERIM_LIMITI) {
                binding.textViewBedavaGonderimMesaji.text =
                    "${currencyFormat.format(kalanTutar)} daha eklerseniz gönderim ücreti bedava!"
                binding.textViewBedavaGonderimMesaji.visibility = View.VISIBLE
            } else {
                binding.textViewBedavaGonderimMesaji.visibility = View.GONE
            }
        }

        // YENİ OBSERVER: Sipariş verildi event'ini dinle
        viewModel.siparisVerildiEvent.observe(viewLifecycleOwner) { verildiMi ->
            verildiMi?.let {
                if (it) { // Sipariş başarıyla verildiyse
                    Log.d("SepetFragment", "Sipariş verildi event'i alındı, anasayfaya yönlendiriliyor.")
                    // Kullanıcıyı anasayfaya yönlendir ve sepet ekranını backstack'ten temizle
                    // Bu, kullanıcı geri geldiğinde boş bir sepete dönmesini engeller.
                    findNavController().popBackStack(R.id.anasayfaFragment, false)
                    // Veya sadece bir önceki ekrana dönmek için:
                    // findNavController().popBackStack()
                } else {
                    // Sipariş verilirken bir hata oluştu (errorMessage zaten gösterilmiş olmalı)
                    Log.e("SepetFragment", "Sipariş verme işlemi başarısız oldu event'i alındı.")
                }
                viewModel.siparisVerildiEventiKullanildi() // Event'i kullanıldı olarak işaretle
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("SepetFragment", "onResume çağrıldı, sepet yükleniyor.")
        viewModel.sepetiYukle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SepetFragment", "onDestroyView çağrıldı.")
        _binding = null
    }
}