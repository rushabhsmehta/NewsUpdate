package com.example.admin.newsupdate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SubmitNews extends AppCompatActivity {

    public static final int PICK_IMAGE = 100;
    private Intent imagedata = null;
    private boolean imageChangedforApprovedNews = false;
    private EditText newsTitle;
    private ImageView newsImage;
    private EditText newsDescription;
    private TextView newsSubmitter;


    private String newstitle;
    private String newsimageurl;
    private String newsdescription;
    private String newssubmitter;
    private String key_to_remove_news;
    private addNews an;

    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_news);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        initialize_variable();

        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        newsSubmitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportActionBar().isShowing())
                    getSupportActionBar().hide();
                else
                    getSupportActionBar().show();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        if(!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("rushabhsmehat@gmail.com")) {
            menu.findItem(R.id.load_unapproved_news).setVisible(false);
            menu.findItem(R.id.approve_news).setVisible(false);
            menu.findItem(R.id.delete_news).setVisible(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit : submitNewsUnApproved(); return (true);
            case R.id.load_unapproved_news: load_unapprovedNews(); return (true);
            case R.id.approve_news: approveNews(); return (true);
            case R.id.delete_news: deleteNews(); return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    private void submitNewsUnApproved() {

        if (newsTitle.getText().toString() != null && newsDescription.getText() != null && imagedata != null) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirm Submission of News ?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    newstitle = newsTitle.getText().toString().trim();
                    newsdescription = newsDescription.getText().toString().trim();
                    newssubmitter = newsSubmitter.getText().toString().trim();

                    Date date = new Date();
                    final ProgressDialog progressDialog = new ProgressDialog(SubmitNews.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage("Uploading...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    uploadTask = mStorageRef.child("unapproved").child(date.toString()).putFile(imagedata.getData());
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
                            an = new addNews(newsdescription, downloadUrl.toString(), "All", newstitle, newssubmitter);
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("unapproved");
                            databaseReference.push().setValue(an).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "News Submitted", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
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

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Picasso.with(getApplicationContext()).load(data.getData()).fit().into(newsImage);
                imagedata = (Intent) data.clone();
                imageChangedforApprovedNews = true;

            } else {
                Toast.makeText(this, "Hey pick your image first",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void load_unapprovedNews() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("unapproved");
        databaseReference.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {
                            an = addNewsSnapshot.getValue(addNews.class);
                            key_to_remove_news = addNewsSnapshot.getKey();
                }
                    Picasso.with(getApplicationContext()).load(an.getImg_url()).fit().into(newsImage);
                    newsTitle.setText(an.getTitle());
                    newsDescription.setText(an.getDescription());
                    newsSubmitter.setText(an.getUser());
                    newsimageurl = an.getImg_url();
                    //imagedata.setData(Uri.parse(an.getImg_url()));
      }
             @Override
            public void onCancelled(DatabaseError databaseError) {
                 Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveNews() {
        if (newsTitle.getText().toString() != null && newsDescription.getText() != null ) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirm Submission of News ?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            newstitle = newsTitle.getText().toString().trim();
                            newsdescription = newsDescription.getText().toString().trim();
                            newssubmitter = newsSubmitter.getText().toString().trim();
                            Date date = new Date();
                            final ProgressDialog progressDialog = new ProgressDialog(SubmitNews.this);
                            progressDialog.setMax(100);
                            progressDialog.setMessage("Uploading...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                            if (imageChangedforApprovedNews) {
                                mStorageRef = FirebaseStorage.getInstance().getReference();
                                uploadTask = mStorageRef.child("approved").child(date.toString()).putFile(imagedata.getData());
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
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(newssubmitter);
                                        sb.append("                         ");
                                        sb.append(new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date()));
                                        newssubmitter = sb.toString();
                                        an = new addNews(newsdescription, downloadUrl.toString(), "All", newstitle, newssubmitter);
                                        databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("approved").push().setValue(an).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("unapproved").child(key_to_remove_news).setValue(null);
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
                                StringBuilder sb = new StringBuilder();
                                sb.append(newssubmitter);
                                sb.append("                         ");
                                sb.append(new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date()));
                                newssubmitter = sb.toString();
                                an = new addNews(newsdescription, newsimageurl, "All", newstitle, newssubmitter);
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("approved").push().setValue(an).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        databaseReference.child("unapproved").child(key_to_remove_news).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Unapproved News Deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    private void initialize_variable() {
        newsTitle = (EditText) findViewById(R.id.news_title);
        newsImage = (ImageView) findViewById(R.id.news_image);
        newsDescription = (EditText) findViewById(R.id.news_description);
        newsSubmitter = (TextView) findViewById(R.id.news_submitter);

        mAuth = FirebaseAuth.getInstance();
        newsSubmitter.setText(mAuth.getCurrentUser().getDisplayName());

    }
}