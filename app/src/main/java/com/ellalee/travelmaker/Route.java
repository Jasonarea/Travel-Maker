package com.ellalee.travelmaker;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class Route{
    public int index;
    public PolylineOptions polylineOptions = new PolylineOptions();
    public ArrayList<Marker> markerList;
    public Polyline polyline;
    public String routeColor = new String();

    Route(int idx,String color, GoogleMap map){
        index = idx;
        markerList = new ArrayList<>();
        routeColor = color;
        setPolylineOptions();
        polyline = map.addPolyline(polylineOptions);
        polyline.setClickable(true);
    }
    boolean add(Marker marker){
        return markerList.add(marker);
    }
    boolean remove(Marker marker){
        return markerList.remove(marker);
    }
    boolean contains(Marker marker){
        Iterator<Marker> iterator = markerList.iterator();
        while(iterator.hasNext()){
            if(iterator.equals(marker))
                return true;
        }
        return false;
    }
    int contains(Polyline line){
        if(polyline.equals(line)){
            return index;
        }
        else return -1;
    }
    public void setMarkerList(ArrayList<Marker> markerList) {
        this.markerList = markerList;
    }

    ArrayList<LatLng> toLatLng(ArrayList<Marker> markers){
        Iterator<Marker> iterator = markers.iterator();
        ArrayList<LatLng> LatLngs = new ArrayList<>();

        while(iterator.hasNext()){
            LatLngs.add(iterator.next().getPosition());
        }
        return LatLngs;
    }
    public void setPolylineOptions(){
        polylineOptions.color(Color.parseColor(routeColor));

        this.polylineOptions.width(10);
        this.polylineOptions.addAll(toLatLng(this.markerList));
    }
    public void setPoints(ArrayList<LatLng> latLng){
        polyline.setPoints(latLng);
    }
    public void setPoints(){
        polyline.setPoints(toLatLng(markerList));
    }
    public void highlightPolyline(){
        polylineOptions.width(20);
    }
    public void normalPolyline(){
        polylineOptions.width(10);
    }
}