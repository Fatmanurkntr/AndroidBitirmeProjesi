package com.example.yemeksiparisuygulamasi.ui.fragment

import android.os.Bundle
import android.util.Log // Loglama için
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemeksiparisuygulamasi.adapter.SiparisGecmisiAdapter
import com.example.yemeksiparisuygulamasi.databinding.FragmentProfilBinding
import com.example.yemeksiparisuygulamasi.viewmodel.ProfilViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar // Snackbar için
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat // Sipariş detayı için gerekebilir
import java.util.Locale     // Sipariş detayı için gerekebilir

@AndroidEntryPoint
class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfilViewModel by viewModels()
    private lateinit var siparisGecmisiAdapter: SiparisGecmisiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        Log.d("ProfilFragment", "onCreateView çağrıldı.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ProfilFragment", "onViewCreated çağrıldı.")

        setupRecyclerViewSiparisGecmisi()
        setupAdresIslemleri() // Adres butonlarını ve mantığını ayarla
        observeViewModel()

        binding.buttonSiparisGecmisiniTemizle.setOnClickListener {
            Log.d("ProfilFragment", "Sipariş Geçmişini Temizle butonuna tıklandı.")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sipariş Geçmişini Temizle")
                .setMessage("Tüm sipariş geçmişinizi silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.")
                .setNegativeButton("İptal", null)
                .setPositiveButton("Sil") { _, _ ->
                    viewModel.gecmisiTemizle()
                    Snackbar.make(binding.root, "Sipariş geçmişiniz temizlendi.", Snackbar.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun setupRecyclerViewSiparisGecmisi() {
        siparisGecmisiAdapter = SiparisGecmisiAdapter(emptyList()) { secilenSiparis ->
            Log.d("ProfilFragment", "Sipariş item'ına tıklandı: ID ${secilenSiparis.siparis.siparisId}")
            val urunlerStr = secilenSiparis.urunler.joinToString("\n") {
                val localeTR = Locale("tr", "TR")
                val currencyFormat = NumberFormat.getCurrencyInstance(localeTR)
                "- ${it.yemekAdi} (${it.yemekSiparisAdet} x ${currencyFormat.format(it.yemekFiyat)})"
            }
            val localeTR = Locale("tr", "TR")
            val currencyFormat = NumberFormat.getCurrencyInstance(localeTR)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sipariş Detayı (${secilenSiparis.siparis.getFormattedSiparisTarihi()})")
                .setMessage("Ürünler:\n$urunlerStr\n\nToplam: ${currencyFormat.format(secilenSiparis.siparis.toplamTutar)}")
                .setPositiveButton("Tamam", null)
                .show()
        }
        binding.recyclerViewSiparisGecmisi.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = siparisGecmisiAdapter
            // NestedScrollView içinde olduğu için bu ayar iyi olabilir
            // XML'de zaten android:nestedScrollingEnabled="false" olarak ayarlamıştık
            // isNestedScrollingEnabled = false
        }
    }

    private fun setupAdresIslemleri() {
        Log.d("ProfilFragment", "setupAdresIslemleri çağrıldı.")
        binding.buttonAdresDuzenleKaydet.setOnClickListener {
            if (viewModel.adresDuzenlemeModunda.value == true) {
                Log.d("ProfilFragment", "Adres Kaydet butonuna tıklandı.")
                // Kaydetme işlemi
                val adres1 = binding.editTextAdresSatiri1.text.toString().trim()
                val ilce = binding.editTextIlce.text.toString().trim()
                val il = binding.editTextIl.text.toString().trim()

                if (adres1.isEmpty() || ilce.isEmpty() || il.isEmpty()) {
                    Snackbar.make(binding.root, "Adres Satırı 1, İlçe ve İl alanları boş bırakılamaz.", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                viewModel.adresiKaydet(
                    adres1,
                    binding.editTextAdresSatiri2.text.toString().trim(),
                    ilce,
                    il
                )
                Snackbar.make(binding.root, "Adres başarıyla kaydedildi.", Snackbar.LENGTH_SHORT).show()
            } else {
                Log.d("ProfilFragment", "Adres Düzenle/Ekle butonuna tıklandı.")
                // Düzenleme moduna geç
                viewModel.adresDuzenleModunuToggle()
            }
        }
    }


    private fun observeViewModel() {
        Log.d("ProfilFragment", "observeViewModel çağrıldı.")
        viewModel.siparisGecmisi.observe(viewLifecycleOwner) { siparisler ->
            Log.d("ProfilFragment", "Sipariş geçmişi güncellendi, sayı: ${siparisler?.size ?: 0}")
            val siparisGecmisiBos = siparisler.isNullOrEmpty()
            binding.textViewSiparisGecmisiBos.visibility = if (siparisGecmisiBos) View.VISIBLE else View.GONE
            binding.recyclerViewSiparisGecmisi.visibility = if (siparisGecmisiBos) View.GONE else View.VISIBLE
            binding.buttonSiparisGecmisiniTemizle.visibility = if (siparisGecmisiBos) View.GONE else View.VISIBLE
            siparisGecmisiAdapter.submitList(siparisler ?: emptyList())
        }

        viewModel.kayitliAdres.observe(viewLifecycleOwner) { adresMetni ->
            Log.d("ProfilFragment", "Kayıtlı adres güncellendi: $adresMetni")
            if (viewModel.adresDuzenlemeModunda.value == false) {
                if (adresMetni.isNullOrEmpty()) {
                    binding.textViewKayitliAdres.text = "Kayıtlı adresiniz bulunmuyor."
                } else {
                    binding.textViewKayitliAdres.text = adresMetni
                }
            }
        }

        viewModel.adresDuzenlemeModunda.observe(viewLifecycleOwner) { duzenlemedeMi ->
            Log.d("ProfilFragment", "Adres düzenleme modu: $duzenlemedeMi")
            if (duzenlemedeMi) {
                binding.textViewKayitliAdres.visibility = View.GONE
                binding.tilAdresSatiri1.visibility = View.VISIBLE
                binding.tilAdresSatiri2.visibility = View.VISIBLE
                binding.layoutIlIlce.visibility = View.VISIBLE
                binding.buttonAdresDuzenleKaydet.text = "Kaydet"

                // Düzenleme moduna geçerken EditText'leri doldur
                binding.editTextAdresSatiri1.setText(viewModel.adresSatiri1.value)
                binding.editTextAdresSatiri2.setText(viewModel.adresSatiri2.value)
                binding.editTextIlce.setText(viewModel.ilce.value)
                binding.editTextIl.setText(viewModel.il.value)
                binding.editTextAdresSatiri1.requestFocus() // İlk alana odaklan
            } else {
                binding.textViewKayitliAdres.visibility = View.VISIBLE
                binding.tilAdresSatiri1.visibility = View.GONE
                binding.tilAdresSatiri2.visibility = View.GONE
                binding.layoutIlIlce.visibility = View.GONE
                binding.buttonAdresDuzenleKaydet.text = if (viewModel.kayitliAdres.value.isNullOrEmpty()) "Adres Ekle" else "Düzenle"
            }
        }

        // EditText'lerin başlangıç değerlerini ViewModel'daki LiveData'lardan almak için
        // Bu observer'lar, ViewModel'daki değerler SharedPreferences'tan ilk yüklendiğinde
        // ve adresDuzenleModunuToggle içinde güncellendiğinde EditText'leri doldurur.
        viewModel.adresSatiri1.observe(viewLifecycleOwner) { binding.editTextAdresSatiri1.setText(it) }
        viewModel.adresSatiri2.observe(viewLifecycleOwner) { binding.editTextAdresSatiri2.setText(it) }
        viewModel.ilce.observe(viewLifecycleOwner) { binding.editTextIlce.setText(it) }
        viewModel.il.observe(viewLifecycleOwner) { binding.editTextIl.setText(it) }

        // ProgressBar için isLoading (Eğer ProfilViewModel'da varsa)
        // viewModel.isLoading.observe(viewLifecycleOwner) { binding.progressBarProfil.visibility = if (it) View.VISIBLE else View.GONE }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfilFragment", "onResume çağrıldı.")
        // Adres ve sipariş geçmişi gibi veriler SharedPreferences ve Room'dan
        // ViewModel init'te veya LiveData kaynakları aracılığıyla yüklendiği için
        // burada tekrar yüklemeye gerek olmayabilir, ancak emin olmak için eklenebilir.
        // viewModel.yukleKayitliAdres() // ViewModel init'te çağırıyor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ProfilFragment", "onDestroyView çağrıldı.")
        _binding = null
    }
}