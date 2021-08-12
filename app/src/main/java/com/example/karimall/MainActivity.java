package com.example.karimall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.karimall.fragment.clothings;
import com.example.karimall.fragment.cosmetics;
import com.example.karimall.fragment.shoes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    Spinner spinner;
    Button button;
    double currlat=0,currlng=0;
    GoogleMap googleMap1;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE =101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner=findViewById(R.id.spinner);
        button=findViewById(R.id.search);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        final String[] list={"ATM","POLICE STATION"};
        final String[] type={"ATM","POLICE STATION"};
        spinner.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,list));

        fetchLocation();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=spinner.getSelectedItemPosition();
                Log.d(String.valueOf(currlng), String.valueOf(currlat));
                Log.d("************",type[i]);
                String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        //location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
                        "location="+currlat+","+currlng+
                        "&radius=1000"+
                        "&type"+ type[i]+
                        "key="+getResources().getString(R.string.google_map);

                new PlaceTask().execute(url);
            }
        });

    }

    private void fetchLocation() {
        //Allow permission to access location
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location>task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    currlat=location.getLatitude();
                    currlng=location.getLongitude();
                    //Toast.makeText(getApplicationContext(),currlocation.getLatitude()+""+currlocation.getLongitude(),Toast.LENGTH_LONG).show();
                    SupportMapFragment supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googlemap);
                    assert supportMapFragment!=null;
                    supportMapFragment.getMapAsync(MainActivity.this);

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap1=googleMap;
        LatLng latLng=new LatLng(currlat,currlng);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("my location");
        googleMap1.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        googleMap1.addMarker(markerOptions);
    }

    private class PlaceTask extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            String data =null;
            try {

                data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url=new URL(string);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream=connection.getInputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder=new StringBuilder();
        String line ="";
        while ((line=reader.readLine())!=null){
            builder.append(line);

        }
        String data=builder.toString();
        reader.close();
        return  data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser=new JsonParser();
            List<HashMap<String,String>>mapList=null;
            JSONObject jsonObject=null;
            try {
                jsonObject=new JSONObject(strings[0]);
                mapList=jsonParser.parseResult((jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            googleMap1.clear();
            for (int i=0;i<hashMaps.size();i++){
                HashMap<String,String>hashMapList=hashMaps.get(i);
                double lat= Double.parseDouble(hashMapList.get("lat"));
                double lng= Double.parseDouble(hashMapList.get("lng"));

                String name=hashMapList.get("name");
                LatLng latLng=new LatLng(lat,lng);
                Log.d("************",name);

                MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(name);
                googleMap1.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                googleMap1.addMarker(markerOptions);
            }
        }
    }
}
