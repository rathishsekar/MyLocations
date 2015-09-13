/*
 * Decompiled with CFR 0_92.
 * 
 * Could not load the following classes:
 *  android.content.ContentValues
 *  android.content.Context
 *  android.database.Cursor
 *  android.database.sqlite.SQLiteDatabase
 *  android.database.sqlite.SQLiteDatabase$CursorFactory
 *  android.database.sqlite.SQLiteOpenHelper
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 */
package com.x.amrita.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.x.amrita.Database.Contact;

import java.util.ArrayList;
import java.util.List;

/*
 * Failed to analyse overrides
 */
public class DatabaseHandler
extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "id";
    private static final String NAME = "name";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String PHN = "phn";
    private static final String TABLE_CONTACTS = "contacts";

    public DatabaseHandler(Context context) {
        super(context, "contactsManager", null, 1);
    }

    public void addContact(Contact contact) {
        SQLiteDatabase sQLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, contact.getName());
        contentValues.put(LAT, contact.getLat());
        contentValues.put(LON, contact.getLon());
        contentValues.put(PHN, contact.getPhn());
        sQLiteDatabase.insert("contacts", null, contentValues);
        sQLiteDatabase.close();
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase sQLiteDatabase = this.getWritableDatabase();
        String[] arrstring = new String[]{String.valueOf((int)contact.getID())};
        sQLiteDatabase.delete("contacts", "id = ?", arrstring);
        sQLiteDatabase.close();
    }

    public List<Contact> getAllContacts() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT  * FROM contacts", null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt((String) cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhn(cursor.getString(2));
                contact.setLat(cursor.getDouble(3));
                contact.setLon(cursor.getDouble(4));
                arrayList.add((Object)contact);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public Contact getContact(int n) {
        String[] arrstring;
        String[] arrstring2;
        SQLiteDatabase sQLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query("contacts", arrstring = new String[]{KEY_ID, NAME, PHN, LAT, LON}, "id=?", arrstring2 = new String[]{String.valueOf((int)n)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Contact contact = new Contact(Integer.parseInt((String)cursor.getString(0)), cursor.getString(1), cursor.getString(2),cursor.getDouble(3),cursor.getDouble(4));
        cursor.close();
        return contact;
    }

    public int getContactsCount() {
        return this.getReadableDatabase().rawQuery("SELECT  * FROM contacts", null).getCount();
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE contacts(id INTEGER PRIMARY KEY,name TEXT,phn TEXT,lat DOUBLE,lon DOUBLE)");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int n, int n2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
        this.onCreate(sQLiteDatabase);
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase sQLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", contact.getName());
        //contentValues.put("phone_number", contact.getPhoneNumber());
        String[] arrstring = new String[]{String.valueOf((int)contact.getID())};
        return sQLiteDatabase.update("contacts", contentValues, "id = ?", arrstring);
    }
}

