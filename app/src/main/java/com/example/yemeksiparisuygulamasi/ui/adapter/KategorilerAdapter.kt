package com.example.yemeksiparisuygulamasi.adapter

import Kategori
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yemeksiparisuygulamasi.databinding.KategoriItemLayoutBinding

class KategorilerAdapter(
    private val kategoriListesi: List<Kategori>,
    private val onKategoriClicked: (Kategori) -> Unit
) : RecyclerView.Adapter<KategorilerAdapter.KategoriViewHolder>() {

    inner class KategoriViewHolder(val binding: KategoriItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(kategori: Kategori) {
            binding.textViewKategoriAdi.text = kategori.ad
            binding.imageViewKategoriIkon.setImageResource(kategori.ikonResId)
            binding.root.setOnClickListener {
                onKategoriClicked(kategori)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val binding =
            KategoriItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KategoriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        holder.bind(kategoriListesi[position])
    }

    override fun getItemCount(): Int = kategoriListesi.size
}