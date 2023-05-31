package com.example.WeatherApp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    RelativeLayout homeRl;
    ProgressBar loadPb;
    TextView cityTv,tempTv,condTv;
    TextInputEditText city_Edt;
    ImageView backIv,ic_Iv,condIv;
    RecyclerView ForecastRv;
    private ArrayList<RVModal> RVModalArrayList;
    private RVAdapter ForecastRVAdapter;
    private LocationManager  locationManager;
    private int PEMISSION_CODE = 1;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        homeRl = findViewById(R.id.RLid);
        loadPb = findViewById(R.id.Loadingid);
        cityTv = findViewById(R.id.citynameid);
        city_Edt = findViewById(R.id.EditCityName);
        backIv = findViewById(R.id.skyid);
        ic_Iv = findViewById(R.id.searchid);
        condIv = findViewById(R.id.IVIconid);
        ForecastRv = findViewById(R.id.RVForecastid);
        tempTv = findViewById(R.id.Tempid);
        condTv = findViewById(R.id.conditionid);
        RVModalArrayList = new ArrayList<>();
        ForecastRVAdapter = new RVAdapter(this,RVModalArrayList);
        ForecastRv.setAdapter(ForecastRVAdapter);
        locationManager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION},PEMISSION_CODE);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);
        ic_Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = city_Edt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter CityName",Toast.LENGTH_SHORT).show();
                }else{
                    cityTv.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PEMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(this,"Permissions Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Please Allow the Permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double latitute, double longitude){
        String cityName = "Not Found";
        Geocoder GCD = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = GCD.getFromLocation(latitute,longitude,10);
            for(Address adr : addresses){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"USER CITY NOT FOUND",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=bf065e24675244dc88561443233105&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        cityTv.setText(cityName);
        RequestQueue  requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadPb.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                RVModalArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    tempTv.setText(temperature+"°C");
                    int IsDay = response.getJSONObject("current").getInt("is_day");
                    String Condition = response.getJSONObject("curret").getJSONObject("condition").getString("text");
                    String ConditionIcon = response.getJSONObject("curret").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(ConditionIcon)).into(condIv);
                    condTv.setText(Condition);
                    if(IsDay == 1){
                        Picasso.get().load("https://images.unsplash.com/photo-1597571063304-81f081944ee8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1036&q=80").into(backIv);
                    }else{
                        Picasso.get().load("https://images.pexels.com/photos/3888585/pexels-photo-3888585.jpeg?auto=compress&cs=tinysrgb&w=400").into(backIv);
                    }
                    JSONObject forecastobj = response.getJSONObject("forecast");
                    JSONObject forecaste0 = forecastobj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecaste0.getJSONArray("hour");
                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");
                        String temper = hourobj.getString("temp_c");
                        String img = hourobj.getJSONObject("condition").getString("icon");
                        String wind = hourobj.getString("wind_kph");
                        RVModalArrayList.add(new RVModal(time,temper,img,wind));

                    }
                    ForecastRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter Valid CityName",Toast.LENGTH_SHORT).show();
            }
        });
    }
}