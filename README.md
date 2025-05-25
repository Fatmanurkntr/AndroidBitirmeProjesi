# Yummo! - Yemek Sipariş Uygulaması (Bootcamp Bitirme Projesi)

Bu proje, [Bootcamp Adınız/Kurum Adınız] kapsamında geliştirilmiş bir Android mobil yemek sipariş uygulamasıdır. Kullanıcıların yemekleri listeleyebileceği, detaylarını görebileceği, sepetine ekleyebileceği ve sipariş verebileceği (simülasyon) bir platform sunmayı amaçlamaktadır.

<!-- BURAYA UYGULAMANIN GENEL BİR EKRAN GÖRÜNTÜSÜNÜ KOYABİLİRSİNİZ (Örn: Anasayfa) -->
<!-- ![Uygulama Anasayfa](link_veya_path/anasayfa_ekran_goruntusu.png) -->

## Proje Künyesi

*   **Geliştirici:** Fatmanur Kantar
*   **Bootcamp:** [Bootcamp Adınız/Kurum Adınız]
*   **Eğitmen:** Kasım Adalan
*   **Proje Teslim Tarihi:** 30 Mayıs 2025
*   **GitHub Repository:** [https://github.com/Fatmanurkntr/AndroidBitirmeProjesi.git](https://github.com/Fatmanurkntr/AndroidBitirmeProjesi.git)

## Uygulamanın Amacı ve Kapsamı

Bu proje, bir mobil yemek sipariş uygulamasının temel işlevlerini ve modern Android geliştirme pratiklerini uygulamayı hedeflemiştir. Kullanıcılar, sanal bir restorandan yemek seçimi yapabilir, sepetlerini yönetebilir ve sipariş sürecini deneyimleyebilirler.

## Kullanılan Teknolojiler ve Kütüphaneler

*   **Programlama Dili:** Kotlin
*   **Mimari Desen:** MVVM (Model-View-ViewModel)
*   **Asenkron İşlemler:** Kotlin Coroutines
*   **Bağımlılık Enjeksiyonu (Dependency Injection):** Hilt
*   **Ağ İstekleri (Networking):** Retrofit & Gson (JSON parse etmek için)
*   **Görsel Yükleme (Image Loading):** Glide
*   **Navigasyon:** Android Navigation Component (Fragment'lar arası geçiş ve argüman transferi için Safe Args ile)
*   **UI Bileşenleri:** Material Design Components, RecyclerView, CardView, BottomNavigationView, SearchView, Snackbar
*   **Yerel Veritabanı (Favoriler ve Sipariş Geçmişi için):** Room Persistence Library
*   **Yerel Veri Saklama (Adres için):** SharedPreferences
*   **ViewBinding:** Layout dosyalarındaki view'lara güvenli erişim için.
*   **Yaşam Döngüsü Bileşenleri (Lifecycle Components):** ViewModel, LiveData

## Uygulama Özellikleri ve İşlevleri

### Temel İşlevler (PDF Gereksinimleri)

1.  **Yemekleri Listeleme (Anasayfa):**
    *   Uzak bir API'den çekilen tüm yemekler, resimleri, adları ve fiyatlarıyla birlikte bir grid yapısında listelenir.
    *   <!-- BURAYA ANASAYFA YEMEK LİSTESİ EKRAN GÖRÜNTÜSÜ -->
    *   Kullanıcılar, `SearchView` aracılığıyla yemekler arasında arama yapabilir.

2.  **Yemek Detayını Görme:**
    *   Listeden bir yemeğe tıklandığında, o yemeğe ait detayların (büyük resim, ad, fiyat) gösterildiği bir ekran açılır.
    *   <!-- BURAYA YEMEK DETAY SAYFASI EKRAN GÖRÜNTÜSÜ -->

3.  **Detayda Adet Seçebilme:**
    *   Yemek detay sayfasında, kullanıcı sipariş vermek istediği adedi "+" ve "-" butonları ile belirleyebilir.

4.  **Sepete Ekleme:**
    *   **Anasayfadan Hızlı Ekleme:** Her yemek kartında bulunan sepet ikonuna tıklayarak varsayılan 1 adet yemek doğrudan sepete eklenebilir.
    *   **Detay Sayfasından Ekleme:** Kullanıcının seçtiği adetle birlikte "Sepete Ekle" butonuna tıklanarak yemek sepete eklenir.
    *   Başarılı ekleme sonrası kullanıcıya `Snackbar` ile geri bildirim verilir.

5.  **Sepetteki Yemekleri Görüntüleme:**
    *   `BottomNavigationView` üzerinden erişilebilen "Sepetim" sayfasında, kullanıcının sepete eklediği tüm yemekler listelenir.
    *   Her ürün için adı, resmi, birim fiyatı, adedi ve o ürüne ait toplam fiyat gösterilir.
    *   Sayfanın altında gönderim ücreti ve genel toplam tutar dinamik olarak hesaplanıp gösterilir.
    *   <!-- BURAYA SEPET SAYFASI EKRAN GÖRÜNTÜSÜ -->

6.  **Sepetten Yemek Silme:**
    *   Sepet sayfasındaki her ürünün yanında bulunan silme ikonuna tıklanarak ilgili ürün sepetten çıkarılır.
    *   Liste ve toplam tutarlar anında güncellenir.

### Eklenen Ekstra Özellikler

1.  **Favori Yemekler Sistemi:**
    *   Kullanıcılar, anasayfadaki veya detay sayfasındaki kalp ikonuna tıklayarak yemekleri favorilerine ekleyebilir veya çıkarabilirler.
    *   Favori durumu yerel Room veritabanında saklanır ve uygulama kapatılıp açılsa bile korunur.
    *   Anasayfadaki yemek kartlarında ve detay sayfasında yemeğin favori olup olmadığı kalp ikonuyla (dolu/boş) gösterilir.
    *   `BottomNavigationView` üzerinden erişilebilen ayrı bir "Favorilerim" sayfası bulunmaktadır. Bu sayfada sadece favoriye eklenmiş yemekler listelenir.
    *   <!-- BURAYA FAVORİLER SAYFASI EKRAN GÖRÜNTÜSÜ -->

2.  **Karanlık Mod (Dark Mode) Desteği:**
    *   Uygulama, cihazın sistem temasına (Açık/Karanlık Mod) otomatik olarak uyum sağlar.
    *   Karanlık mod için özel renk paleti tanımlanmıştır, bu sayede tüm ekranlar ve bileşenler karanlık temada da okunabilir ve estetik bir görünüme sahiptir.
    *   <!-- BURAYA UYGULAMANIN KARANLIK MODDA BİR EKRAN GÖRÜNTÜSÜ -->

3.  **Kullanıcı Profili ve Sipariş Geçmişi:**
    *   `BottomNavigationView` üzerinden erişilebilen bir "Profilim" sayfası eklenmiştir.
    *   **Teslimat Adresi:** Kullanıcılar bu sayfadan teslimat adreslerini girebilir, görüntüleyebilir ve güncelleyebilirler. Adres bilgisi `SharedPreferences` kullanılarak yerel olarak saklanır.
    *   **Sipariş Geçmişi:** "Sepeti Onayla" butonuna tıklandığında (bu bir simülasyondur, gerçek bir sipariş işlemi API'de yoktur), sepetteki ürünler, sipariş tarihi ve toplam tutar bilgisiyle birlikte Room veritabanına "sipariş" olarak kaydedilir. Bu siparişler, Profilim sayfasında listelenir. Kullanıcılar geçmiş siparişlerinin detaylarını (ürünler, adetler, fiyatlar) görebilir ve sipariş geçmişini temizleyebilirler.
    *   <!-- BURAYA PROFİL SAYFASI (ADRES VE SİPARİŞ GEÇMİŞİ GÖRÜNEN) EKRAN GÖRÜNTÜSÜ -->

4.  **Kategoriye Göre Basit Filtreleme (Anasayfa):**
    *   Anasayfada "Yiyecekler", "İçecekler", "Tatlılar" ve "Tümü" gibi kategorileri temsil eden ikonlar ve başlıklar içeren yatay bir liste bulunmaktadır.
    *   Bir kategoriye tıklandığında, yemek listesi (istemci tarafında, yemek adlarına göre basit bir mantıkla) o kategoriye göre filtrelenir. "Tümü" seçildiğinde veya aynı kategoriye tekrar tıklandığında filtre kaldırılır.
    *   <!-- BURAYA KATEGORİLERİN VE FİLTRELENMİŞ YEMEK LİSTESİNİN GÖRÜNDÜĞÜ BİR EKRAN GÖRÜNTÜSÜ -->

5.  **Gönderim Ücreti Mantığı (Sepet Sayfası):**
    *   Sepet tutarı 500 TL'nin altındaysa 50 TL gönderim ücreti uygulanır.
    *   Sepet tutarı 500 TL ve üzerindeyse gönderim ücreti bedava olur.
    *   Sepet sayfasında, "X TL daha eklerseniz gönderim ücreti bedava!" şeklinde kullanıcıyı bilgilendiren bir mesaj dinamik olarak gösterilir.

6.  **Snackbar ile Geri Bildirimler:**
    *   Sepete ürün ekleme, favori ekleme/çıkarma, sepetten ürün silme gibi işlemler sonrasında kullanıcıya `Toast` mesajları yerine daha modern ve görünür olan `Snackbar` ile geri bildirim verilir.

7.  **Modernleştirilmiş UI Tasarımı ve Tipografi:**
    *   Uygulama genelinde PDF'teki örnek tasarıma kıyasla daha modern bir görünüm hedeflenmiştir.
    *   "Handjet" adlı karakteristik bir font kullanılarak uygulamanın görsel kimliği zenginleştirilmiştir.
    *   Renk paleti, boşluk kullanımı ve genel yerleşim düzeni kullanıcı deneyimini iyileştirmek üzere tasarlanmıştır.

## Mimari Yaklaşım (MVVM)

Proje, Google tarafından önerilen **MVVM (Model-View-ViewModel)** mimari desenine uygun olarak geliştirilmiştir. Bu sayede kodun okunabilirliği, test edilebilirliği ve sürdürülebilirliği artırılmıştır.

*   **Model:**
    *   **Data Katmanı:** API'den gelen veriler için data class'ları (`Yemek`, `SepetYemek` vb.), Room veritabanı için Entity sınıfları (`FavoriYemek`, `Siparis`), DAO'lar (`FavoriYemekDao`, `SiparisDao`) ve `AppDatabase` sınıfını içerir.
    *   **Remote Katmanı:** `RetrofitClient` ve `ApiService` arayüzü ile uzak API'ye yapılan çağrıları yönetir.
    *   **Local Katmanı:** Room veritabanı ve DAO'ları içerir.
    *   **Repository Katmanı (`YemeklerRepository`):** ViewModel'lar için veri kaynağı soyutlaması sağlar. Hem uzak API'den hem de yerel Room veritabanından veri alır/kaydeder ve bu işlemleri ViewModel'lardan gizler. Hilt ile enjekte edilir.
*   **View:**
    *   `MainActivity` ve Fragment'lar (`AnasayfaFragment`, `DetayFragment`, `SepetFragment`, `FavorilerFragment`, `ProfilFragment`) kullanıcı arayüzünü oluşturur.
    *   Layout XML dosyaları ve `RecyclerView.Adapter`'ları (`YemeklerAdapter`, `KategorilerAdapter`, `SiparisGecmisiAdapter`) bu katmanın parçasıdır.
    *   View'lar, ViewModel'lardaki `LiveData`'ları observe ederek UI'ı günceller ve kullanıcı etkileşimlerini ViewModel'a iletir.
*   **ViewModel:**
    *   Her bir Fragment için ayrı ViewModel sınıfları (`AnasayfaViewModel`, `DetayViewModel`, `SepetViewModel`, `ProfilViewModel`) oluşturulmuştur.
    *   ViewModel'lar, Repository aracılığıyla ilgili verileri alır ve işler.
    *   Bu verileri ve UI durumlarını (yüklenme durumu, hata mesajları vb.) `LiveData` aracılığıyla View'a (Fragment'lara) sunar.
    *   `@HiltViewModel` ve `@Inject` anotasyonları ile Hilt tarafından yönetilirler.

## Öğrenilenler ve Karşılaşılan Zorluklar

*   Bu proje sayesinde Kotlin dilini, MVVM mimarisini, Hilt ile bağımlılık enjeksiyonunu, Room veritabanını, Retrofit ile ağ işlemlerini, Navigation Component'i ve Coroutine'leri daha etkin kullanmayı öğrendim.
*   Favori ve sipariş geçmişi gibi özellikleri eklerken `LiveData` transformasyonları (`map`, `switchMap`, `MediatorLiveData`) ve Room ilişkilerini (`@Relation`) anlamak ve uygulamak önemli bir öğrenme deneyimi oldu.
*   İstemci tarafında kategori filtreleme gibi API'nin doğrudan desteklemediği işlevleri implemente etmenin zorlukları ve olası çözüm yolları (basit anahtar kelime eşleştirmesi, manuel ID eşleştirmesi) üzerine düşünüldü.
*   UI tasarımında tutarlılığı sağlamak, farklı ekran boyutları ve modları (açık/karanlık) için kullanıcı dostu bir arayüz oluşturmak zaman ve dikkat gerektirdi.
*   [Buraya karşılaştığınız spesifik bir zorluk ve onu nasıl aştığınızı ekleyebilirsiniz.]

## Gelecekteki Olası Geliştirmeler

*   Gerçek bir backend ile sipariş verme ve ödeme entegrasyonu.
*   Kullanıcı girişi ve çoklu kullanıcı desteği.
*   Daha gelişmiş kategori ve filtreleme seçenekleri (API desteğiyle).
*   Yemeklere puan verme ve yorum yapma özelliği (API desteğiyle).
*   Harita üzerinden adres seçimi ve teslimat takibi.
*   Push bildirimleri ile sipariş durumu güncellemeleri.
*   Daha kapsamlı birim ve UI testleri.

---

Bu taslağı kendi projenize göre düzenleyebilir, eksik gördüğünüz yerleri tamamlayabilir ve ekran görüntülerinizi ilgili yerlere ekleyebilirsiniz. Başarılar dilerim!
