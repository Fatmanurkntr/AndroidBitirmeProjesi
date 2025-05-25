package com.example.yemeksiparisuygulamasi // Kendi paket adınla değiştir

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yemeksiparisuygulamasi.R // R importu eklendi (gerekliyse)
import com.example.yemeksiparisuygulamasi.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration // AppBar yapılandırması için

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // activity_main.xml'e eklediğimiz Toolbar'ı ActionBar olarak ayarla
        setSupportActionBar(binding.toolbar) // binding.toolbar ID'sinin XML'de doğru olduğundan emin ol
        supportActionBar?.setDisplayShowTitleEnabled(false) // BU SATIRI EKLEYİN


        // NavHostFragment'ı bul ve NavController'ı al
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // AppBarConfiguration'ı oluştur. Top-level destination'ları (geri oku olmayanları) belirt.
        // BottomNavigationView'daki her bir fragment ID'sini buraya ekle.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.anasayfaFragment, R.id.favorilerFragment, R.id.sepetFragment
                // Eğer başka top-level fragment'ların varsa buraya ekle
            )
        )

        // Toolbar'ı (ActionBar) NavController ve AppBarConfiguration ile bağla
        // Bu, başlıkları ve geri tuşunu otomatik olarak yönetir.
        setupActionBarWithNavController(navController, appBarConfiguration)

        // BottomNavigationView'ı NavController ile bağla
        binding.bottomNavigationView.setupWithNavController(navController)
        // Yukarıdaki satır NavigationUI.setupWithNavController ile aynı işi yapar,
        // ama extension fonksiyon olarak daha modern bir kullanımdır. İkisinden birini kullanabilirsiniz.
        // NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    // Toolbar'daki geri tuşu ve yukarı navigasyon (up navigation) için bu metodu override et
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}