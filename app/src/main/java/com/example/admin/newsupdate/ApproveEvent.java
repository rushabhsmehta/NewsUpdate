package com.example.admin.newsupdate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class ApproveEvent extends AppCompatActivity {

    public static final int PICK_IMAGE = 100;
    private Intent eventimagedata = null;
    private boolean imageChangedforApprovedEvent = false;
    private EditText eventTitle;
    private ImageView eventImage;
    private EditText eventDescription;
    private Button submitevent;
    private Button approveevent;
    private NewEvent newEvent;
    private String eventtitle;
    private String eventimageurl;
    private String eventdescription;
    private String eventubmitter;
    private String key_to_remove_event;

    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_approve_event);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        initialize_variable();

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        approveevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveevent();
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Picasso.with(getApplicationContext()).load(data.getData()).fit().into(eventImage);
                eventimagedata = (Intent) data.clone();
                imageChangedforApprovedEvent = true;

            } else {
                Toast.makeText(this, "Hey pick your image first",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void load_unapprovedEvent() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("unapprovedevent");
        databaseReference.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot NewEventSnapshot : dataSnapshot.getChildren()) {
                    newEvent = NewEventSnapshot.getValue(NewEvent.class);
                    key_to_remove_event = NewEventSnapshot.getKey();
                }
                Picasso.with(getApplicationContext()).load(newEvent.getImg_url()).fit().into(eventImage);
                eventTitle.setText(newEvent.getTitle());
                eventDescription.setText(newEvent.getDescription());
                eventimageurl = newEvent.getImg_url();
                //imagedata.setData(Uri.parse(an.getImg_url()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveevent() {
        if (eventTitle.getText().toString() != null && eventDescription.getText() != null) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirm Submission of News ?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    eventtitle = eventTitle.getText().toString().trim();
                    eventdescription = eventDescription.getText().toString().trim();
                    Date date = new Date();
                    final ProgressDialog progressDialog = new ProgressDialog(ApproveEvent.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage("Uploading...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    if (imageChangedforApprovedEvent) {
                        mStorageRef = FirebaseStorage.getInstance().getReference();
                        uploadTask = mStorageRef.child("approvedevent").child(date.toString()).putFile(eventimagedata.getData());
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //sets and increments value of progressbar
                                progressDialog.incrementProgressBy((int) progress);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Toast.makeText(getApplicationContext(), "Image Upload successful", Toast.LENGTH_SHORT).show();
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                newEvent = new NewEvent(eventdescription, downloadUrl.toString(), "All", eventtitle, eventubmitter);
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("approved").push().setValue(newEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("unapprovedevent").child(key_to_remove_event).setValue(null);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        newEvent = new NewEvent(eventdescription, eventimageurl, "All", eventtitle, eventubmitter);
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("approvedevent").push().setValue(newEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss();
                                deleteNews();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            adb.show();
        } else
            Toast.makeText(getApplicationContext(), "Please Enter Title, Description and also upload Image", Toast.LENGTH_LONG).show();
    }

    private void deleteNews() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("unapprovedevent").child(key_to_remove_event).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Unapproved News Deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initialize_variable() {
        eventTitle = (EditText) findViewById(R.id.event_title);
        eventImage = (ImageView) findViewById(R.id.event_image);
        eventDescription = (EditText) findViewById(R.id.event_description);
        submitevent = (Button) findViewById(R.id.submit_event);
        submitevent.setVisibility(View.INVISIBLE);
        approveevent = (Button) findViewById(R.id.approve_event);
        approveevent.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        eventubmitter = mAuth.getCurrentUser().getDisplayName();
        load_unapprovedEvent();
    }
}

