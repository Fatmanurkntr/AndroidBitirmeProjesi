package com.example.yemeksiparisuygulamasi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemeksiparisuygulamasi.adapter.YemeklerAdapter // Aynı adapter'ı kullanabiliriz
import com.example.yemeksiparisuygulamasi.databinding.FragmentFavorilerBinding
import com.example.yemeksiparisuygulamasi.viewmodel.DetayViewModel
import com.example.yemeksiparisuygulamasi.viewmodel.FavorilerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavorilerFragment : Fragment() {

    private var _binding: FragmentFavorilerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavorilerViewModel by viewModels()
    private val detayViewModel: DetayViewModel by viewModels() // Sepete ekleme için

    private lateinit var favorilerAdapter: YemeklerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavorilerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        detayViewModel.toastMesaji.observe(viewLifecycleOwner) { mesaj ->
            mesaj?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                detayViewModel.toastMesajiGosterildi()
            }
        }
    }

    private fun setupRecyclerView() {
        favorilerAdapter = YemeklerAdapter(
            yemekUiStateListesi = emptyList(),
            onItemClicked = { yemek ->
                // Favorilerden detaya gitmek için action tanımlanabilir veya
                // Anasayfa'daki action kullanılabilir (argüman aynı olduğu için)
                val action = FavorilerFragmentDirections.actionFavorilerFragmentToDetayFragment(yemek)
                findNavController().navigate(action)
            },
            onSepeteEkleClicked = { yemek ->
                detayViewModel.sepeteYemekEkle(yemek, 1)
            },
            onFavoriClicked = { yemekItemState ->
                // Favoriler sayfasından favoriden çıkar
                viewModel.favoridenCikar(yemekItemState.yemek.yemekId)
                // Toast mesajı ViewModel'dan gelebilir veya burada gösterilebilir
                Toast.makeText(requireContext(), "${yemekItemState.yemek.yemekAdi} favorilerden çıkarıldı.", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewFavoriler.apply {
            layoutManager = LinearLayoutManager(requireContext()) // XML'de tanımlı
            adapter = favorilerAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.favoriYemekDetaylari.observe(viewLifecycleOwner) { favoriYemekler ->
            if (favoriYemekler.isNotEmpty()) {
                favorilerAdapter.submitList(favoriYemekler)
                binding.recyclerViewFavoriler.visibility = View.VISIBLE
                binding.textViewFavoriYok.visibility = View.GONE
            } else {
                binding.recyclerViewFavoriler.visibility = View.GONE
                binding.textViewFavoriYok.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarFavoriler.visibility = if (isLoading) View.VISIBLE else View.GONE
            if(isLoading) binding.textViewFavoriYok.visibility = View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.textViewFavoriYok.text = it
                binding.textViewFavoriYok.visibility = View.VISIBLE
                binding.recyclerViewFavoriler.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}