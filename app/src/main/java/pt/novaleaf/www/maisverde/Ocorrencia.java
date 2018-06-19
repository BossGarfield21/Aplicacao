package pt.novaleaf.www.maisverde;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hugo Mochão on 27/04/2018.
 */

public class Ocorrencia {

    String titulo;
    int imgId;
    boolean liked;
    boolean favorito;
    int risco;
    String hora;
    long latitude;
    long longitude;
    String id;
    String owner;
    String descricao;
    String image_uri;
    String likers[];
    Map<String, String> comentarios;
    int likes;
    String status;
    String type;


    public static final List<Ocorrencia> items = new ArrayList<>();

    public Ocorrencia(){

    }

    public Ocorrencia(String titulo, int imgId, int risco, String hora, String id, String descricao,
                      String owner, String likers[], Map<String, String> comentarios, String status,
                      long latitude, long longitude, int likes, String type, String image_uri){
        this.titulo = titulo;
        this.imgId = imgId;
        favorito = false;
        liked = false;
        this.risco = risco;
        this.hora = hora;
        this.id = id;
        this.descricao = descricao;
        this.owner = owner;
        this.likers = likers;
        this.comentarios = comentarios;
        this.status = status;
        this.longitude =longitude;
        this.latitude = latitude;
        this.likes = likes;
        this.type = type;
        this.image_uri = image_uri;
    }

    public String getTitulo(){
        return titulo;
    }

    public int getImgId(){
        return imgId;
    }

    public int getRisco() {
        return risco;
    }

    public String getHora() {
        return hora;
    }

    public void like(){
        liked = !liked;
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

    public int getLikes() {
        return likes;
    }

    public long getLongitude() {
        return longitude;
    }

    public Map<String, String> getComentarios() {
        return comentarios;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String[] getLikers() {
        return likers;
    }

    public String getType() {
        return type;
    }

    public String getImage_uri() {
        return image_uri;
    }
}
