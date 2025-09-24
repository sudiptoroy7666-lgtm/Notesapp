package com.example.notesapp;

import androidx.annotation.NonNull;
import android.Manifest;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notesapp.databinding.ActivityNotesactivityBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;



import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class notesActivity extends AppCompatActivity {

    private FloatingActionButton mcreatenotefab;
    private FirebaseAuth firebaseAuth;
    private RelativeLayout mnavd;
    private RecyclerView mrecyclerview;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNotesactivityBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

   // private Button setTimerButton;
   private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesactivity);



        binding = ActivityNotesactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

      //  setTimerButton = itemView.findViewById(R.id.set_timer_button);

        drawerLayout = binding.drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = binding.naviView;


        drawerLayout = binding.drawerLayout;
        View logoutTextView = findViewById(R.id.logout);
       View loginTextView = findViewById(R.id.login);
       View signupTextView = findViewById(R.id.signup);
        View aboutTextView = findViewById(R.id.about);
        View uniTextView = findViewById(R.id.uni);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(notesActivity.this, MainActivity.class));
                finish();
                Toast.makeText(notesActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });


        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(notesActivity.this, MainActivity.class));
                finishAffinity();
            }
        });


        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(notesActivity.this, signup.class));
                finishAffinity();
            }
        });

        uniTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(notesActivity.this, Uni.class));
            }
        });


        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(notesActivity.this, About.class));
            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        mcreatenotefab = findViewById(R.id.createnotefab);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mcreatenotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(notesActivity.this, createnote.class));
                finishAffinity();
            }
        });



        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {
                int colorcode = getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorcode, null));
                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());
                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                // Long press listener to show delete option
                noteViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                Intent intent = new Intent(v.getContext(), editnoteactivity.class);
                                intent.putExtra("title", firebasemodel.getTitle());
                                intent.putExtra("content", firebasemodel.getContent());
                                intent.putExtra("noteId", docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);

                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            // Retrieve the image URL from Firestore
                                            String imageUrl = documentSnapshot.getString("imageUrl");

                                            // Delete the document from Firestore
                                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Document deleted successfully
                                                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();

                                                    // If an image is associated with the note, delete it from Firebase Storage
                                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Image deleted successfully
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Failed to delete image
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to delete document
                                                    Toast.makeText(getApplicationContext(), "Failed to Delete", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });

                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Set Timer").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                showTimerDialog(docId, firebasemodel.getTitle(), firebasemodel.getContent());
                                return false;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });


                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), notedetails.class);
                        intent.putExtra("title", firebasemodel.getTitle());
                        intent.putExtra("content", firebasemodel.getContent());
                        intent.putExtra("noteId", docId);
                        v.getContext().startActivity(intent);
                      //  Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        mrecyclerview = findViewById(R.id.recycleview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);
    }

    private void showTimerDialog(String noteId, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Timer");

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        builder.setView(timePicker);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                setNotification(noteId, title, content, hour, minute);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void setNotification(String noteId, String title, String content, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("noteId", noteId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Notification set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


  //  public boolean onOptionsItemSelected(MenuItem item) {
   //     switch (item.getItemId()) {
  //          case android.R.id.home:
  //              drawerLayout.openDrawer(GravityCompat.START); // Open navigation drawer
   //             return true;


    //        default:
   //             return super.onOptionsItemSelected(item);
    //    }
 //   }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem navidr) {
        switch (navidr.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START); // Open navigation drawer
                return true;

            case R.id.logout:

                // Handle logout action
                firebaseAuth.signOut();
                startActivity(new Intent(notesActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(navidr);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

  //  @Override
   // protected void onStop() {
     //  super.onStop();
    //   if (noteAdapter != null) {
   //        noteAdapter.stopListening();
     //  }
  //  }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        // Add your color codes here
        colorcode.add(R.color.Brilliant_Azure);
        colorcode.add(R.color.Peach_Orange);
        colorcode.add(R.color.Crayolas_Blue_Violet);
        colorcode.add(R.color.Tiffany_Blue);
        colorcode.add(R.color.Teal_Deer);
        colorcode.add(R.color.Deep_Champagne);
        colorcode.add(R.color.Goldenrod_Yellow);

        colorcode.add(R.color.Diamond);
      colorcode.add(R.color.Shampoo);
       colorcode.add(R.color.Sunset_Orange);
       colorcode.add(R.color.Alabaster);
       colorcode.add(R.color.Tea_Green);


        colorcode.add(R.color.Crayola);
       colorcode.add(R.color.Middle);
      colorcode.add(R.color.Mauvelous);
       colorcode.add(R.color.Lilac);
       colorcode.add(R.color.Desert);
       colorcode.add(R.color.Keppel);
       colorcode.add(R.color.Light);
        colorcode.add(R.color.Cornflower);





        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}
