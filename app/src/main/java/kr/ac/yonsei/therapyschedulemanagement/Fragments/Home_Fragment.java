package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Home_Fragment extends Fragment {

    private static final String TAG = "FRAGMENT1";

    ImageView img_weather;
    TextView txt_date, txt_location, txt_weather;
    TextView txt_temp, txt_humidity;
    double latitude, longitude;
    int year, month, day;

    // 주소정보
    private String area1, area2, area3, area4;
    private String cityName;

    public static Home_Fragment newInstance() {
        Home_Fragment f = new Home_Fragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        img_weather = view.findViewById(R.id.img_weather);
        txt_date = view.findViewById(R.id.txt_date);
        txt_weather = view.findViewById(R.id.txt_weather_information);
        txt_location = view.findViewById(R.id.txt_location);
        txt_temp = view.findViewById(R.id.txt_temperature);
        txt_humidity = view.findViewById(R.id.txt_humidity);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        year = date.getYear() + 2000 - 100;
        month = date.getMonth() + 1;
        day = date.getDay();
        txt_date.setText(year + "년 " + month + "월 " + day + "일");

        // GPS 연동을 위한 권한 체크
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            // 내위치 검색
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();

            latitude = now_latitude;
            longitude = now_longitude;

//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    3000,
//                    1,
//                    gpsLocationListener);
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                    3000,
//                    1,
//                    gpsLocationListener);
        }


        getAddress(longitude, latitude);

        return view;
    }

    /** 날씨정보 받아오기 */
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
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getInt("humidity"));
                    String descroption = object.getString("description");
                    String city = response.getString("name");

                    //humidity 소숫점 제거
                    //받아온 데이터를 어떻게 output해줄지 결정
                    if (Integer.parseInt(humidity) > 100) {
                        humidity = "100";
                    }
                    txt_humidity.setText(humidity);

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
                            || descroption.equals("light intensity shower rain")) {
                        img_weather.setImageResource(R.drawable.heavy_rain_icon);
                        txt_weather.setText("가랑비");

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

    /** 위치 정보 리스너  */
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();

            latitude = now_latitude;
            longitude = now_longitude;

            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // 권한 체크
            if (ActivityCompat.checkSelfPermission(getContext(),
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
                    3000,
                    1,
                    gpsLocationListener);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    /** 네이버 Geocoding */
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
            findWeather();
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
}
