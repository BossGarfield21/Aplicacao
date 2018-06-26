package pt.novaleaf.www.maisverde;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Hugo Mochão on 27/04/2018.
 */

public class Ocorrencia {

    boolean liked;
    boolean favorito;
    String hora;
    long latitude;
    long longitude;
    public String id;
    public String name;
    public String owner; //username
    public String description;
    public double risk;
    public String image_uri;
    public List<String> likers;
    public Map<String, Comentario> comments;
    public long likes;
    public long status;
    public long creationDate;
    public String district;
    public String county;
    public String parish;
    public String type;


    public static final List<Ocorrencia> items = new ArrayList<>();

    public Ocorrencia(){

    }

    public Ocorrencia(String name, double risk, String hora, String id, String description,
                      String owner, List<String> likers, Long status,
                      long latitude, long longitude, long likes, String type, String image_uri,
                      Map<String, Comentario> comments, long creationDate, String district, boolean liked){
        this.name = name;
        this.district = district;
        favorito = false;
        this.liked = liked;
        this.risk = risk;
        this.hora = hora;
        this.id = id;
        this.description = description;
        this.owner = owner;
        this.likers = likers;
        this.status = status;
        this.longitude =longitude;
        this.latitude = latitude;
        this.likes = likes;
        this.type = type;
        this.image_uri = image_uri;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public String getName(){
        return name;
    }

    public double getRisk() {
        return risk;
    }

    public String getHora() {
        return hora;
    }

    public void like(){
        if (liked) {
            liked = !liked;
            likes--;
        } else {
            liked = !liked;
            likes++;
        }
    }

    public void favorito(){
        favorito = !favorito;
    }

    public boolean isFavorito(){
        return favorito;
    }

    public boolean isLiked() {
        return liked;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLikes() {
        return likes;
    }

    public long getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public long getStatus() {
        return status;
    }

    public List<String> getLikers() {
        return likers;
    }

    public String getType() {
        return type;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public Map<String, Comentario> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getDistrict() {
        return district;
    }
}
