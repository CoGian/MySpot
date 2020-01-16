package com.example.myspot;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng DEFAAULT_LOCATION = new LatLng(40.6250129,22.9601085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private MarkerOptions marker = new MarkerOptions().position(this.DEFAAULT_LOCATION);

    private Location mLastKnownLocation;

    private FloatingActionButton addButton, locationButton, markerButton;
    private Animation fabOpen, fabClose, fabCW, fabCounterCW;
    private TextView markerLabel, locationLabel;
    private boolean isMenuOpen = false;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
                intent = new Intent(MapsActivity.this, AlarmActivity.class)
                        .putExtra("location", new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));

                startActivity(intent);
            }
        });

        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MapsActivity.this, AlarmActivity.class)
                        .putExtra("location", marker.getPosition());

                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Add a marker in UoM
        mMap.addMarker(marker);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                marker = new MarkerOptions().position(latLng);
                mMap.clear();
                mMap.addMarker(marker);
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
                Toast.makeText(this, "Saved Spot selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAAULT_LOCATION, DEFAULT_ZOOM));
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
}
