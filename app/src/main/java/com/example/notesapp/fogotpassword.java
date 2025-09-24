package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class fogotpassword extends AppCompatActivity {

    private EditText mforgotpassword;
    private Button mpasswordrecoverbutton;
    private TextView mgobacktologin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogotpassword);

        firebaseAuth = FirebaseAuth.getInstance();

           getSupportActionBar().hide();
        mforgotpassword=findViewById(R.id.forgotpassword);
        mpasswordrecoverbutton=findViewById(R.id.passwordrecoverbutton);
        mgobacktologin=findViewById(R.id.gobacktologin);

        mgobacktologin.setOnClickListener(new View.OnClickListener(){

     public void onClick(View v) {

         Intent intent = new Intent(fogotpassword.this, MainActivity.class);
         startActivity(intent);
         finishAffinity();
     }

     });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

      String mail = mforgotpassword.getText().toString().trim();

    if(mail.isEmpty()){
        Toast.makeText(getApplicationContext(), "Enter Your Email", Toast.LENGTH_SHORT).show();
    }

    else {

 firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
     @Override
     public void onComplete(@NonNull Task<Void> task) {
         if (task.isSuccessful()) {

             Toast.makeText(getApplicationContext(), "Email is sent", Toast.LENGTH_SHORT).show();

             finish();
             startActivity(new Intent(fogotpassword.this, MainActivity.class));
             finishAffinity();
         }
         else{
             Toast.makeText(getApplicationContext(), "Account doesn't exist", Toast.LENGTH_SHORT).show();

         }

     }
 });
    }
    }



        });




    }
}