package com.example.irmarcelbag.tembeza;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

//import static com.google.firebase.database.FirebaseDatabase.getInstance;


// Implementing the Google location so that when when the map is connected and the request have been created
// it get the current location
public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    // Three variables which call make use of the current location
    GoogleApiClient mGoogleApiClient;

    //GoogleApiClient client = new GoogleApiClient.Builder(this)


    Location mLastLocation;
    LocationRequest mLocationRequest;
    private DatabaseReference mDatabase;
    private Button mLogout;
    /* private FusedLocationProviderClient fusedLocationClient; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Setting the button when clicking the logout with firebase's way
        mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                //return;

            }
        });
        //Creating the fuse location
     //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       //fusedLocationClient  = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.isMyLocationEnabled();
        mMap.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }
    //Creating the function of buildGoogleApiClient
    protected synchronized void buildGoogleApiClient(){
        // Trying to validate the API Client
         new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        //bag.connect();
//        new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
    }


    @Override
    public void onLocationChanged(Location location) {
       Toast.makeText(DriverMapActivity.this, "TestLocation "+ location, Toast.LENGTH_LONG).show();
        //Now trying to update location
        mLastLocation = location;
   //     LatLng lating = new LatLng(location.getAltitude(), location.getLatitude());
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //Moving the camera(the google location ) when the user (device) is moving
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // trying to specify the use
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


        //Making the firebase connection to save the longitude and latitude of the user
//        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
       String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Creating the database reference
     // DatabaseReference ref = getInstance().getReference("DriversAvailable");

        mDatabase = (FirebaseDatabase.getInstance().getReference("DriversAvailable")); /* Using GeoFire saving Values to the Database with his own way */

        GeoFire geoFire = new GeoFire(mDatabase); // Database reference with value of geofire
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));

    }
    

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Request to get the location from seconds to seconds
        mLocationRequest = new LocationRequest();
        //1000 mille second which is one seconds
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
       //Setting a low battery consumer accuracy
       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
    //Getting the refreshment of the location instead of getting a location once it will be refresh in a 1000milliseconde which equal to 1 second

     LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    // Knowing when the user is available we call on stop

    @Override
    protected void onStop() {
        super.onStop();
        //Making the firebase connection to save the longitude and latitude of the user
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Creating the database reference
     //DatabaseReference ref = getInstance().getReference("DriversAvailable");
       mDatabase = FirebaseDatabase.getInstance().getReference("DriversAvailable");

        //Using GeoFire saving Values to the Database with his own way
        GeoFire geoFire = new GeoFire(mDatabase); // Database reference with value of
        //when the user is getting out the activity we're removing the location in the database
        geoFire.removeLocation(userId);
    }

}
