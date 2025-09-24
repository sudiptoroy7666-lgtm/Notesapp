package com.example.notesapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class createnote extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;

    EditText mcreatecontentofnote, mcreatetitleofnote;
    FloatingActionButton msavenote;
    ImageView noteImageView;
    Button selectImageButton;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Uri imageUri;

    Toolbar toolbar;
    DocumentReference currentNoteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);

        mcreatecontentofnote = findViewById(R.id.createcontentofnote);
        mcreatetitleofnote = findViewById(R.id.createtitleofnote);
        msavenote = findViewById(R.id.savenote);
        noteImageView = findViewById(R.id.noteImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("notes_images");

        // Toolbar setup
        toolbar = findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the reference to the current note
        currentNoteRef = firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes")
                .document();

        // Add TextWatcher to EditText fields for auto-save
     //   mcreatecontentofnote.addTextChangedListener(noteTextWatcher);
    //    mcreatetitleofnote.addTextChangedListener(noteTextWatcher);

        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            noteImageView.setImageURI(imageUri);
            noteImageView.setVisibility(View.VISIBLE);
        }
    }

    // TextWatcher to monitor changes in EditText fields

    // Method to update the content of the current note
    private void updateCurrentNote() {
        String title = mcreatetitleofnote.getText().toString();
        String content = mcreatecontentofnote.getText().toString();

        // Check if title and content are not empty
        if (!title.isEmpty() && !content.isEmpty()) {
            // Create a new note map
            Map<String, Object> note = new HashMap<>();
            note.put("title", title);
            note.put("content", content);

            // Update the content of the current note
            currentNoteRef.set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Note content updated successfully
                          //  Toast.makeText(getApplicationContext(), "Note Auto-saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update note content
                            Toast.makeText(getApplicationContext(), "Failed to auto-save note", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to save the note manually
    private void saveNote() {
        String title = mcreatetitleofnote.getText().toString();
        String content = mcreatecontentofnote.getText().toString();

        // Check if title and content are not empty
        if (!title.isEmpty() && !content.isEmpty()) {
            // Create a new note map
            Map<String, Object> note = new HashMap<>();
            note.put("title", title);
            note.put("content", content);

            // Check if an image is selected
            if (imageUri != null) {
                uploadImage(note);
            } else {
                saveNoteToFirestore(note);



            }
        }
    }

    private void uploadImage(Map<String, Object> note) {
        StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                note.put("imageUrl", uri.toString());
                                saveNoteToFirestore(note);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(createnote.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void saveNoteToFirestore(Map<String, Object> note) {
        firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(createnote.this, notesActivity.class));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to save note", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            // Navigate back to the previous activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate back to the notesActivity
        Intent intent = new Intent(this, notesActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent it from being added to the back stack again
    }
}
