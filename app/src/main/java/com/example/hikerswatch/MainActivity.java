package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    int locationTaken = 0;
    double oldLatitudeValue = 0.0;
    double newLatitudeValue = 0.0;
    double newLongitudeValue = 0.0;
    double oldLongitudeValue = 0.0;
    TextView latTextView;
    TextView lonTextView;
    TextView accTextView;
    TextView altTextView;
    TextView addressTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = (TextView) findViewById(R.id.latTextView);
        lonTextView = (TextView) findViewById(R.id.lonTextView);
        accTextView = (TextView) findViewById(R.id.accTextView);
        altTextView = (TextView) findViewById(R.id.altTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                newLatitudeValue = location.getLatitude();
                newLongitudeValue = location.getLongitude();

                if(locationTaken!=0){
                    locationTaken = 1;
                }else{
                    if(oldLatitudeValue!=newLatitudeValue || oldLongitudeValue!=newLongitudeValue){
                        updateLocation(location);
                    }
                }

                oldLatitudeValue = newLatitudeValue;
                oldLongitudeValue = newLongitudeValue;


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }


    public void updateLocation(Location ulocation){
        //Log.i("LOCATION : ",ulocation.toString());

        latTextView.setText("Latitude : " + Double.toString(ulocation.getLatitude()));
        lonTextView.setText("Longitude : " + Double.toString(ulocation.getLongitude()));
        accTextView.setText("Accuracy : " + Double.toString(ulocation.getAccuracy()));
        altTextView.setText("Altitude : " + Double.toString(ulocation.getAltitude()));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "Was Not able to find location :( ";

        try {
            List<Address> addresses = geocoder.getFromLocation(ulocation.getLatitude(),ulocation.getLongitude(),1);

            if(addresses != null && addresses.size()>0){
                address = "Address :\n";
                if(addresses.get(0).getThoroughfare() != null){
                    address += addresses.get(0).getThoroughfare() +"\n";
                }
                if(addresses.get(0).getLocality() != null){
                    address += addresses.get(0).getLocality()+"\n";
                }
                if(addresses.get(0).getPostalCode() != null){
                    address += addresses.get(0).getPostalCode()+"\n";
                }
                if(addresses.get(0).getAdminArea() != null){
                    address += addresses.get(0).getAdminArea()+"\n";
                }
                if(addresses.get(0).getPremises() != null){
                    address += addresses.get(0).getPremises()+"\n";
                }
                if(addresses.get(0).getCountryName() != null){
                    address += addresses.get(0).getCountryName()+"\n";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        addressTextView.setText(address);
    }
}