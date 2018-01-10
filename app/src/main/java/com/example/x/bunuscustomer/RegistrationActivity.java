package com.example.x.bunuscustomer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ImageView imgHome = (ImageView) findViewById(R.id.imageViewHome);
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Set up the login form.
        Fragment fragment = new FragmentRegistrations(this);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }

        try {
            //init();
            //transaction();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                //transaction();
            } else {
                requestCameraPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
    }
    public void toastEr(){
        Toast.makeText(this,"Проверьте подключение к интернету...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // do what you want to be done on home button click event
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void progress(){
        if(progressBar.getVisibility()==View.VISIBLE) progressBar.setVisibility(View.GONE);
        else progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        try {
            if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
                return;
            }

            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                init();
//                transaction();
                //initQRCodeReaderView();
            } else {
                Snackbar.make(progressBar, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestCameraPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(progressBar, "Требуется разрешить доступ к файлам.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        ActivityCompat.requestPermissions(RegistrationActivity.this, new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, MY_PERMISSION_REQUEST_CAMERA);
                    }
                }).show();
            } else {
                Snackbar.make(progressBar, "",
                        Snackbar.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {
                         Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, MY_PERMISSION_REQUEST_CAMERA);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

