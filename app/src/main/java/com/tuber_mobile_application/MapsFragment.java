package com.tuber_mobile_application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.tuber_mobile_application.MapsClasses.GetNearByPlaces;
import com.tuber_mobile_application.helper.HelperMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private EditText location_search;
    private GoogleMap mMap;
    private String TAG = "so47492459";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LatLng myLocation;
    private LatLng chosenLocation;
    private LatLng requestedLocation = null;
    private String parcel;
    private Marker currentUserLocationMarker;
    private static int Request_User_Location_Code = 99;
    private double latitude_ = 0;
    private double longitude_ = 0;
    private Bundle bundle;
    public MapsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            String ComingFromType = bundle.getString("ComingFromType");
            switch (ComingFromType){
                case "Location-request":
                case "Location-renegotiation":
                case "Location-bookings": {

                    if(bundle.getString("Parcel") != null)
                        parcel = bundle.getString("Parcel");
                    String bundleLocation = bundle.getString("UserLocation");
                    String[] bundleContants = HelperMethods.separateString(bundleLocation,"_");
                    double bundleLat_ = Double.parseDouble(bundleContants[1]);
                    double bundleLong_ = Double.parseDouble(bundleContants[2]);
                    requestedLocation = new LatLng(bundleLat_,bundleLong_);
                }break;
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
    }
    /**
     *
     *
     */
    private OnMapReadyCallback callback = new OnMapReadyCallback()
    {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               buildGoogleApiClient();
                 /*LatLng southAfrica = new LatLng(-26.2485, 27.8540);
                mMap.addMarker(new MarkerOptions().position(southAfrica).title("Marker in South Africa").draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(southAfrica));
                mMap.setMyLocationEnabled(true);
                */
                //Define list to get all latlng for the route
                findNearByPlaces();
            }

        }
    };

    /**
     *
     */
    protected  synchronized void  buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    /**
     *
     * @return
     */
    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }

            return false;
        }
        else {
            return true;
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 99:

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(getContext(),"Permission Denied....", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_maps, container, false);

        location_search = view.findViewById(R.id.txtLocation);
        final Bundle[] bundle = {this.getArguments()};
        Button btnBackToRequest = view.findViewById(R.id.btnSelectLocation);

        if (bundle[0] != null) {
            String ComingFromType = bundle[0].getString("ComingFromType");
            switch (ComingFromType){
                case "Location-request":
                {
                    btnBackToRequest.setText("Back To Booking Request");
                }break;
                case "Location-renegotiation":
                {
                    btnBackToRequest.setText("Back To renegotiation");
                }break;case "Location-bookings":
                {
                    btnBackToRequest.setText("Back To Bookings");
                }break;
                default:{

                }break;
            }
        }
        btnBackToRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bundle[0] != null) {

                    String ComingFromType = bundle[0].getString("ComingFromType");

                    switch (ComingFromType){
                        case "Location-request":
                        {
                            parcel = bundle[0].getString("Parcel");
                            String bundleLocation = bundle[0].getString("UserLocation");
                            String[] bundleContants = HelperMethods.separateString(bundleLocation,"_");
                            double bundleLat_ = Double.parseDouble(bundleContants[1]);
                            double bundleLong_ = Double.parseDouble(bundleContants[2]);
                            requestedLocation = new LatLng(bundleLat_,bundleLong_);
                            Bundle returnBundle = new Bundle();
                            returnBundle.putString("ComingFromType","Location-request");
                            returnBundle.putString("Parcel",parcel);
                            returnBundle.putString("UserLocation", bundle[0].getString("UserLocation"));
                            returnBundle.putString("moduleName",bundle[0].getString("moduleName"));
                            returnBundle.putString("NotificationType",bundle[0].getString("NotificationType"));
                            //Go Back to the booking request view fragment
                            MultipurposeFragment multipurposeFragment = new MultipurposeFragment();
                            multipurposeFragment.setArguments(returnBundle); // setting arguments for next fragment
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, multipurposeFragment).commit();
                        }break;
                        case "Location-renegotiation":
                        {
                            parcel = bundle[0].getString("Parcel");
                            String bundleLocation = bundle[0].getString("UserLocation");
                            String[] bundleContants = HelperMethods.separateString(bundleLocation,"_");
                            double bundleLat_ = Double.parseDouble(bundleContants[1]);
                            double bundleLong_ = Double.parseDouble(bundleContants[2]);
                            requestedLocation = new LatLng(bundleLat_,bundleLong_);
                            Bundle returnBundle = new Bundle();
                            returnBundle.putString("ComingFromType","Location-request");
                            returnBundle.putString("Parcel",parcel);
                            returnBundle.putString("UserLocation", bundle[0].getString("UserLocation"));
                            returnBundle.putString("moduleName",bundle[0].getString("moduleName"));
                            returnBundle.putString("NotificationType",bundle[0].getString("NotificationType"));
                            returnBundle.putString("otherUserId",bundle[0].getString("otherUserId"));

                            //Go Back to the booking negotiation fragment
                            NegotiationFragment negotiationFragment = new NegotiationFragment();
                            negotiationFragment.setArguments(returnBundle); // setting arguments for next fragment
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, negotiationFragment).commit();
                        }break;
                        case "Location-bookings":
                        {
                            parcel = bundle[0].getString("Parcel");
                            String bundleLocation = bundle[0].getString("UserLocation");
                            String[] bundleContants = HelperMethods.separateString(bundleLocation,"_");
                            double bundleLat_ = Double.parseDouble(bundleContants[1]);
                            double bundleLong_ = Double.parseDouble(bundleContants[2]);
                            requestedLocation = new LatLng(bundleLat_,bundleLong_);

                            //Go Back to the Booked session fragment
                            BookedSessionFragment bookedSessionFragment = new BookedSessionFragment();
                            bookedSessionFragment.setArguments(bundle[0]);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,bookedSessionFragment).commit();
                        }break;
                        default:{

                        }break;
                    }
                }else
                {
                    String loc = location_search.getText().toString();//" (" + currentUserLocationMarker.getPosition().latitude + " , "+ currentUserLocationMarker.getPosition().longitude + ")";
                    bundle[0] = new Bundle();
                    bundle[0].putString("Location",loc + "_" +latitude_+"_" +longitude_);
                    RequestFragment requestFragment = new RequestFragment();
                    requestFragment.setArguments(bundle[0]);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,requestFragment).commit();
                }
            }
        });
        return view;
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            location_search = view.findViewById(R.id.location_search);
            //LatLng northEast = new LatLng(-26.2485, 27.8540);
            //LatLng southWest = new LatLng(-26.2485, 27.8540);
            //RectangularBounds.newInstance(northEast,southWest);
            Places.initialize(getContext(), "AIzaSyAf02KGmhqrFCU9DTbgiKy8dXSgFpb1hx4");
            location_search.setFocusable(false);
            location_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Initialise place field list
                    List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                            ,Place.Field.LAT_LNG,
                            Place.Field.NAME);
                    //Create intent
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(getActivity());
                    startActivityForResult(intent,100);
                }
            });
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        if(currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //Marker Customization
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.draggable(true);
        //Place marker on location
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        //Get The bundle for View the location
        //get the latitude and longitude
        latitude_ = location.getLatitude();
        longitude_ = location.getLongitude();
        myLocation = new LatLng(latitude_,longitude_);
        bundle = this.getArguments();
        if (bundle != null) {
            List<LatLng> path = new ArrayList();
            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAf02KGmhqrFCU9DTbgiKy8dXSgFpb1hx4")
                    .build();
            String ComingFromType = bundle.getString("ComingFromType");

            switch (ComingFromType){
                case "Location-request":
                case "Location-renegotiation":
                case "Location-bookings":
                {
                    String bundleLocation = bundle.getString("UserLocation");
                    String[] bundleConstants = HelperMethods.separateString(bundleLocation,"_");
                    double bundleLat_ = Double.parseDouble(bundleConstants[1]);
                    DirectionsApiRequest req = DirectionsApi.getDirections(context, ""+ myLocation.latitude + ","+ myLocation.longitude, ""+ requestedLocation.latitude + ","+ requestedLocation.longitude);/* */
                    LatLng JohannesBurg = new LatLng(-26.2485, 27.8540);
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.position(requestedLocation);
                    markerOptions2.title(bundleConstants[0]);
                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    markerOptions2.draggable(true);

                    drawPMapPath(path,req,JohannesBurg);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(15));
                    mMap.addMarker(markerOptions2);
                }break;
            }
        }else{
            //Move camera here & zoom on marker
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(16));
        }
        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            //fill the edit text
            location_search.setText(place.getAddress());
            //mMap.re
            LatLng newLocation = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(newLocation);
            markerOptions.title(place.getAddress());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.draggable(true);
            latitude_ = newLocation.latitude;
            longitude_ = newLocation.longitude;
            currentUserLocationMarker = mMap.addMarker(markerOptions);
            chosenLocation = new LatLng(latitude_,longitude_);
            List<LatLng> path = new ArrayList();
            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAf02KGmhqrFCU9DTbgiKy8dXSgFpb1hx4")
                    .build();

            DirectionsApiRequest req = DirectionsApi.getDirections(context, ""+ myLocation.latitude + ","+ myLocation.longitude, ""+ chosenLocation.latitude + ","+ chosenLocation.longitude);/* */
            LatLng JohannesBurg = new LatLng(-26.2485, 27.8540);
            drawPMapPath(path,req,JohannesBurg);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.i("System out", "onMarkerDrag...");
                }
            });
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            // on error
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), status.getStatusMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Algorithm that find and draws the path
     * @param path
     * @param req
     * @param southAfrica
     */
    protected void drawPMapPath( List<LatLng> path, DirectionsApiRequest req, LatLng southAfrica){
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(southAfrica, 6));
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void findNearByPlaces(){
        int radius = 1500;
        String placeType = "restaurant";
        String keyword = "cruise";
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                                                        "location="+ latitude_+","+ longitude_+
                                                        "&radius="+ radius +
                                                        "&type="+ placeType +
                                                        "&keyword="+ keyword +
                                                        "&key=" +R.string.google_maps_key);
        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearByPlaces getNearByPlaces = new GetNearByPlaces(this);
        getNearByPlaces.execute(dataTransfer);
    }
}