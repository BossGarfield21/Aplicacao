package pt.novaleaf.www.maisverde;

import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Evento implements Serializable{

    public String name;
    public String creator;
    public long creationDate;
    public long meetupDate;
    public long endDate;
    public String image_uri;
    public List<String> interests;
    public List<String> confirmations;
    public List<String> admin;
    public List<LatLng> area;
    public double radious;
    public double meetupPointLatitude;
    public double meetupPointLongitude;
    public double centerPointLongitude;
    public double centerPointLatitude;
    public String id;
    public String location;
    public String alert;
    public String description;
    public String weather; //??
    private byte[] bitmap;
    private int imageID;

    public boolean interesse;
    public boolean ir;

    public Evento(String name, String creator, long creationDate, long meetupDate, long endDate, List<String> interests,
                  List<String> confirmations, List<String> admin, String id, String location, String alert, String description,
                  String weather, String image_uri, double meetupPointLatitude, double meetupPointLongitude,
                  double centerPointLatitude, double centerPointLongitude, double radious, boolean ir, boolean interesse,
                  List<LatLng> area) {

        this.name = name;
        this.creator = creator;
        this.creationDate = creationDate;
        this.meetupDate = meetupDate;
        this.endDate = endDate;
        this.interests = interests;
        this.confirmations = confirmations;
        this.admin = admin;
        this.id = id;
        this.location = location;
        this.alert = alert;
        this.description = description;
        this.weather = weather;
        this.image_uri = image_uri;
        this.centerPointLatitude = centerPointLatitude;
        this.centerPointLongitude = centerPointLongitude;
        this.meetupPointLatitude = meetupPointLatitude;
        this.meetupPointLongitude = meetupPointLongitude;
        this.area = area;
        this.radious = radious;
        this.ir = ir;
        this.interesse = interesse;
        this.bitmap = null;
        this.imageID = 0;
    }

    public List<LatLng> getArea() {
        return area;
    }

    public double getRadious() {
        return radious;
    }

    public double getCenterPointLatitude() {
        return centerPointLatitude;
    }

    public double getMeetupPointLatitude() {
        return meetupPointLatitude;
    }

    public double getCenterPointLongitude() {
        return centerPointLongitude;
    }

    public double getMeetupPointLongitude() {
        return meetupPointLongitude;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getMeetupDate() {
        return meetupDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public List<String> getInterests() {
        return interests;
    }

    public List<String> getConfirmations() {
        return confirmations;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getAlert() {
        return alert;
    }

    public String getDescription() {
        return description;
    }

    public void setIr() {
        ir = !ir;
    }

    public void setInteresse() {
        interesse = !interesse;
    }

    public boolean isInteresse() {
        return interesse;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getWeather() {
        return weather;
    }

    public boolean isIr() {
        return ir;
    }

    public int getImageID() {
        return imageID;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        Evento evento = (Evento) obj;
        return this.getId().equals(evento.getId());
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
