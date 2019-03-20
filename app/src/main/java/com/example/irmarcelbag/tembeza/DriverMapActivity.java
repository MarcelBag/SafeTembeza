package com.example.irmarcelbag.tembeza;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//import com.karumi.dexter.listener.single.PermissionListener;
//
//import java.util.List;

import static com.google.firebase.database.FirebaseDatabase.*;

//import com.google.android.gms.maps.CameraUpdate;

// Implementing the Google location so that when when the map is connected and the request have been created
// it get the current location
public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    // Three variables which call make use of the current location
    GoogleApiClient mGoogleApiClient;

    //GoogleApiClient client = new GoogleApiClient.Builder(this)

    //  GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.RECORD_AUDIO
//
//                ).withListener(new MultiplePermissionsListener() {
//            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
//            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
//        }).check();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.isMyLocationEnabled();
        mMap.getUiSettings().setAllGesturesEnabled(true);


        /**
         *Making the application to locate the devices where is located
         * with a lowest accuracy and check it
         */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }
    //Creating the function of buildGoogleApiClient
    protected synchronized void buildGoogleApiClient(){
        // Trying to validate the API Client
        GoogleApiClient bag = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        bag.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
       Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_LONG).show();
        //Now trying to update location
        mLastLocation = location;
   //     LatLng lating = new LatLng(location.getAltitude(), location.getLatitude());
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //Moving the camera(the google location ) when the user (device) is moving
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // trying to specify the use
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


        //Making the firebase connection to save the longitude and latitude of the user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Creating the database reference
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DrversAvailable");

        /* Using GeoFire saving Values to the Database with his own way */
        GeoFire geoFire = new GeoFire(ref); // Database reference with value of geofire
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Request to get the location from seconds to seconds
        mLocationRequest = new LocationRequest();
        //1000 mille second which is one seconds
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        /**
         * We set a priority of the activity which consume a lot of Battery to take the real as possible Accuracy
         */
       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**
         * We test this first otherwise we're going to try the lowest
         */
        // mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /** TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
             */

            return;
        }

       // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        //Making the firebase conncetion to save the longitude and latitude of the user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Creating the database reference
        DatabaseReference ref = getInstance().getReference("DriversAvailable");
        //Using GeoFire saving Values to the Database with his own way
        GeoFire geoFire = new GeoFire(ref); // Database reference with value of
        //when the user is getting out the activity we're removing the location in the database
        geoFire.removeLocation(userId);
    }

}
