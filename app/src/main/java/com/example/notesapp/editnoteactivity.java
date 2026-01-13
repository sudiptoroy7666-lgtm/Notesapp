package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class editnoteactivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;

    EditText medittitleofnote, meditcontentofnote;
    FloatingActionButton msaveeditnote;
    ImageView noteImageView;
    Button selectImageButton;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Uri imageUri;
    String imageUrl;
    String noteId;

    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnoteactivity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("notes_images");

        medittitleofnote = findViewById(R.id.edittitleofnote);
        meditcontentofnote = findViewById(R.id.editcontentofnote);
        msaveeditnote = findViewById(R.id.saveeditnote);
        noteImageView = findViewById(R.id.noteImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        data = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String notetitle = data.getStringExtra("title");
        String notecontent = data.getStringExtra("content");
        imageUrl = data.getStringExtra("imageUrl");
        noteId = data.getStringExtra("noteId"); // Get the noteId

        medittitleofnote.setText(notetitle);
        meditcontentofnote.setText(notecontent);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Uri uri = Uri.parse(imageUrl);
            noteImageView.setImageURI(uri);
            noteImageView.setVisibility(View.VISIBLE);
        }

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        msaveeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
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

    private void saveNote() {
        String newtitle = medittitleofnote.getText().toString();
        String newcontent = meditcontentofnote.getText().toString();

        if (newtitle.isEmpty() || newcontent.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Something is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference documentReference = firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes")
                .document(noteId); // Use the noteId to reference the correct document

        Map<String, Object> note = new HashMap<>();
        note.put("title", newtitle);
        note.put("content", newcontent);

        if (imageUri != null) {
            uploadImage(note, documentReference);
        } else {
            note.put("imageUrl", imageUrl); // keep the existing image URL if no new image is selected
            updateNoteInFirestore(note, documentReference);
        }
    }

    private void uploadImage(Map<String, Object> note, DocumentReference documentReference) {
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
                                updateNoteInFirestore(note, documentReference);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editnoteactivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void updateNoteInFirestore(Map<String, Object> note, DocumentReference documentReference) {
        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Note is Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(editnoteactivity.this, notesActivity.class));
                finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Updating Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
