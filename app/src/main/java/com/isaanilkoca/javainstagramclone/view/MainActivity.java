package com.isaanilkoca.javainstagramclone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.isaanilkoca.javainstagramclone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth; // limik dogrulama islemi authentacion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        FirebaseUser user= auth.getCurrentUser();// daha önce giris yapma kullanıcı varsa onu verir

        if(user!=null){
            Intent intent= new Intent(MainActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signInClicked(View view){
        String email =binding.emalilText.getText().toString();
        String password=binding.passwordText.getText().toString();

        if(email.equals("")||password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                startActivity(intent);
                finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
              Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void signUpClicked(View view){

        String email=binding.emalilText.getText().toString();
        String password=binding.passwordText.getText().toString();

        if(email.equals("")|| password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {//basarılı olursa kısmıdır
                @Override
                public void onSuccess(AuthResult authResult) { // basarılı olursak napıcagımızı metodudur

                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() { // hata olursak ne olucak kısmı
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show(); // getlocalizedmessage analyacagı türden bi mesaj ver demek74

                }
            });
        }
    }
}