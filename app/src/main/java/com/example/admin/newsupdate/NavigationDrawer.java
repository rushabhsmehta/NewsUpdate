package com.example.admin.newsupdate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.submit_news) {

            submitNews();
            // Handle the camera action
        }
        else if (id == R.id.load_unapproved_news) {
            linearLayout.setVisibility(View.VISIBLE);
            newsImage.setVisibility(View.VISIBLE);
            newsTitleGuj.setVisibility(View.GONE);
            newsDescriptionEng.setVisibility(View.VISIBLE);
            newsSubmitter.setVisibility(View.VISIBLE);
            btn_for_select_image.setVisibility(View.GONE);
            //load_unapprovedNews();
        } else if (id == R.id.approve_news) {
            approveNews();

        } else if (id == R.id.delete_news) {
            //deleteNews();
        }
        else if (id == R.id.submit_event) {
            submitEvent();
        }
        else if (id == R.id.approve_event) {
            approveEvent();
        }
        else if (id == R.id.submit_ad) {
            submitAd();
        }
        else if (id == R.id.approve_ad) {
            approveAd();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void submitNews()
    {
        Intent intent = new Intent(NavigationDrawer.this, SubmitNews.class);
        startActivity(intent);
        finish();
    }

    private void approveNews()
    {
        Intent intent = new Intent(NavigationDrawer.this, ApproveNews.class);
        startActivity(intent);
        finish();
    }

    private void submitEvent()
    {
        Intent intent = new Intent(NavigationDrawer.this, SubmitEvent.class);
        startActivity(intent);
        finish();
    }
    private void approveEvent()
    {
        Intent intent = new Intent(NavigationDrawer.this, ApproveEvent.class);
        startActivity(intent);
        finish();
    }
    private void submitAd()

    {
        Intent intent = new Intent(NavigationDrawer.this, SubmitAd.class);
        startActivity(intent);
        finish();
    }
    private void approveAd()

    {
        Intent intent = new Intent(NavigationDrawer.this, ApproveAd.class);
        startActivity(intent);
        finish();
    }
}