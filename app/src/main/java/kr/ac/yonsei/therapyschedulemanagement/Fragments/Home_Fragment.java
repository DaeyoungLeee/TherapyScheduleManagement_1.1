package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import kr.ac.yonsei.therapyschedulemanagement.Adatpers.CalendarDaySchdule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.Adatpers.HomeMonthSchedule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.HomeMonth_CardItem;
import kr.ac.yonsei.therapyschedulemanagement.JobSchedulerStart;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Home_Fragment extends Fragment {

    private static final String TAG = "FRAGMENT1";

    // Firebase 객체
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // UI element
    private ImageView img_weather;
    private TextView txt_date, txt_location, txt_weather;
    private TextView txt_temp, txt_humidity, txt_wind;
    private double latitude, longitude;
    private int year, month, day, day1, day2, nowHour, nowMinute;
    private SlidingUpPanelLayout sl_q1, sl_q2, sl_q3, sl_q4, sl_q5, sl_main;
    private WebView web_q1, web_q2, web_q3, web_q4, web_q5;
    private HomeMonthSchedule_Adapter homeMonthScheduleAdapter;
    private RecyclerView recyclerViewMonth;
    private RelativeLayout linearLayoutMain;
    private AVLoadingIndicatorView avi_home_weather;
    public static LinearLayout home_block, linear_recycle_block;
    private LinearLayout linear_home_loading;

    private boolean isRunning1 = false;
    private boolean isRunning2 = false;
    private boolean isRunning3 = false;

    // 주소정보
    private String area1, area2, area3, area4;
    private String cityName;

    // 날씨 Flag
    private boolean findWeatherFlag = true;

    public static Home_Fragment newInstance() {
        Home_Fragment f = new Home_Fragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //Firebase elements
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // UI elements mapping
        img_weather = view.findViewById(R.id.img_weather);
        txt_date = view.findViewById(R.id.txt_date);
        txt_weather = view.findViewById(R.id.txt_weather_information);
        txt_location = view.findViewById(R.id.txt_location);
        txt_temp = view.findViewById(R.id.txt_temperature);
        txt_humidity = view.findViewById(R.id.txt_humidity);
        txt_wind = view.findViewById(R.id.txt_wind);
        web_q1 = view.findViewById(R.id.web_q1);
        web_q2 = view.findViewById(R.id.web_q2);
        web_q3 = view.findViewById(R.id.web_q3);
        web_q4 = view.findViewById(R.id.web_q4);
        web_q5 = view.findViewById(R.id.web_q5);
        recyclerViewMonth = view.findViewById(R.id.recyclerView_home);
        linearLayoutMain = view.findViewById(R.id.linear_main);
        avi_home_weather = view.findViewById(R.id.avi_home_weather);
        home_block = view.findViewById(R.id.linear_home_weather_block);
        linear_recycle_block = view.findViewById(R.id.linear_recycle_block);
        linear_home_loading = view.findViewById(R.id.linear_home_loading);

        // 날씨정보창 로딩
        avi_home_weather.smoothToShow();
        home_block.setVisibility(View.VISIBLE);
        linear_recycle_block.setVisibility(View.VISIBLE);

        // 웹뷰 세팅값
        webSettingMethod(web_q1);
        webSettingMethod(web_q2);
        webSettingMethod(web_q3);
        webSettingMethod(web_q4);
        webSettingMethod(web_q5);

        // 슬라이딩뷰 설정
        slidingViewSet();

        // 잠깐 테스트용, 지울것
        img_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobSchedulerStart.start(getContext());
                Log.d(TAG, "job_start");

            }
        });
        txt_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
                dispatcher.cancelAll();
                Log.d(TAG, "job_stop");
            }
        });

        // now date
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        year = date.getYear() + 2000 - 100;
        month = date.getMonth() + 1;
        day = date.getDate();
        day1 = day + 1;
        day2 = day + 2;
        nowHour = date.getHours();
        nowMinute = date.getMinutes();
        txt_date.setText(year + "년 " + month + "월 " + day + "일");

        // GPS가 꺼져잇으면 켜도록 유도
        chkGpsService();

        linearLayoutMain.bringToFront();

        /** GPS 연동을 위한 권한 체크 및 위치정보 찾기 */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            // 내위치 검색
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
            }
        }

        ArrayList<String> dataList1 = new ArrayList<>();
        ArrayList<String> dataList2 = new ArrayList<>();
        ArrayList<String> dataList3 = new ArrayList<>();
        ArrayList<String> allDataList = new ArrayList<>();

        /** 오늘 날짜 */
        // splitData[0] : 연도,
        // splitData[1] : 월,
        // splitData[2] : 일,
        // splitData[3] : 시작시간,
        // splitData[4] : 시작 분,
        // splitData[5] : 끝 시간,
        // splitData[6] : 끝 분,
        // splitData[7] : 치료 종류
        Log.d(TAG, "onCreateView: year=" + year + "month=" + month + "day=" + day);
        try {
            linear_home_loading.setVisibility(View.VISIBLE);
            mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                    .child("Calendar")
                    .child(year + "/" + month + "/" + day)
                    .child("Therapy_schedule")
                    .child("data_save")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            String dataAll = data.get("data_save").toString();

                            final String[] splitData = dataAll.split(":");

                            Log.d(TAG, "onChildAdded: nowTIme : " + nowHour + ":" + nowMinute);
                            if (Integer.parseInt(splitData[5]) > nowHour) {
                                dataList1.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);
                            } else if (Integer.parseInt(splitData[5]) == nowHour && Integer.parseInt(splitData[6]) >= nowMinute) {
                                dataList1.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);
                            } else {

                            }

                            Log.d(TAG, "onDataChange: day1" + dataList1.size());

                            for (int i = 0; i < dataList1.size(); i++) {
                                if (!allDataList.contains(dataList1.get(i))) {
                                    allDataList.add(dataList1.get(i));

                                    Collections.sort(allDataList);
                                }
                            }

                            Log.d(TAG, "alldata=" + allDataList);

                            ArrayList<HomeMonth_CardItem> cardItemsList = new ArrayList<>();
                            homeMonthScheduleAdapter = new HomeMonthSchedule_Adapter(cardItemsList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerViewMonth.setLayoutManager(linearLayoutManager);
                            homeMonthScheduleAdapter.notifyDataSetChanged();

                            if (!isRunning1) {
                                Log.d(TAG, "onChildAdded: 1번호출");
                                for (int j = 0; j < allDataList.size(); j++) {
                                    try {
                                        isRunning1 = true;
                                        final HomeMonth_CardItem cardItem = new HomeMonth_CardItem(allDataList.get(j).substring(16),
                                                allDataList.get(j).substring(6, 8),
                                                allDataList.get(j).substring(8, 10) + ":" + allDataList.get(j).substring(10, 12),
                                                allDataList.get(j).substring(12, 14) + ":" + allDataList.get(j).substring(14, 16));
                                        cardItemsList.add(cardItem);
                                        recyclerViewMonth.removeAllViewsInLayout();
                                        recyclerViewMonth.setAdapter(homeMonthScheduleAdapter);
                                        linear_home_loading.setVisibility(View.INVISIBLE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            /** 내일 날짜 */
            mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                    .child("Calendar")
                    .child(year + "/" + month + "/" + day1)
                    .child("Therapy_schedule")
                    .child("data_save")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            String dataAll = data.get("data_save").toString();

                            final String[] splitData = dataAll.split(":");

                            dataList2.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);

                            Log.d(TAG, "onDataChange: day2" + dataList2);

                            for (int i = 0; i < dataList2.size(); i++) {
                                if (!allDataList.contains(dataList2.get(i))) {
                                    allDataList.add(dataList2.get(i));

                                    Collections.sort(allDataList);
                                }
                            }

                            ArrayList<HomeMonth_CardItem> cardItemsList = new ArrayList<>();
                            homeMonthScheduleAdapter = new HomeMonthSchedule_Adapter(cardItemsList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerViewMonth.setLayoutManager(linearLayoutManager);
                            homeMonthScheduleAdapter.notifyDataSetChanged();

                            Log.d(TAG, "2번호출");
                            for (int j = 0; j < allDataList.size(); j++) {
                                try {
                                    final HomeMonth_CardItem cardItem = new HomeMonth_CardItem(allDataList.get(j).substring(16),
                                            allDataList.get(j).substring(6, 8),
                                            allDataList.get(j).substring(8, 10) + ":" + allDataList.get(j).substring(10, 12),
                                            allDataList.get(j).substring(12, 14) + ":" + allDataList.get(j).substring(14, 16));
                                    cardItemsList.add(cardItem);
                                    recyclerViewMonth.removeAllViewsInLayout();
                                    recyclerViewMonth.setAdapter(homeMonthScheduleAdapter);
                                    linear_home_loading.setVisibility(View.INVISIBLE);
                                    isRunning2 = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            /** 모레 날짜 */
            mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                    .child("Calendar")
                    .child(year + "/" + month + "/" + day2)
                    .child("Therapy_schedule")
                    .child("data_save")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            String dataAll = data.get("data_save").toString();

                            final String[] splitData = dataAll.split(":");

                            dataList3.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);

                            Log.d(TAG, "onDataChange: day3" + dataList3.size());
                            Log.d(TAG, "allDataList = " + allDataList.size());

                            for (int i = 0; i < dataList3.size(); i++) {
                                if (!allDataList.contains(dataList3.get(i))) {
                                    allDataList.add(dataList3.get(i));
                                    Collections.sort(allDataList);
                                }
                            }

                            ArrayList<HomeMonth_CardItem> cardItemsList = new ArrayList<>();
                            homeMonthScheduleAdapter = new HomeMonthSchedule_Adapter(cardItemsList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerViewMonth.setLayoutManager(linearLayoutManager);
                            homeMonthScheduleAdapter.notifyDataSetChanged();

                            Log.d(TAG, "3번호출");

                            for (int j = 0; j < allDataList.size(); j++) {
                                try {
                                    final HomeMonth_CardItem cardItem = new HomeMonth_CardItem(allDataList.get(j).substring(16),
                                            allDataList.get(j).substring(6, 8),
                                            allDataList.get(j).substring(8, 10) + ":" + allDataList.get(j).substring(10, 12),
                                            allDataList.get(j).substring(12, 14) + ":" + allDataList.get(j).substring(14, 16));
                                    cardItemsList.add(cardItem);
                                    recyclerViewMonth.removeAllViewsInLayout();
                                    recyclerViewMonth.setAdapter(homeMonthScheduleAdapter);
                                    linear_home_loading.setVisibility(View.INVISIBLE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            // 메인 슬라이딩 뷰 동작
            sl_main.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    // 슬라이딩 뷰가 올라오면 Parent뷰로 만들고, 내려가면 캘린더뷰가 Parent 뷰가 되도록
                    if (newState.toString().equals("DRAGGING")) {
                        sl_main.bringToFront();
                    }
                    if (newState.toString().equals("EXPANDED")) {
                        sl_main.bringToFront();
                    } else if (newState.toString().equals("COLLAPSED")) {
                        linearLayoutMain.bringToFront();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 날씨정보 받아오기
     */
    public void findWeather() {
        //open weather api 받아오기
        String Url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=27b1b8b908d5ad361af19ff8eee92989";
        //JSON형태로 저장된 url 받아오기
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // JSON 파일 받아올 종류 선정
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject wind_object = response.getJSONObject("wind");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getInt("humidity"));
                    String descroption = object.getString("description");
                    String wind = String.valueOf(wind_object.getString("speed"));
                    String city = response.getString("name");

                    txt_wind.setText(wind);

                    //humidity 소숫점 제거
                    //받아온 데이터를 어떻게 output해줄지 결정
                    if (Integer.parseInt(humidity) > 100) {
                        humidity = "100";
                    }
                    txt_humidity.setText(humidity);

                    Log.d(TAG, "description : " + descroption);
                    //description(날씨)에 따라 그림 변화
                    // 안개
                    if (descroption.equals("haze")
                            || descroption.equals("smoke")
                            || descroption.equals("mist")
                            || descroption.equals("fog")
                            || descroption.equals("sand, dust whirl")
                            || descroption.equals("sand")
                            || descroption.equals("dust")
                            || descroption.equals("volcanic ash")
                            || descroption.equals("squalls")
                            || descroption.equals("tornado")) {
                        img_weather.setImageResource(R.drawable.fog_icon);
                        txt_weather.setText("흐림");
                        //맑음
                    } else if (descroption.equals("clear sky")) {
                        img_weather.setImageResource(R.drawable.sunny_icon);
                        txt_weather.setText("맑음");
                        //눈
                    } else if (descroption.equals("light snow")
                            || descroption.equals("snow")
                            || descroption.equals("sleet")
                            || descroption.equals("shower sleet")
                            || descroption.equals("light rain and snow")
                            || descroption.equals("rain and snow")
                            || descroption.equals("light shower snow")
                            || descroption.equals("shower snow")
                            || descroption.equals("heavy shower snow")) {
                        img_weather.setImageResource(R.drawable.snow_icon);
                        txt_weather.setText("눈 내림");
                        // 구름
                    } else if (descroption.equals("scattered clouds")
                            || descroption.equals("few clouds")
                            || descroption.equals("broken clouds")
                            || descroption.equals("overcast clouds")) {
                        img_weather.setImageResource(R.drawable.cloudy_icon);
                        txt_weather.setText("구름 많음");
                        // 비
                    } else if (descroption.equals("thunderstorm")
                            || descroption.equals("thunderstorm with light rain")
                            || descroption.equals("thunderstorm with rain")
                            || descroption.equals("thunderstorm with heavy rain")
                            || descroption.equals("light thunderstorm")
                            || descroption.equals("heavy thunderstorm")
                            || descroption.equals("ragged thunderstorm")
                            || descroption.equals("thunderstorm with drizzle")
                            || descroption.equals("thunderstorm with light drizzle")
                            || descroption.equals("thunderstorm with heavy drizzle")
                            || descroption.equals("light intensity drizzle")
                            || descroption.equals("heavy intensity shower rain")) {
                        img_weather.setImageResource(R.drawable.thunder_icon);
                        txt_weather.setText("폭우");
                    } else if (descroption.equals("light rain")
                            || descroption.equals("moderate rain")
                            || descroption.equals("shower rain")
                            || descroption.equals("intensity shower rain")
                            || descroption.equals("light intensity shower rain")) {
                        img_weather.setImageResource(R.drawable.heavy_rain_icon);
                        txt_weather.setText("비");

                    }

                    //일자
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat adf = new SimpleDateFormat("EEEE-DD-MM");

                    //화씨로 표시된 온도를 도씨로 변경
                    double temp_int = Double.parseDouble(temp);
                    double centi = temp_int - 273.15;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    txt_temp.setText(String.valueOf(i));

                    avi_home_weather.hide();
                    home_block.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "지역 설정을 다시 해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        /* 이전 응답 결과가 있어도 새로운 결과를 매 번 보여주기(캐싱 없애기)
        jor.setShouldCache(false);
        */
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);
    }

    /**
     * 위치 정보 리스너
     */
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();

            Log.d(TAG, "latitude = " + latitude + "  longitude = " + longitude);

            latitude = now_latitude;
            longitude = now_longitude;

            // 프래그먼트가 한 번 열릴때 한 번만 탐색
            if (findWeatherFlag) {
                getAddress(longitude, latitude);
                findWeatherFlag = false;
            }
            try {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                // 권한 체크
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    /**
     * 네이버 Geocoding
     */
    private void getAddress(final double longitude, final double latitude) {

        new Thread(new Runnable() {
            String clientId = "96vy6chg9j";// 애플리케이션 클라이언트 아이디값";
            String clientSecret = "35kkhOIGhqnCir1PDTlMlP1A9LM8J0D34W0L2Ll4";// 애플리케이션 클라이언트 시크릿값";
            String json = null;

            @Override
            public void run() {
                try {
                    Log.d(TAG, "getPointFromNaver: 진행중");

                    String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + longitude + "," + latitude + "&sourcecrs=epsg:4326&orders=legalcode,admcode,addr,roadaddr&output=json"; // json
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    // 헤더부분 입력
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

                    // request 코드가 200이면 정상적으로 호출된다고 나와있다.
                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "response code:" + responseCode);

                    BufferedReader br = null;

                    if (responseCode == 200) { // 정상 호출
                        Log.d(TAG, "getPointFromNaver: 정상호출");
                        //정상적으로 호출이 되면 읽어온다.
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else { // 에러 발생
                        Log.d(TAG, "getPointFromNaver: 비정상호출");
                    }

                    // json으로 받아내는 코드!
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    //한 줄 한 줄 읽어들임
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    json = response.toString();

                    // json값이 만약 null값이면 return시킴
                    if (json == null) {
                        return;
                    }

                    //이제 그 결과값 json이 잘 변환되어 들어왔는지 로그를 찍어서 확인해본다.
                    Log.d("TEST2", "json => " + json);

                    // json형식의 데이터를 String으로 변환하는 과정
                    JSONObject jsonObject = new JSONObject(json);

                    // results는 대괄호 []로 감싸져있다. -> Array변환
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    JSONObject jsonObject1 = resultsArray.getJSONObject(0);

                    //이제 배열중 region에 area값들이 들어있기 때문에 중괄호 {}로 감싸진 region값을 가져온다.
                    JSONObject dataObject = (JSONObject) jsonObject1.get("region");

                    // region에서 area1, area2, area3, area4를 각각 또 불러와야한다.
                    JSONObject area1Object = (JSONObject) dataObject.get("area1");
                    JSONObject area2Object = (JSONObject) dataObject.get("area2");
                    JSONObject area3Object = (JSONObject) dataObject.get("area3");
                    JSONObject area4Object = (JSONObject) dataObject.get("area4");

                    Log.d(TAG, "area1 name : " + area1Object.getString("name") + area2Object.getString("name") + area3Object.getString("name") + area4Object.getString("name"));

                    // 각각 불러온 객체에서 원하는 name 값을 가져오면 끝( area1, area2, area3, area4 는 final 전역변수로 지정
                    area1 = area1Object.getString("name");
                    area2 = area2Object.getString("name");
                    area3 = area3Object.getString("name");
                    area4 = area4Object.getString("name");

                    // 이제 추출한 데이터를 가지고 Ui 변경하기 위해 handler 사용
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // 주소 받아오면 처리 메시지
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            txt_location.setText(area1 + " " + area2 + " " + area3 + " " + area4);
            cityName = koreanAddressToEng(area2);
            try {
                findWeather();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 영어로 주소 변환
    private String koreanAddressToEng(String koreanAddress) {

        String engAddress;

        engAddress = koreanAddress.replace("원주시", "Wonju")
                .replace("강릉시", "Gangneung")    // 강원도
                .replace("동해시", "Donghae")
                .replace("속초시", "Sokcho")
                .replace("춘천시", "Chuncheon")
                .replace("태백시", "Taebaek")
                .replace("고성군", "Goseong")
                .replace("양구군", "Yanggu")
                .replace("양양군", "Yangyang")
                .replace("영월군", "Yeongwol")
                .replace("인제군", "Inje")
                .replace("정선군", "Jeongseon")
                .replace("철원군", "Cheorwon")
                .replace("평창군", "Pyeongchang")
                .replace("홍천군", "Hongcheon")
                .replace("화천군", "Hwacheon")
                .replace("횡성군", "Hoengseong");

        return engAddress;
    }

    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(getContext());
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    // 슬라이딩 뷰 위계질서
    private void slidingViewSet() {
        sl_main = view.findViewById(R.id.sliding_layout_home);
        sl_q1 = view.findViewById(R.id.Q1);
        sl_q2 = view.findViewById(R.id.Q2);
        sl_q3 = view.findViewById(R.id.Q3);
        sl_q4 = view.findViewById(R.id.Q4);
        sl_q5 = view.findViewById(R.id.Q5);

        web_q1.onPause();
        web_q2.onPause();
        web_q3.onPause();
        web_q4.onPause();
        web_q5.onPause();

        sl_q1.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.toString().equals("DRAGGING")) {
                    sl_q1.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sl_q1.bringToFront();
                    web_q1.loadUrl("https://www.youtube.com/watch?v=A9c7KW8ePVE");
                    web_q1.onResume();
                } else if (newState.toString().equals("COLLAPSED")) {
                    sl_q1.bringToFront();
                    sl_q2.bringToFront();
                    sl_q3.bringToFront();
                    sl_q4.bringToFront();
                    sl_q5.bringToFront();
                    web_q1.onPause();
                }
            }
        });

        sl_q2.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.toString().equals("DRAGGING")) {
                    sl_q2.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sl_q2.bringToFront();
                    web_q2.loadUrl("https://www.youtube.com/watch?v=2XQo8N72YII");
                    web_q2.onResume();
                } else if (newState.toString().equals("COLLAPSED")) {
                    sl_q1.bringToFront();
                    sl_q2.bringToFront();
                    sl_q3.bringToFront();
                    sl_q4.bringToFront();
                    sl_q5.bringToFront();
                    web_q2.onPause();
                }
            }
        });
        sl_q3.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.toString().equals("DRAGGING")) {
                    sl_q3.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    web_q3.onResume();
                    web_q3.loadUrl("https://www.youtube.com/watch?v=dCAZ2fio2BA");
                    sl_q3.bringToFront();
                } else if (newState.toString().equals("COLLAPSED")) {
                    sl_q1.bringToFront();
                    sl_q2.bringToFront();
                    sl_q3.bringToFront();
                    sl_q4.bringToFront();
                    sl_q5.bringToFront();
                    web_q3.onPause();
                }
            }
        });
        sl_q4.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.toString().equals("DRAGGING")) {
                    sl_q4.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sl_q4.bringToFront();
                    web_q4.loadUrl("https://www.youtube.com/watch?v=fiNHTC6LSg0");
                    web_q4.onResume();
                } else if (newState.toString().equals("COLLAPSED")) {
                    sl_q1.bringToFront();
                    sl_q2.bringToFront();
                    sl_q3.bringToFront();
                    sl_q4.bringToFront();
                    sl_q5.bringToFront();
                    web_q4.onPause();
                }
            }
        });
        sl_q5.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.toString().equals("DRAGGING")) {
                    sl_q5.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sl_q5.bringToFront();
                    web_q5.loadUrl("https://www.youtube.com/watch?v=nbTFiG2aclk");
                    web_q5.onResume();
                } else if (newState.toString().equals("COLLAPSED")) {
                    sl_q1.bringToFront();
                    sl_q2.bringToFront();
                    sl_q3.bringToFront();
                    sl_q4.bringToFront();
                    sl_q5.bringToFront();
                    web_q5.onPause();
                }
            }
        });
    }

    private void webSettingMethod(WebView webView) {
        WebSettings webSettings = webView.getSettings();

        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        //webset.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
