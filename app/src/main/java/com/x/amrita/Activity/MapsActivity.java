package com.x.amrita.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.x.amrita.Database.Contact;
import com.x.amrita.Database.DatabaseHandler;
import com.x.amrita.Helper.GPSTracker;
import com.x.amrita.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Location location;
    String name="",phone="",titlenam,value1="",value2="";
    DatabaseHandler db;
    Contact con;
    GPSTracker gps;
    int n=-1 ,set=0;
    String adline="";
    FloatingActionButton actionA, actionB, actionC, actionD;
    View actionD1,actionD2,actionD3;

    public IconGenerator iconGenerator;
    public SharedPreferences myPreference;
    GPSTracker gpsTracker = new GPSTracker(this);
    PolylineOptions polylineOptions;
    public ProgressDialog pDialog;
    ArrayList<LatLng> points;

    SupportMapFragment supportMapFragment;
    LocationManager locManager;
    LatLng userPosition;
    Geocoder gcd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        initialize();
        myPreference= PreferenceManager.getDefaultSharedPreferences(this);
        String myListPreference = myPreference.getString("type_provider", "Default");
        actionD = (FloatingActionButton)findViewById(R.id.action_d);
        actionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionA.setVisibility(actionA.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionC.setVisibility(actionC.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionD1.setVisibility(actionD1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionD2.setVisibility(actionD2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionD3.setVisibility(actionD3.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        actionD1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MapsActivity.this);
                // check if GPS enabled
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    share(latitude ,longitude, "Here is my location - ");
                }else{
                    // can't get location   // GPS or Network is not enabled    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
        actionD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SavedLocationsActivity.class);
                i.putExtra("flag",1);
                startActivity(i);
            }
        });
        actionD3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set=1;
                Toast.makeText(getApplicationContext(),"Click on a location to send ",Toast.LENGTH_LONG).show();
            }
        });


        db = new DatabaseHandler(this);
        try{
            Intent i = getIntent();
            name = i.getStringExtra("nam");
            phone = i.getStringExtra("ph");
            if(name != null)
            Toast.makeText(getApplicationContext(),"Tap somewhere to add a location",Toast.LENGTH_LONG).show();
        }catch (Exception e){}


        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        // Getting a reference to the map
        supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        supportMapFragment.getMap().setPadding(0, 75, 0, 0);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        maptype(myListPreference);
        addmarker();
         points = new ArrayList<>();
            LatLng position = new LatLng(12.99,80.255 );
            points.add(position);
            LatLng position1 = new LatLng(13,80.257 );
            points.add(position1);
            LatLng position2 = new LatLng(13.1,80.215 );
            points.add(position2);

        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions.width(2);
        polylineOptions.color(Color.BLUE);
        Polyline line = mMap.addPolyline(polylineOptions);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (set == 1) {
                    share(latLng.latitude, latLng.longitude, "The Location is ");
                    set = 0;
                } else {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    title(latLng);
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.addMarker(markerOptions);
                    name = null;
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                marker.hideInfoWindow();
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.marker_dialog);
                dialog.setTitle(marker.getTitle());
                TextView t2 = (TextView) dialog.findViewById(R.id.t2);
                t2.setText(marker.getPosition().latitude + "," + marker.getPosition().longitude);
                TextView t4 = (TextView) dialog.findViewById(R.id.t4);
                try {
                    List<Address> addresses = gcd.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    if (addresses.size() > 0)
                        adline = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1) + "\n" + addresses.get(0).getAddressLine(2) + "\n" + addresses.get(0).getAddressLine(3);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                t4.setText(adline);
                Button nav = (Button) dialog.findViewById(R.id.b2);
                nav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+mMap.getMyLocation().getLatitude()+","
                                +mMap.getMyLocation().getLongitude()+"&daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude));
                            dialog.dismiss();
                            startActivity(intent);
                                           }
                                       });

                Button call = (Button) dialog.findViewById(R.id.b1);
                call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                StringBuilder stringBuilder = new StringBuilder();
                                                String string = stringBuilder.append("tel:").append(marker.getSnippet()).toString();
                                                Intent intent = new Intent("android.intent.action.CALL");
                                                intent.setData(Uri.parse((String) string));
                                                dialog.dismiss();
                                                marker.hideInfoWindow();
                                                try {
                                                    startActivity(intent);
                                                } catch (Exception e) {}
                                            }
                                        }

                );
                    dialog.show();
                return false;
                }
            });

            if(mMap!=null)

            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                mMap.setMyLocationEnabled(true);
            }

        }

    @Override
    protected void onResume() {
        super.onResume();
        maptype(myPreference.getString("type_provider", "Default"));
        addmarker();
    }

    public void initialize(){
        actionA = (FloatingActionButton)findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SavedLocationsActivity.class);
                startActivity(i);
            }
        });
        actionB = (FloatingActionButton)findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
        actionC = (FloatingActionButton)findViewById(R.id.action_c);
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, ContactActivity.class);
                startActivity(i);
            }
        });
        actionD1 = findViewById(R.id.action_d1);
        actionD1.setVisibility(View.GONE);
        actionD2 = findViewById(R.id.action_d2);
        actionD2.setVisibility(View.GONE);
        actionD3 = findViewById(R.id.action_d3);
        actionD3.setVisibility(View.GONE);
    }

    public void share(Double latitude,Double longitude,String ShareSub){
        try{
        List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                adline = addresses.get(0).getAddressLine(0)+"\n"+addresses.get(0).getAddressLine(1)+"\n"+addresses.get(0).getAddressLine(2)+"\n"+addresses.get(0).getAddressLine(3);

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

        String uri = "https://www.google.co.in/maps/@"+latitude+","+longitude+",15z?hl=en"+"\n\n address::\n"+adline;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void title(final LatLng latLng){
        Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.title_dialog);
       /* final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);*/
        final EditText nam = (EditText) dialog.findViewById(R.id.e1);
        final EditText ph = (EditText) dialog.findViewById(R.id.e2);
        if(name != null){
            nam.setText(name);
            ph.setText(phone);
        }
        dialog.setTitle("Description for marker..");
        Button save = (Button) dialog.findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con = new Contact(nam.getText().toString(), ph.getText().toString(), latLng.latitude, latLng.longitude);
                db.addContact(con);
                Intent it = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(it);
            }
        });
      /*  dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (n != 0) {
                    value = input.getText().toString();
                }
                con = new Contact(nam.getText().toString(), ph.getText().toString(), latLng.latitude, latLng.longitude);
                db.addContact(con);
                Intent it = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(it);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });*/
        dialog.show();
    }

    public void maptype(String myListPreference){
        if(myListPreference.equals("Hybrid"))
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if(myListPreference.equals("Normal"))
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(myListPreference.equals("Satellite"))
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if(myListPreference.equals("Terrain"))
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void markerColor(String color){
        if(color.equals("Red"))
            iconGenerator.setStyle(IconGenerator.STYLE_RED);
        if(color.equals("Blue"))
            iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        if(color.equals("Green"))
            iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        if(color.equals("Purple"))
            iconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
        if(color.equals("White"))
            iconGenerator.setStyle(IconGenerator.STYLE_WHITE);
        if(color.equals("Orange"))
            iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
    }

    public void addmarker(){
        try{
            Iterator iterator = db.getAllContacts().iterator();
            while (iterator.hasNext()) {
                Contact contact = (Contact) iterator.next();
                StringBuilder stringBuilder = new StringBuilder();
                Log.d("Name: ", stringBuilder.append("Id: ").append(contact.getID()).append(" ,Name: ").append(contact.getName()).
                        append(" ,Phone: ").append(contact.getPhn()).append(contact.getLat()).append(contact.getLon()).toString());

                iconGenerator = new IconGenerator(MapsActivity.this);
                markerColor(myPreference.getString("color_provider", "Default"));
                Bitmap bitmap = iconGenerator.makeIcon(contact.getName());
                mMap.addMarker(new MarkerOptions().position(new LatLng(contact.getLat(), contact.getLon())).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(contact.getName()).snippet(contact.getPhn()));
            }
        }catch (Exception e){
        }
    }

}
