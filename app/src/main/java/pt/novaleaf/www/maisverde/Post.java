package pt.novaleaf.www.maisverde;

import android.graphics.Bitmap;

import org.w3c.dom.Comment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Post implements Serializable {

    public String id;
    public String groupId;
    public String author;
    public String message;
    public String image;
    public String user_image;
    public List<String> likers;
    public Map<String, Comentario> comments;
    public long likes;
    public boolean liked;
    public long creationDate;
    private byte[] bitmap;
    private byte[] bitmapUser;


    public Post(String id, String author, String message, String image, List<String> likers,
                Map<String, Comentario> comments_map, long likes, boolean liked, String groupId, String user_image) {

        this.id = id;
        this.author = author;
        this.message = message;
        this.image = image;
        this.likers = likers;
        this.comments = comments_map;
        this.likes = likes;
        this.liked = liked;
        this.groupId = groupId;
        this.user_image = user_image;
        bitmap = null;
        bitmapUser = null;

    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public void setBitmapUser(byte[] bitmapUser) {
        this.bitmapUser = bitmapUser;
    }

    public byte[] getBitmapUser() {
        return bitmapUser;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_image() {
        return user_image;
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

    public Map<String, Comentario> getComments() {
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

    public void like() {
        if (liked) {
            liked = !liked;
            likes--;
        } else {
            liked = !liked;
            likes++;
        }
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void addComentario(Comentario com) {
        comments.put(com.getId(), com);
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public boolean equals(Object obj) {
        Post post = (Post) obj;
        return this.getId().equals(post.getId());
    }

    public void removeComent(Comentario com) {
        comments.remove(com.getId());
    }



}
