package com.android.hihschannelpartner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.hihschannelpartner.UI.UserProfile;
import com.android.hihschannelpartner.Util.Constants;
import com.android.hihschannelpartner.fragments.HomeFragment;
import com.android.hihschannelpartner.storage.SharedPrefereceUserData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import static com.android.hihschannelpartner.Util.Constants.LOGINKEY;

public class dashboard extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    BottomNavigationView navigation;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    TextView tv_logout,tv_Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        findViewByIds();
        toolbar = findViewById(R.id.toolbar);

        tv_logout = findViewById(R.id.tv_logout);
        setSupportActionBar(toolbar);
//        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawatv_MyOfferble.ic_menu));
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationItemClicks();

        navigation = findViewById(R.id.navigationView);


        HomeFragment homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        navigation.getItemIconTintList();
        navigation.setSelectedItemId(R.id.bnav_Home);
        navigationItemClicks();
        //  isStoragePermissionGranted();
        checkAndRequestPermissions();

    }
    private void findViewByIds() {

        tv_logout = findViewById(R.id.tv_logout);
        tv_Profile = findViewById(R.id.tv_Profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.bnav_Home);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
        builder.setMessage("Are you sure want to exit?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void navigationItemClicks() {
        tv_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, UserProfile.class));
                drawer.closeDrawers();
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
                builder.setMessage("Are you sure want to logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Constants.clearSavedPreferences(dashboard.this, LOGINKEY);
                        final SharedPreferences.Editor sharedPreferences = new SharedPrefereceUserData(dashboard.this).getRemove();
                        sharedPreferences.putString("User", null);
                        sharedPreferences.clear();
                        sharedPreferences.commit();
                        startActivity(new Intent(dashboard.this, MainActivity.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        //transaction.addToBackStack(null);
        fragmentManager.popBackStack();
        transaction.commit();
    }

    private boolean checkAndRequestPermissions() {
        int phone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }






}