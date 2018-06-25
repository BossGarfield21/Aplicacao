package pt.novaleaf.www.maisverde;

import org.w3c.dom.Comment;

import java.util.List;
import java.util.Map;

public class Post {

    public String id;
    public String author;
    public String message;
    public String image;
    public List<String> likers;
    public Map<String, Comment> comments;
    public long likes;
    public boolean liked;


    public Post(String id, String author, String message, String image) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.image = image;
        liked = false;
    }

    public String getId() {
        return id;
    }

    public List<String> getLikers() {
        return likers;
    }

    public long getLikes() {
        return likes;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public boolean isLiked() {
        return liked;
    }

    public void like(){
        liked = !liked;
    }

}
