package com.android.hihschannelpartner.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hihschannelpartner.Adapter.BannerAdapter;
import com.android.hihschannelpartner.Helper.NetworkConnectionHelper;
import com.android.hihschannelpartner.R;
import com.android.hihschannelpartner.UI.TodayIncome;
import com.android.hihschannelpartner.entity.Banner;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import static com.android.hihschannelpartner.Util.Api_Urls.banner_URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    ViewPager viewPager, vp_CommingProjects;
    Activity activity;
    private ImageView[] dots, dotsc;
    private int dotscount, dotscountc;
    LinearLayout sliderDotspanel, SliderDots1;
    SwipeRefreshLayout srl_refresh;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }
    LinearLayout ll_MyProject,
            ll_RowHouses,
            ll_Flats,
            ll_Plots,
            ll_TodayIncome,
            ll_MothlyIncome,
            ll_MyWallet,
            ll_Recipt_Payment,
            ll_Associate,
            ll_dayBook,
            ll_Customers,
            ll_My_Offers,
            ll_My_Rewards,
            ll_Totalbusiness;
    TextView tv_Project_count,
            tv_RowHouses_count,
            tv_Flats_count,
            tv_Plots_count,
            tv_TodayIncome_count,
            tv_MonthlyIncome_count,
            tv_Reciept_count,
            tv_Associate_count,
            tv_Daybook_count,
            tv_kissan_report_count,
            tv_BusinessPartners_count;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Fresco.initialize(getActivity());
        View view = inflater.inflate(R.layout.fragment_home, container, false);
       /* final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // activity.recreate();
                        pullToRefresh.setRefreshing(false);
                    }
                }, 1000);
                //  refreshData(); // your code

            }
        });*/

        findViewByIds(view);
        forBanner();
//        onClickListeners();
        return view;
    }

    private void findViewByIds(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        sliderDotspanel = view.findViewById(R.id.SliderDots);
        ll_Plots = view.findViewById(R.id.ll_Plots);
        ll_Plots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),TodayIncome.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
            }
        });

    }

    public void forBanner() {

        // progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  banner_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("res","res"+response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String JsonInString = jsonObject1.getString("banner");
                        if (JsonInString != null) {
                            final List<Banner> bannerList = Banner.createListFromJson(JsonInString);
                            if (bannerList != null && bannerList.size() > 0) {
                                final BannerAdapter viewPagerAdapter = new BannerAdapter(getActivity(), bannerList);
                                viewPager.setAdapter(viewPagerAdapter);
                                viewPager.setAdapter(viewPagerAdapter);
                                dotscount = viewPagerAdapter.getCount();
                                dots = new ImageView[dotscount];

                                for (int i = 0; i < dotscount; i++) {
                                    try {
                                        dots[i] = new ImageView(getActivity());
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
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_type","business_partner");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void onClickListeners() {
        ll_RowHouses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TodayIncome.class);
//                intent.putExtra("fromWhere", "row houses");
                startActivity(intent);
            }
        });





        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnectionHelper.isOnline(getActivity())) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                            startActivity(activity.getIntent());
                            activity.overridePendingTransition(0, 0);
                            srl_refresh.setRefreshing(false);
                        }
                    }, 2000);
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection! try again...", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    }

