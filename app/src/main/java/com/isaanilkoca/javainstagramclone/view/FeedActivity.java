package com.isaanilkoca.javainstagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.isaanilkoca.javainstagramclone.R;
import com.isaanilkoca.javainstagramclone.adapter.PostAdapter;
import com.isaanilkoca.javainstagramclone.databinding.ActivityFeedBinding;
import com.isaanilkoca.javainstagramclone.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding; // recylerview ulsamak icin binding
    PostAdapter postAdapter;

 // ana akıs ekranımız
 //Arayüz kurulumu ve Firebase başlatma.
 //Gönderileri almak için getData() çağrılır. oncreate özetim
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();// görünümü aldik
        setContentView(view);

        postArrayList = new ArrayList<>();//ilk basladıgında bos bi array listimiz var


        auth= FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        binding.recylerView.setLayoutManager(new LinearLayoutManager((this)));
        postAdapter = new PostAdapter(postArrayList);
        binding.recylerView.setAdapter(postAdapter);
    }


    //Firestore'dan gönderileri alır ve postArrayList'e ekler.
    //RecyclerView'ı günceller. getdata özetim

    private void getData(){ //
        //DocumentReference documentReference = firebaseFirestore.collection("Posts").document("blabla");
        //Collection documentReference = firebaseFirestore.collection("Posts");
        //whereEqualTo("useremail","isaanilk@gmail.com") kimin göndersini görmek istedigimiz sql ile aynı mantık


        //orderby da dizimdir,neye göre dizecegimizi seceriz filtreliyoruz
        //ascending artan ya da descenting azalan
// filtreleme islemleri burada yapildi, (EN GÜNCEL GÖNDERİNİN EN YUKARIDA GÖRÜLMESİ DURUMU , tarihe göre sıralama güncellik)

        // addsnapshıtlistenerdan önce filtreleme yapilir
        // order by ("date ile tarhie göre dizdik")
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error!= null){
                Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
            if(value != null){
                for(DocumentSnapshot snapshot: value.getDocuments()) {  // dizi,liste veriyor, bu dizi icerisinden tek tek elamanları alıp snapshot isimli degiskene kaydeder

                    Map<String, Object> data= snapshot.getData();
                   //Casting =  hata nedeniyle casting yapildi object görüdügü icin,string oldugunu casting
                    String usereamil = (String) data.get("usereamil");
                    String comment=(String) data.get("comment");
                    String downloadUrl=(String) data.get("downloadurl");
                    System.out.println(usereamil); // veri tabanından bilgi cekebiliyor muyuz testi
                    Post post= new Post(usereamil,comment,downloadUrl);
                    postArrayList.add(post);
                }
                postAdapter.notifyDataSetChanged(); // recylview yeni veri geldiginde göster amacında
            }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // baglama yeri
        MenuInflater menuInflater=getMenuInflater();  // kodu birbirinze baglamak icin kullandıgım yerdir
        menuInflater.inflate(R.menu.opiton_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //secince ne olacagı

        if(item.getItemId()==R.id.add_post){ // kullanıcı addposta tıkladıgında ne yapacagını bakarız
        //upload activity
            Intent intentToUpload=new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);
        }else if(item.getItemId()==R.id.signout){
         //Signout
          auth.signOut();
         Intent intentToMain =new Intent(FeedActivity.this,MainActivity.class);
         startActivity(intentToMain);
         finish();// kullanıcı cıkıs yaptıysa geri dönmemesi lzaim
        }
        return super.onOptionsItemSelected(item);
    }
}