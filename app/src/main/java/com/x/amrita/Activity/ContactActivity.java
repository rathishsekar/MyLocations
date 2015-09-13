package com.x.amrita.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.x.amrita.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactActivity extends ActionBarActivity {
    //Contact con;
    ArrayList<HashMap<String, String>> contactsList;
    EditText content;
    //DatabaseHandler db;
    ListView list;
    private ProgressDialog pDialog;
    private Button post;
    String value;

    public ContactActivity() {
        ArrayList arrayList;
        this.contactsList = arrayList = new ArrayList();
    }

    public void call() {
        SimpleAdapter simpleAdapter = new SimpleAdapter((Context)this, this.contactsList, R.layout.contact_list, new String[]{"name", "phone",
        "photo"}, new int[]{R.id.name, R.id.phone, R.id.photo});
        this.list.setAdapter((ListAdapter)simpleAdapter);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_contacts);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        this.list = (ListView)this.findViewById(R.id.list);
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String string1 = cursor.getString(cursor.getColumnIndex("display_name"));
            String string2 = cursor.getString(cursor.getColumnIndex("data1"));
            String string3 = cursor.getString(cursor.getColumnIndex("photo_uri"));
            HashMap hashMap = new HashMap();
            hashMap.put("name", string1);
            hashMap.put("phone", string2);
            hashMap.put("photo", string3);
            this.contactsList.add(hashMap);
        }
        /*ArrayList<HashMap<String, String>> arrayList = this.contactsList;
        Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String, String>>(){

            public int compare(HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
                return (hashMap.get("name")).compareTo(hashMap2.get("name"));
            }
        };
        Collections.sort(arrayList, (Comparator)comparator);
        Bundle bundle2 = this.getIntent().getExtras();
        if (bundle2 != null) {
            this.value = bundle2.getString("id");
        }*/
        this.call();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ContactActivity.this,MapsActivity.class);
                HashMap<String,String> h = contactsList.get(position);
                i.putExtra("nam", h.get("name"));
                i.putExtra("ph", h.get("phone"));
                startActivity(i);
            }
        });
    }


}

