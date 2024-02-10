package com.example.navidrawerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.example.navidrawerapp.databinding.ActivitySecondCamBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SecondActivityCam extends AppCompatActivity {

    ActivitySecondCamBinding binding;

    ActivityResultLauncher<Intent> launcher;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    Executor executor;
    Handler handler;

    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySecondCamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open,
                R.string.nav_close);
        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_main_activity:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_camera_activity:
                        intent = new Intent(getApplicationContext(), SecondActivityCam.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_settings:
                        Toast.makeText(getApplicationContext(), "You navigated to Setting Screen", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_logout:
                        Toast.makeText(getApplicationContext(), "You are logged out! See ya!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        binding.btnTakePic.setOnClickListener(view ->launcher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE )));

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::fnAfterCam);

        setInitialize();

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        binding.btnAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if(networkInfo != null  && networkInfo.isConnected())
                {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL ImageURL = new URL("https://pbs.twimg.com/media/DTT-1SaU0AANOW-?format=jpg&name=medium");
                                HttpURLConnection connection = (HttpURLConnection) ImageURL.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream inputStream = connection.getInputStream();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                bitmap = BitmapFactory.decodeStream(inputStream,null,options);

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {  // this is to update main thread -- UI
                                @Override
                                public void run() {
                                    binding.imgVwSelfie.setImageBitmap(bitmap);
                                }
                            });
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Network!! Please add dataplan!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setInitialize() {
        // set and initialize the view


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fnAfterCam(ActivityResult result) {

        Bitmap bp = (Bitmap) result.getData().getExtras().get("data");
        binding.imgVwSelfie.setImageBitmap(bp);

        Intent intent = new Intent();
        intent.putExtra("imgSelfie", bp);
        setResult(RESULT_OK,intent);
        //finish();

    }

    private void fnTakePic(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
        launcher.launch(intent);
    }

    private class AsyncTaskFetchImgURL extends AsyncTask <String, String, Bitmap>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected Bitmap doInBackground(String... strings) {
            return null;
        }
    }

 //define subclass ButtonAsyncTask extends AsyncTask
}