package com.example.myspot;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.StringValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng DEFAULT_LOCATION = new LatLng(40.6250129,22.9601085);
    private static final int DEFAULT_ZOOM = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Marker marker;

    private Location mLastKnownLocation;

    private FloatingActionButton addButton, locationButton, markerButton;
    private Animation fabOpen, fabClose, fabCW, fabCounterCW;
    private TextView markerLabel, locationLabel;
    private boolean isMenuOpen = false;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    //format to display only two digits
    DecimalFormat df = new DecimalFormat();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        DB.createAndOrLoadDB(getBaseContext());

        printKeyHash();
        Intent notif_intent = getIntent();

        // check if you are coming from notification
        if(notif_intent.getExtras()!=null){

            if(notif_intent.getExtras().getBoolean("Alarm",false)){

                // get values from latest parking
                Parking latestParking = DB.getLatestParking();
                double lat = latestParking.getLocation().latitude;
                double lng = latestParking.getLocation().longitude ;
                Calendar time = latestParking.getTime();
                String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
                String month = String.valueOf(time.get(Calendar.MONTH)+1);

                int hour = time.get(Calendar.HOUR_OF_DAY);
                String strHour  ;
                if (hour < 10)
                    strHour = "0" +hour;
                else
                    strHour = String.valueOf(hour);

                int minute = time.get(Calendar.MINUTE);
                String strMinute  ;
                if (minute < 10)
                    strMinute = "0" + minute;
                else
                    strMinute = String.valueOf(minute);

                String strTime = day + "/" + month + "  " + strHour + ":" + strMinute;

                // upload to firebase
                Spot spot = new Spot();
                spot.setLatitude(lat);
                spot.setLongitude(lng);
                spot.setTime(strTime);
                spot.setAddress(getAddres(lat,lng));
                new FirebaseDatabaseHelper().addSpot(spot, new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<Spot> spots, List<String> keys) {

                    }

                    @Override
                    public void DataIsInserted() {
                        Toast.makeText(MapsActivity.this,"The spot is free"
                        ,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void DataIsUpdated() {

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });


                //Init FB share
                FacebookSdk.sdkInitialize(this.getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(this);

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("Parking spot at:")
                        .setContentUrl(Uri.parse("https://maps.google.com/?q="+lat+","+lng))
                        .build();

                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);

                }
            }

        }


        Toolbar mapToolbar = findViewById(R.id.mapToolbar);
        setSupportActionBar(mapToolbar);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // set on click listener to floatingAddButton. Expands menu and calls alarm activity
        addButton = findViewById(R.id.floatingAddButton);
        locationButton = findViewById(R.id.floatingLocationButton);
        markerButton = findViewById(R.id.floatingMarkerButton);

        markerLabel = findViewById(R.id.markerLabel);
        locationLabel = findViewById(R.id.locationLabel);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fabCounterCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open menu and get get location
                if(isMenuOpen){
                    locationLabel.setVisibility(View.INVISIBLE);
                    markerLabel.setVisibility(View.INVISIBLE);
                    locationButton.startAnimation(fabClose);
                    locationButton.setClickable(false);
                    markerButton.startAnimation(fabClose);
                    markerButton.setClickable(false);
                    addButton.startAnimation(fabCounterCW);
                    isMenuOpen = false;
                } else{
                    locationLabel.setVisibility(View.VISIBLE);
                    markerLabel.setVisibility(View.VISIBLE);
                    locationButton.startAnimation(fabOpen);
                    locationButton.setClickable(true);
                    markerButton.startAnimation(fabOpen);
                    markerButton.setClickable(true);
                    addButton.startAnimation(fabCW);
                    isMenuOpen = true;
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(MapsActivity.this, AlarmActivity.class);
                intent.putExtra("latitude", mLastKnownLocation.getLatitude())
                        .putExtra("longitude",mLastKnownLocation.getLongitude());

                startActivity(intent);
            }
        });

        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MapsActivity.this, AlarmActivity.class);
                intent.putExtra("latitude", marker.getPosition().latitude)
                        .putExtra("longitude",marker.getPosition().longitude);

                startActivity(intent);
            }
        });

        //format to display only two decimal places for price in marker title
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    }

    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.myspot",
                    PackageManager.GET_SIGNATURES);

            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("kostas",""+ Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        Parking latestParking = DB.getLatestParking();

        // Add a marker for the most recent parking
        if (latestParking != null){
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latestParking.getLocation())
                    .title(df.format(latestParking.getFinalCost()) + "€")
                    .snippet("Duration: " + latestParking.getDuration() + " minutes"));
        } else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(DEFAULT_LOCATION));
        }

        //move marker on long click
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),DEFAULT_ZOOM));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_saved_spot:
//                Toast.makeText(this, "Saved Spot selected", Toast.LENGTH_SHORT).show();
                Parking latestParking = DB.getLatestParking();
                if (latestParking != null){

                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latestParking.getLocation())
                            .title(df.format(latestParking.getFinalCost()) + "€")
                            .snippet("Duration: " + latestParking.getDuration() + " minutes"));
                } else {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(DEFAULT_LOCATION));
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),DEFAULT_ZOOM));

                return true;
            case R.id.action_available_spots:
                Intent s_intent = new Intent(MapsActivity.this, FreeSpotsActivity.class);

                startActivity(s_intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            Toast.makeText(getApplicationContext(), mLastKnownLocation.getLatitude() + " " + mLastKnownLocation.getLongitude(),Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(getApplicationContext(), "Current location is null. Using defaults.", Toast.LENGTH_SHORT);
                            Log.d("LOCATION", "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                mLocationPermissionGranted = false;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        updateLocationUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public String getAddres(Double latitude ,Double longitude) {
        //Get address base on location
        try{
            Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {

            }
            else {
                if (addresses.size() > 0) {
                    return addresses.get(0).getFeatureName() + ","
                            + addresses.get(0).getThoroughfare() + ","
                            + addresses.get(0).getLocality() ;

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  "" ;
    }
}
