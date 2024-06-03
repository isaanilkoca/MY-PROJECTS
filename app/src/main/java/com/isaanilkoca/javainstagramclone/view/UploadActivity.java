package com.isaanilkoca.javainstagramclone.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isaanilkoca.javainstagramclone.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
   private FirebaseStorage firebaseStorage;
   private FirebaseAuth auth;
   private FirebaseFirestore firebaseFirestore;
   private StorageReference storageReference;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher; // bu intent cünkü direk galeriye gitme intenit ve veriyi geri aldıgımız
    ActivityResultLauncher<String> permissionLauncher; // izin istiyoruz burdada
    private ActivityUploadBinding binding;
    //Bitmap selectedImage;
    @Override

    //Aktivite oluşturulduğunda arayüz bağlanır ve Firebase bileşenleri başlatılır.


    protected void onCreate(Bundle savedInstanceState) { // lacunherlrei önce register ederiz kullandıgımıız göstermek icin tanımlar oncreat aldında
        super.onCreate(savedInstanceState);
        binding=ActivityUploadBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();


        firebaseStorage = FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference(); // böyle alabiliriz referans alarak(bos alanı referans eder gösterir verir)


    }
    public void uploadButtonClicked(View view){


        //Bu kısımda, kullanıcının seçtiği görselin Firebase Storage'a yüklenme işlemi gerçekleştirilir.
        //Yükleme işlemi başarılı olduğunda, yüklenen görselin indirme URL'si alınır ve bu URL ile diğer gönderi bilgileri birlikte Firebase Firestore'a kaydedilir.

    if(imageData != null){ // kullanıcı secti mi secmedi mi anlama kısmımız !!!!
        //universal unique id her seferinde farklı bi isim olusturmamız gerekir yeni ekledigimizde silinmesin diye

        UUID uuid = UUID.randomUUID(); // UYDURMA İSİM OLUSTURUR UUID SINIFI
        String imageName ="images/" +uuid +".jpg"; // hep images klasörüne koy uydurma isim ver sonunda jpg ekle demek !!!


        // storageReference.putFile();// bi uri koy deriz ve calisir bos alana koyar ama en dogru yöntem degil , klasör altına koyulmalı

        // images diye bi klasör olustur altınada jpg dosyası ekle

        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             // download url alınır burada,
             StorageReference newReference=firebaseStorage.getReference(imageName); // kaydettigimiz görselin referansını olusturms oluyoruz kaydettikten sonra

              newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {

                  //HashMap e yüklenir alınır ve burada basarılı oldugunda : getDownloadUrl() ile!!!1


                  String downloadUrl = uri.toString(); // görsel burada

                  String comment=binding.commentText.getText().toString();// yorum burada

                      FirebaseUser user = auth.getCurrentUser(); // kullanıcının kendisini alırız burada *****

                      String email= user.getEmail();

                      HashMap<String,Object> postData=new HashMap<>(); // anahtar kelime string degerler object olsun
                      postData.put("usereemail",email);
                      postData.put("downloadurl",downloadUrl);
                      postData.put("comment",comment);
                      postData.put("date", FieldValue.serverTimestamp());// Field value firebase kendi sınıfı serverise güncel saati veriir

                      firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                          @Override
                          public void onSuccess(DocumentReference documentReference) {
                           Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // HER ŞEYİ BİZİM İCİN TEMİZLER
                           startActivity(intent);
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                         Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                          }
                      });
                  }
              });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // hatada mesaj gösterilir

                Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }

    }
    public void selectImage(View view) {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 ve sonrası
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        }

        // İlk olarak, cihazın SDK sürümüne bağlı olarak doğru izin türü belirlenir.
        //Sonra, uygulamanın galeriye erişim izni olup olmadığı kontrol edilir. Eğer izin yoksa, kullanıcıya izin gerekçesi gösterilir ve izin isteme işlemi başlatılır.
        //Eğer izin varsa, galeriye erişim izni alınmış demektir ve galeri açılır.

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa ne yapacağımız kısmı
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Kullanıcıya izin gerekçesini göster
                String finalPermission = permission;
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // İzin isteme işlemi
                                permissionLauncher.launch(finalPermission);
                            }
                        }).show();
            } else {
                // İzin isteme işlemi
                permissionLauncher.launch(permission);
            }
        } else {
            // İzin verilmişse galeriye intent yap
            openGallery();
        }
    }
    private void openGallery() {
        //galeri uygulamasını acilmasi icin intent olusuur ve activityResultLauncher ile baslar


        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intentToGallery);



    }

    //
    private void registerLauncher(){


        //Kullanıcı bir resim seçtiğinde, onActivityResult() metodunda seçilen resmin URI'si alınır ve imageData değişkenine atanır
        //Ardından, bu URI, kullanıcının seçtiği resmi göstermek için ImageView'e atanır.
        //registerLauncher() metodunda ise, activityResultLauncher ve permissionLauncher için ActivityResultContracts ve ActivityResultCallback'ler kaydedilir.
        //Galeriye erişim izni istenildiğinde veya galeriden bir resim seçildiğinde, bu callback'ler çağrılır ve gerekli işlemler gerçekleştirilir.


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {// yeni bir aktiviyte baslatma yeni sonuc icin
            @Override
            public void onActivityResult(ActivityResult result) {
           if(result.getResultCode() == RESULT_OK){ //  sonucu kodunu görüyoruz sonra HER ŞEY OKEYSE
           Intent intentFromResult=result.getData(); // degiskene kaydedip veriyi alıyoruz
           if(intentFromResult!= null){ // veri bos mu degilmi kontrol noktası ????
               imageData=intentFromResult.getData(); // firebase koymak icin yeterli
               binding.imageView.setImageURI(imageData); // surdaki satır yorumdakiler yapar
/*
              // BİTMAPA CEVİRME KİSMİ
               try{
                   if(Build.VERSION.SDK_INT>=28){ // try cathce buildi kontrol et sdk verisyonu yüksekse ayrı bi yöntem
                       ImageDecoder.Source source=ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                         selectedImage=ImageDecoder.decodeBitmap(source);
                               binding.imageView.setImageBitmap(selectedImage);
                   }else{
                       selectedImage=MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);
                       binding.imageView.setImageBitmap(selectedImage);
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }*/
           }
           }
            }
        });
        // registerlauncher icindeyim
        permissionLauncher=
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){ // result truysa
                    Intent intentToGallery =new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(UploadActivity.this,"Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}