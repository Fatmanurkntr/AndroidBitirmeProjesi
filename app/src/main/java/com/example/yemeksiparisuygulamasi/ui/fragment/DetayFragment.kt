package com.example.yemeksiparisuygulamasi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// import android.widget.Toast // Toast artık kullanılmayacak
import androidx.core.content.ContextCompat // Renk almak için
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.data.model.Yemek
import com.example.yemeksiparisuygulamasi.data.remote.ApiService
import com.example.yemeksiparisuygulamasi.databinding.FragmentDetayBinding
import com.example.yemeksiparisuygulamasi.viewmodel.DetayViewModel
import com.google.android.material.snackbar.Snackbar // Snackbar import edildi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetayFragment : Fragment() {

    private var _binding: FragmentDetayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetayViewModel by viewModels()
    private val args: DetayFragmentArgs by navArgs()

    private var secilenAdet = 1
    private lateinit var secilenYemek: Yemek

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secilenYemek = args.yemek
        viewModel.setSecilenYemek(secilenYemek) // ViewModel'a seçilen yemeği bildir

        uiGuncelle(secilenYemek)
        adetButonlariniAyarla()
        sepeteEkleButonunuAyarla()
        favoriButonunuAyarla() // Favori butonu için yeni fonksiyon
        observeViewModel()
    }

    private fun uiGuncelle(yemek: Yemek) {
        binding.textViewDetayYemekAdi.text = yemek.yemekAdi
        binding.textViewDetayYemekFiyat.text = "₺${yemek.yemekFiyat}"
        binding.textViewAdet.text = secilenAdet.toString()

        val resimUrl = "${ApiService.BASE_URL_RESIMLER}${yemek.yemekResimAdi}"
        Glide.with(this)
            .load(resimUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.imageViewDetayYemekResim)

        // Favori ikonunu da burada başlangıçta ayarlayabiliriz (observeViewModel'da da güncellenecek)
        // VEYA direkt observeViewModel'da ilk değeri almasını bekleyebiliriz.
        // Şimdilik observeViewModel'a bırakalım.
    }

    private fun adetButonlariniAyarla() {
        binding.buttonAdetArtir.setOnClickListener {
            secilenAdet++
            binding.textViewAdet.text = secilenAdet.toString()
        }

        binding.buttonAdetAzalt.setOnClickListener {
            if (secilenAdet > 1) {
                secilenAdet--
                binding.textViewAdet.text = secilenAdet.toString()
            }
        }
    }

    private fun sepeteEkleButonunuAyarla() {
        binding.buttonDetaySepeteEkle.setOnClickListener {
            viewModel.sepeteYemekEkle(secilenYemek, secilenAdet)
        }
    }

    // YENİ FONKSİYON: Favori ikonunu ve tıklama olayını ayarlar
    private fun favoriButonunuAyarla() {
        // fragment_detay.xml dosyanızda imageViewDetayFavori ID'li bir ImageView olmalı
        binding.imageViewDetayFavori.setOnClickListener {
            viewModel.toggleFavoriDurumu()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarDetay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonDetaySepeteEkle.isEnabled = !isLoading
            binding.buttonAdetArtir.isEnabled = !isLoading
            binding.buttonAdetAzalt.isEnabled = !isLoading
            binding.imageViewDetayFavori.isEnabled = !isLoading // Favori butonu da etkilensin
        }

        viewModel.toastMesaji.observe(viewLifecycleOwner) { mesaj ->
            mesaj?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() // Snackbar uzun gösterilsin
                viewModel.toastMesajiGosterildi()

                // Sadece sepete ekleme başarılıysa geri dön
                if (it.contains("sepete eklendi", ignoreCase = true) && viewModel.sepeteEklemeSonucu.value?.success == 1) {
                    findNavController().popBackStack()
                }
            }
        }

        viewModel.sepeteEklemeSonucu.observe(viewLifecycleOwner) { cevap ->
            // Bu observer, toastMesaji observer'ı ile birleştirilebilir veya
            // sadece sepeteEklemeSonucu'nun success durumuna göre işlem yapılabilir.
            // Yukarıdaki toastMesaji observer'ı zaten bu durumu kontrol ediyor.
        }

        // YENİ OBSERVER: Yemeğin favori durumunu dinler ve ikonu günceller
        viewModel.isFavori.observe(viewLifecycleOwner) { isFavori ->
            if (isFavori) {
                binding.imageViewDetayFavori.setImageResource(R.drawable.ic_favorite_filled)
                binding.imageViewDetayFavori.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.favori_ikon_dolu_rengi)
            } else {
                binding.imageViewDetayFavori.setImageResource(R.drawable.ic_favorite_border)
                binding.imageViewDetayFavori.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.favori_ikon_bos_rengi)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}