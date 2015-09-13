package com.x.amrita.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.x.amrita.Activity.MapsActivity;
import com.x.amrita.Database.DatabaseHandler;
import com.x.amrita.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by RATHISH on 09-Sep-15.
 */
public class SavedLocationsActivity extends ActionBarActivity {


    DatabaseHandler db;
    ArrayList<HashMap<String, String>> list;
    ListView lv;
    MapsActivity mp;
    int flag=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.savedlocations);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.setting)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
        Intent resultIntent = new Intent(this, MapsActivity.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        db = new DatabaseHandler(getApplicationContext());
        list = new ArrayList<>();
        lv = (ListView)findViewById(R.id.list);
        for(int i =1; i <= db.getContactsCount(); i++){
            HashMap hashMap = new HashMap();
            hashMap.put("name",db.getContact(i).getName());
            hashMap.put("latlon", "" + Math.round(db.getContact(i).getLat()*10000000000.0)/10000000000.0
            + "," + Math.round(db.getContact(i).getLon()*10000000000.0)/10000000000.0);
            list.add(hashMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter((Context)this, this.list, R.layout.location_list, new String[]{"name", "latlon",
                }, new int[]{R.id.name, R.id.latlon});
        lv.setAdapter(simpleAdapter);
        ArrayList<HashMap<String, String>> arrayList = list;
        Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String, String>>(){

            public int compare(HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
                return (hashMap.get("name")).compareTo(hashMap2.get("name"));
            }
        };
        Collections.sort(arrayList, (Comparator) comparator);
        try{
            Intent i = getIntent();
            flag = i.getIntExtra("flag", 0);
            if(flag ==1){
                mp = new MapsActivity();
            }
        }catch (Exception e){}
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag==1){
                String uri = "https://www.google.co.in/maps/@"+db.getContact(position).getLat()+","+db.getContact(position).getLon()+",15z?hl=en";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, db.getContact(position).getName()+"'s location is ");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });

    }

}