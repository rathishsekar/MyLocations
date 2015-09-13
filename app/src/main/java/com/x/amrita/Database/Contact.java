package com.x.amrita.Database;

public class Contact {
    int _id;
    String _name;
    double lat;
    double lon;
    String phn;
    public Contact() {
    }

    public Contact(int n, String string, String phn, double lat, double lon) {
        this._id = n;
        this._name = string;
        this.lat = lat;
        this.lon = lon;
        this.phn = phn;
    }

    public Contact(String string, String phn, double lat, double lon) {
        this._name = string;
        this.lat = lat;
        this.lon = lon;
        this.phn = phn;
    }
    public int getID() {
        return this._id;
    }

    public String getName() {return this._name; }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setID(int n) {
        this._id = n;
    }

    public void setName(String string) {this._name = string;}

    public void setLon(double lon) {this.lon = lon;}

    public double getLon() {return this.lon;}

    public void setPhn(String phn) {this.phn = phn;}

    public String getPhn() {return this.phn;}
}
