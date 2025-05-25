package com.example.yemeksiparisuygulamasi.adapter // Kendi paket adınla değiştir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yemeksiparisuygulamasi.data.model.SepetYemek
import com.example.yemeksiparisuygulamasi.data.remote.ApiService
import com.example.yemeksiparisuygulamasi.databinding.SepetItemLayoutBinding

class SepetAdapter(
    private var sepetListesi: List<SepetYemek>,
    private val onSepettenSilClicked: (SepetYemek, Int) -> Unit // Silinecek yemek ve pozisyonu
) : RecyclerView.Adapter<SepetAdapter.SepetViewHolder>() {

    inner class SepetViewHolder(val binding: SepetItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sepetYemek: SepetYemek, position: Int) {
            binding.textViewSepetYemekAdi.text = sepetYemek.yemekAdi
            binding.textViewSepetYemekFiyat.text = "Fiyat: ₺${sepetYemek.yemekFiyat}"
            binding.textViewSepetYemekAdet.text = "Adet: ${sepetYemek.yemekSiparisAdet}"

            val fiyat = sepetYemek.yemekFiyat.toDoubleOrNull() ?: 0.0
            val adet = sepetYemek.yemekSiparisAdet.toIntOrNull() ?: 0
            binding.textViewSepetItemToplamFiyat.text = "₺${String.format("%.0f", fiyat * adet)}"


            val resimUrl = "${ApiService.BASE_URL_RESIMLER}${sepetYemek.yemekResimAdi}"
            Glide.with(binding.root.context)
                .load(resimUrl)
                .into(binding.imageViewSepetYemekResim)

            binding.imageViewSepettenSil.setOnClickListener {
                onSepettenSilClicked(sepetYemek, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SepetViewHolder {
        val binding = SepetItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SepetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SepetViewHolder, position: Int) {
        holder.bind(sepetListesi[position], position)
    }

    override fun getItemCount(): Int {
        return sepetListesi.size
    }

    fun submitList(yeniListe: List<SepetYemek>) {
        sepetListesi = yeniListe
        notifyDataSetChanged() // DiffUtil ile daha iyi performans
    }
}