package pt.novaleaf.www.maisverde;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class Comentario {

    public String id, author, message;
    public String image;
    public long creation_date;
    private int origem;


    public Comentario(String id, String author, String message, String image, long creation_date, int origem) {

        this.id = id;
        this.author = author;
        this.message = message;
        this.image = image;
        this.creation_date = creation_date;
        this.origem = origem;

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
