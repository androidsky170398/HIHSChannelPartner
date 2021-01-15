package com.android.hihschannelpartner.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hihschannelpartner.Helper.NetworkConnectionHelper;
import com.android.hihschannelpartner.MainActivity;
import com.android.hihschannelpartner.R;
import com.android.hihschannelpartner.Util.Api_Urls;
import com.android.hihschannelpartner.Util.Constants;
import com.android.hihschannelpartner.dashboard;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import static com.android.hihschannelpartner.Util.Constants.LOGINKEY;


public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 1500;
    LinearLayout ll_1, ll_2;
    NumberProgressBar npb_progress;
    int progresscount = 0;
    ImageView img_icon;
    String general = "general-setting";
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_splash);
        Animation uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        Animation downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        npb_progress = findViewById(R.id.npb_progress);
        img_icon = findViewById(R.id.img_icon);
        txt = findViewById(R.id.txt);
        txt.setAnimation(downtoup);
        img_icon.setAnimation(uptodown);
        npb_progress.setProgress(progresscount);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progresscount == 100) {
                    /**/
                } else {
                    handler.postDelayed(this, 30);
                    progresscount++;
                    npb_progress.setProgress(progresscount);
                }
            }
        }, 200);

        /*Animation scaleUp = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.scale_up);
        Animation downToUp = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.downtoup);

        ll_1 = findViewById(R.id.ll_1);
        ll_2 = findViewById(R.id.ll_2);
        ll_1.startAnimation(scaleUp);
       *//* findViewById(R.id.txt1).startAnimation(downToUp);
        findViewById(R.id.txt2).startAnimation(downToUp);
        findViewById(R.id.txt3).startAnimation(downToUp);*//*
        ll_2.startAnimation(downToUp);*/
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (NetworkConnectionHelper.isOnline(SplashActivity.this)) {
                    if (Constants.getSavedPreferences(SplashActivity.this, LOGINKEY, "").equalsIgnoreCase("")) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                 //       startActivity(new Intent(SplashActivity.this, Login.class));

                    } else {
                       /* User user = User.createobjectFromJson(new SharedPrefereceUserData(getApplicationContext()).getSharedData().getString("User", null));
                        if (user != null && user.getUsertype() != null && user.getUsertype().equalsIgnoreCase("owner")) {
                            startActivity(new Intent(SplashActivity.this, Owner_Dashboard.class));
                        } else {*/
                        startActivity(new Intent(SplashActivity.this, dashboard.class));
                    }
                    // }
                } else {
                    Toast.makeText(SplashActivity.this, "Please Check your Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void generalSetting() {
        final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api_Urls.BASE_URL + general, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        Picasso.with(SplashActivity.this).load(jsonObject1.getString("site_logo")).fit().into(img_icon);
                        // tv_header.setText(jsonObject1.getString("site_title"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(SplashActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //  Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(SplashActivity.this, "Internet connection is slow Or no internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        requestQueue.add(stringRequest);
    }

}
