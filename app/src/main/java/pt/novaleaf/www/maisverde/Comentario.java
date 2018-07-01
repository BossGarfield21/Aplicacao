package pt.novaleaf.www.maisverde;

import java.io.Serializable;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class Comentario implements Serializable{

    public String id, author, message, markerid;
    public String image;
    public long creation_date;
    private int origem;


    public Comentario(String id, String author, String message, String image, long creation_date, int origem, String markerid) {

        this.id = id;
        this.author = author;
        this.message = message;
        this.image = image;
        this.creation_date = creation_date;
        this.origem = origem;
        this.markerid = markerid;

    }

    public String getMarkerid() {
        return markerid;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public long getCreation_date() {
        return creation_date;
    }

    public int getOrigem() {
        return origem;
    }
}
