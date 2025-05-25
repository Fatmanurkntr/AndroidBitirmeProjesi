package com.example.yemeksiparisuygulamasi.adapter // Kendi paket adınla değiştir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.data.model.Yemek
import com.example.yemeksiparisuygulamasi.data.remote.ApiService
import com.example.yemeksiparisuygulamasi.databinding.YemekItemLayoutBinding
import com.example.yemeksiparisuygulamasi.viewmodel.YemekItemUiState

class YemeklerAdapter(
    private var yemekUiStateListesi: List<YemekItemUiState>,
    private val onItemClicked: (Yemek) -> Unit,
    private val onSepeteEkleClicked: (Yemek) -> Unit,
    private val onFavoriClicked: (YemekItemUiState) -> Unit
) : RecyclerView.Adapter<YemeklerAdapter.YemekViewHolder>() {

    inner class YemekViewHolder(val binding: YemekItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(yemekItemState: YemekItemUiState) {
            val yemek = yemekItemState.yemek
            binding.textViewYemekAdi.text = yemek.yemekAdi
            binding.textViewYemekFiyat.text = "₺${yemek.yemekFiyat}"

            val resimUrl = "${ApiService.BASE_URL_RESIMLER}${yemek.yemekResimAdi}"
            Glide.with(binding.root.context)
                .load(resimUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.imageViewYemekResim)

            if (yemekItemState.isFavori) {
                binding.imageViewFavori.setImageResource(R.drawable.ic_favorite_filled)
                binding.imageViewFavori.imageTintList = binding.root.context.getColorStateList(R.color.favori_ikon_dolu_rengi)
            } else {
                binding.imageViewFavori.setImageResource(R.drawable.ic_favorite_border)
                binding.imageViewFavori.imageTintList = binding.root.context.getColorStateList(R.color.favori_ikon_bos_rengi)
            }

            binding.root.setOnClickListener {
                onItemClicked(yemek)
            }
            binding.imageViewSepeteEkle.setOnClickListener {
                onSepeteEkleClicked(yemek)
            }
            binding.imageViewFavori.setOnClickListener {
                onFavoriClicked(yemekItemState)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekViewHolder {
        val binding = YemekItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YemekViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YemekViewHolder, position: Int) {
        holder.bind(yemekUiStateListesi[position])
    }

    override fun getItemCount(): Int {
        return yemekUiStateListesi.size
    }

    fun submitList(yeniListe: List<YemekItemUiState>) {
        yemekUiStateListesi = yeniListe
        notifyDataSetChanged()
    }
}