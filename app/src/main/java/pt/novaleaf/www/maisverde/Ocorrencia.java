package pt.novaleaf.www.maisverde;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.w3c.dom.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Hugo Mochão on 27/04/2018.
 */

public class Ocorrencia implements Serializable, ClusterItem{

    boolean liked;
    boolean favorito;
    String hora;
    double latitude;
    double longitude;
    public String id;
    public String name;
    public String owner; //username
    public String description;
    public double risk;
    public double radius;
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
    private String user_image;
    private byte bitmap[];
    private byte bitmapUser[];
    private int imageID;
    private int imageIDUser;


    public static final List<Ocorrencia> items = new ArrayList<>();



    public Ocorrencia(String name, double risk, String hora, String id, String description,
                      String owner, List<String> likers, Long status,
                      double latitude, double longitude, long likes, String type, String image_uri,
                      Map<String, Comentario> comments, long creationDate, String district, boolean liked,
                      String user_image, double radius){
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
        this.user_image = user_image;
        bitmap = null;
        bitmapUser = null;
        imageID = 0;
        imageIDUser = 0;
        this.radius = radius;

    }

    public double getRadius() {
        return radius;
    }

    public int getImageIDUser() {
        return imageIDUser;
    }

    public void setImageIDUser(int imageIDUser) {
        this.imageIDUser = imageIDUser;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setBitmapUser(byte[] bitmapUser) {
        this.bitmapUser = bitmapUser;
    }

    public byte[] getBitmapUser() {
        return bitmapUser;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
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

    public double getLatitude() {
        return latitude;
    }

    public long getLikes() {
        return likes;
    }

    public double getLongitude() {
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

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        Ocorrencia ocorrencia = (Ocorrencia) obj;
        return this.getId().equals(ocorrencia.getId());
    }


    public void addComentario(Comentario com) {
        comments.put(com.getId(), com);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

}
