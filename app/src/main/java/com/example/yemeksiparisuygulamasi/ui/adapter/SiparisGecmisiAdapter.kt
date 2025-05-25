package com.example.yemeksiparisuygulamasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yemeksiparisuygulamasi.data.local.SiparisVeUrunleri
import com.example.yemeksiparisuygulamasi.databinding.SiparisGecmisiItemLayoutBinding
import java.text.NumberFormat
import java.util.Locale

class SiparisGecmisiAdapter(
    private var siparisListesi: List<SiparisVeUrunleri>,
    private val onItemClicked: (SiparisVeUrunleri) -> Unit // Sipariş detayını görmek için
) : RecyclerView.Adapter<SiparisGecmisiAdapter.SiparisViewHolder>() {

    inner class SiparisViewHolder(val binding: SiparisGecmisiItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(siparisDetay: SiparisVeUrunleri) {
            binding.textViewSiparisTarihi.text = siparisDetay.siparis.getFormattedSiparisTarihi()

            val urunAdlari = siparisDetay.urunler.joinToString(separator = ", ") { "${it.yemekAdi} (${it.yemekSiparisAdet} Adet)" }
            binding.textViewSiparisUrunleriOzeti.text = urunAdlari

            val localeTR = Locale("tr", "TR")
            val currencyFormat = NumberFormat.getCurrencyInstance(localeTR)
            binding.textViewSiparisToplamTutar.text = "Toplam: ${currencyFormat.format(siparisDetay.siparis.toplamTutar)}"

            binding.root.setOnClickListener {
                onItemClicked(siparisDetay)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiparisViewHolder {
        val binding = SiparisGecmisiItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SiparisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiparisViewHolder, position: Int) {
        holder.bind(siparisListesi[position])
    }

    override fun getItemCount(): Int = siparisListesi.size

    fun submitList(yeniListe: List<SiparisVeUrunleri>) {
        siparisListesi = yeniListe
        notifyDataSetChanged() // DiffUtil daha iyi olur
    }
}