package com.android.hihschannelpartner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.hihschannelpartner.Adapter.BannerAdapter;
import com.android.hihschannelpartner.Helper.ViewDialog;
import com.android.hihschannelpartner.Util.Api_Urls;
import com.android.hihschannelpartner.Util.ConnectionDetector;
import com.android.hihschannelpartner.Util.Constants;
import com.android.hihschannelpartner.entity.Banner;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.hihschannelpartner.Util.Api_Urls.BASE_URL;
import static com.android.hihschannelpartner.Util.Api_Urls.banner_URL;
import static com.android.hihschannelpartner.Util.Constants.LOGINKEY;


public class MainActivity extends AppCompatActivity {
    String url = "login";
    TextInputEditText mobile,edt_pass;
    ConnectionDetector connectionDetector;
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private final static int RESOLVE_HINT = 1011;
    String mobNumber;
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobile = findViewById(R.id.edt_mobile);
        edt_pass = findViewById(R.id.edt_pass);
        connectionDetector = new ConnectionDetector(this);
        viewPager = findViewById(R.id.viewPager);
        sliderDotspanel = findViewById(R.id.SliderDots);
        forBanner();
//        forBanner();
        // getPhone();
      /*  AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();*/
        findViewById(R.id.btn_sendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().trim().isEmpty() || mobile.getText().toString().equalsIgnoreCase("")) {
                    Constants.showCustomToastInCenter(MainActivity.this, "Please enter mobile no.");
                    //   Toast.makeText(MainActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                }
//                else if (mobile.getText().toString().trim().length() != 10) {
//                    Toast.makeText(MainActivity.this, "Please Enter 10 digit mobile no.", Toast.LENGTH_SHORT).show();
//                }
                else if (edt_pass.getText().toString().trim().isEmpty() || edt_pass.getText().toString().equalsIgnoreCase("")) {
                    Constants.showCustomToastInCenter(MainActivity.this, "Please enter Password");
                    //   Toast.makeText(MainActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                }
                else {
                    isValid();
                }
                //  startActivity(new Intent(MainActivity.this, OtpVerify.class));
            }
        });
    }

    public void MainActivity() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        startSMSListener();
                        Constants.savePreferences(MainActivity.this, LOGINKEY, jsonObject1.getString("id"));
                        startActivity(new Intent(MainActivity.this, dashboard.class));
                    } else {
                        String substring = jsonObject.getJSONObject("error_code").getString("mobile_no");
                        String str1 = substring.substring(2, substring.length() - 2);
                        Toast.makeText(MainActivity.this, str1, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        //jsonObject.getString("error_code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Internet connection is slow Or no internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_no", mobile.getText().toString().trim());
                params.put("password",edt_pass.getText().toString().trim());
//                params.put("usertype", "agent");
                return params;
                // return super.getParams();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    private void isValid() {
        if (connectionDetector.isConnected()) {
            MainActivity();
        } else {
            Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
        //
    }

    public void forBanner() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  banner_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("res","res"+response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String JsonInString = jsonObject1.getString("banner");
                        if (JsonInString != null) {
                            final List<Banner> bannerList = Banner.createListFromJson(JsonInString);
                            if (bannerList != null && bannerList.size() > 0) {
                                final BannerAdapter viewPagerAdapter = new BannerAdapter(MainActivity.this, bannerList);
                                viewPager.setAdapter(viewPagerAdapter);
                                viewPager.setAdapter(viewPagerAdapter);
                                dotscount = viewPagerAdapter.getCount();
                                dots = new ImageView[dotscount];

                                for (int i = 0; i < dotscount; i++) {
                                    try {
                                        dots[i] = new ImageView(MainActivity.this);
                                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(8, 0, 8, 0);
                                        sliderDotspanel.addView(dots[i], params);
                                    } catch (Exception e) {

                                    }
                                }
                                try {
                                    dots[0].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
                                } catch (Exception e) {

                                }

                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        try {
                                            for (int i = 0; i < dotscount; i++) {
                                                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));
                                            }
                                            dots[position].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                                if (bannerList.size() > 1) {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            handler.postDelayed(this, 5000);
                                            if (viewPager.getCurrentItem() == bannerList.size() - 1) {
                                                viewPager.setCurrentItem(0);
                                            } else {
                                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                                            }
                                        }
                                    }, 5000);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//jsonObject.getString("error_code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_type","business_partner");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.auth.api.credentials.Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    mobNumber = credential.getId();
                    String newString = mobNumber.replace("+91", "");
                    mobile.setText(newString);
                } else {
                    Toast.makeText(this, "err", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void onConnected(@Nullable Bundle bundle) {

    }


    public void onConnectionSuspended(int i) {

    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(MainActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}

