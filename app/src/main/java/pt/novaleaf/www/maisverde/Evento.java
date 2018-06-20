package pt.novaleaf.www.maisverde;

import java.util.List;

public class Evento {

    public String name;
    public String creator;
    public long creationDate;
    public long meetupDate;
    public long endDate;
    public List<String> interests;
    public List<String> confirmations;
    public List<String> admin;
    public String id;
    public String location;
    public String alert;
    public String description;
    private String weather;
    private String image_uri;
    public boolean interesse;
    public boolean ir;

    public Evento(String name, String creator, long creationDate, long meetupDate, long endDate, List<String> interests,
                  List<String> confirmations, List<String> admin, String id, String location, String alert, String description,
                  String weather, String image_uri){

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

    public void setIr(){
        ir = !ir;
    }

    public void setInteresse(){
        interesse = !interesse;
    }

    public boolean isInteresse(){
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


}
