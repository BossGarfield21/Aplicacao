package pt.novaleaf.www.maisverde;

import java.io.Serializable;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class Comentario implements Serializable {

    public String id, author, message, markerid;
    public String image;
    public long creation_date;
    private int origem;
    private String postId;
    private String groupId;
    private byte[] bitmap;


    public Comentario(String id, String author, String message, String image, long creation_date,
                      int origem, String markerid, String postId, String groupId) {

        this.id = id;
        this.author = author;
        this.message = message;
        this.image = image;
        this.creation_date = creation_date;
        this.origem = origem;
        this.markerid = markerid;
        this.postId = postId;
        this.groupId = groupId;
        this.bitmap = null;

    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
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

    public void setId(String id) {
        this.id = id;
    }


    public long getCreation_date() {
        return creation_date;
    }

    public int getOrigem() {
        return origem;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getPostId() {
        return postId;
    }

    @Override
    public boolean equals(Object obj) {
        Comentario comentario = (Comentario) obj;
        return this.getId().equals(comentario.getId());
    }
}
