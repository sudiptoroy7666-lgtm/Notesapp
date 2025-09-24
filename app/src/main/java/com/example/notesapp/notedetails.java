package com.example.notesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class notedetails extends AppCompatActivity {

    private TextView titleTextView, contentTextView;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private FloatingActionButton gotoedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notedetails);

        gotoedit = findViewById(R.id.gotoeditnote);



        Intent data = getIntent();
   gotoedit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), editnoteactivity.class);
        intent.putExtra("title", data.getStringExtra("title"));
        intent.putExtra("content", data.getStringExtra("content"));
        intent.putExtra("noteId", data.getStringExtra("noteId"));
        intent.putExtra("imageUri", data.getStringExtra("imageUri"));
        v.getContext().startActivity(intent);
        // Intent intent = new Intent(notedetails.this, editnoteactivity.class);
       // startActivity(intent);
    }
});
        // Initialize views
        titleTextView = findViewById(R.id.titleofnotedetail);
        contentTextView = findViewById(R.id.contentofnotedetail);
        imageView = findViewById(R.id.noteImageView);

        // Initialize Firestore and current user
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbarofnotedetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get noteId from intent
        String noteId = getIntent().getStringExtra("noteId");

        // Fetch note details from Firestore
        firestore.collection("notes")
                .document(currentUser.getUid())
                .collection("myNotes")
                .document(noteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String content = documentSnapshot.getString("content");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Set title and content
                        titleTextView.setText(title);
                        contentTextView.setText(content);

                        // Load image using Picasso if imageUrl is not null
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageView.setVisibility(View.VISIBLE);
                            Picasso.get().load(imageUrl).into(imageView);
                        } else {
                            imageView.setVisibility(View.GONE); // Hide ImageView if no image
                        }
                    } else {
                        Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
                        finish(); // Finish activity if note is not found
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch note details", Toast.LENGTH_SHORT).show();
                    finish(); // Finish activity on failure
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Navigate back when toolbar back button is clicked
        return true;
    }
}
