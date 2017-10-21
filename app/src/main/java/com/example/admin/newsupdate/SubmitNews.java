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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class SubmitNews extends AppCompatActivity {

    public static final int PICK_IMAGE = 100;
    private Intent imagedata = null;
    private boolean imageChangedforApprovedNews = false;
    private EditText newsTitleEng;
    private EditText newsTitleGuj;
    private ImageView newsImage;
    private EditText newsDescriptionEng;
    private EditText newsDescriptionGuj;
    private TextView newsSubmitter;
    private Button btn_for_select_image;
    private Button btn_for_description;
    private Button btn_view_final_news;
    private LinearLayout linearLayout;

    private String newstitleeng;
    private String newstitleguj;
    private String newsimageurl;
    private String newsdescriptioneng;
    private String newsdescriptionguj;
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
        setContentView(R.layout.submit_approve_news);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().hide();
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

                if(newsTitleEng.getVisibility() == View.GONE)
                {
                    newsTitleEng.setVisibility(View.VISIBLE);
                    newsTitleGuj.setVisibility(View.GONE);
                }
                else
                {
                    newsTitleEng.setVisibility(View.GONE);
                    newsTitleGuj.setVisibility(View.VISIBLE);
                }
                if(newsDescriptionEng.getVisibility() == View.GONE)
                {
                    newsDescriptionEng.setVisibility(View.VISIBLE);
                    newsDescriptionGuj.setVisibility(View.GONE);
                }
                else
                {
                    newsDescriptionEng.setVisibility(View.GONE);
                    newsDescriptionGuj.setVisibility(View.VISIBLE);
                }

            }
        });
        btn_for_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsTitleEng.setVisibility(View.GONE);
                newsTitleGuj.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                newsImage.setVisibility(View.VISIBLE);
                btn_for_description.setVisibility(View.VISIBLE);
                btn_for_select_image.setVisibility(View.GONE);

            }
        });
        btn_for_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                newsImage.setVisibility(View.GONE);
                newsDescriptionEng.setVisibility(View.VISIBLE);
                newsDescriptionGuj.setVisibility(View.VISIBLE);
                btn_for_description.setVisibility(View.GONE);
                btn_view_final_news.setVisibility(View.VISIBLE);
            }
        });
        btn_view_final_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsTitleEng.setVisibility(View.VISIBLE);
                newsDescriptionEng.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                newsImage.setVisibility(View.VISIBLE);
                newsTitleGuj.setVisibility(View.GONE);
                newsDescriptionGuj.setVisibility(View.GONE);
                btn_view_final_news.setVisibility(View.GONE);
                newsSubmitter.setVisibility(View.VISIBLE);
            }
        });

    }
    private void submitNewsUnApproved() {

        if (newsTitleEng.getText().toString() != null && newsTitleGuj.getText().toString() != null &&
                newsDescriptionEng.getText() != null && newsDescriptionGuj.getText() != null && imagedata != null) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirm Submission of News ?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    newstitleeng = newsTitleEng.getText().toString().trim();
                    newstitleguj = newsTitleGuj.getText().toString().trim();
                    newsdescriptioneng = newsDescriptionEng.getText().toString().trim();
                    newsdescriptionguj = newsDescriptionGuj.getText().toString().trim();
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
                            StringBuilder sb = new StringBuilder();
                            /*sb.append(newssubmitter);
                            sb.append("                         ");
                            sb.append(new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date()));
                            newssubmitter = sb.toString(); */
                            an = new addNews(newsdescriptionguj, newsdescriptioneng,  downloadUrl.toString(), "All", newstitleguj,newstitleeng, newssubmitter);
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
    private void initialize_variable() {
        newsTitleEng = (EditText) findViewById(R.id.news_title_eng);
        newsTitleGuj = (EditText) findViewById(R.id.news_title_guj);
        newsImage = (ImageView) findViewById(R.id.news_image);
        newsDescriptionEng = (EditText) findViewById(R.id.news_description_eng);
        newsDescriptionGuj = (EditText) findViewById(R.id.news_description_guj);
        newsSubmitter = (TextView) findViewById(R.id.news_submitter);
        btn_for_select_image = (Button) findViewById(R.id.button_next_for_image_select);
        btn_for_description = (Button) findViewById(R.id.button_next_for_description);
        btn_view_final_news = (Button) findViewById(R.id.view_final_news);
        linearLayout = (LinearLayout) findViewById(R.id.news_image_layout);
        mAuth = FirebaseAuth.getInstance();
        newsSubmitter.setText(mAuth.getCurrentUser().getDisplayName());
    }
}

