package pt.novaleaf.www.maisverde;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable{

    private String name;
    private List<String> base_users;
    private List<String> admins;
    private long points;
    private long creationDate;
    private String groupId;
    private String privacy;
    private String image_uri;
    private String distrito;
    private boolean isAdmin;
    private boolean isMember;
    private long numbMembers;
    private int imageID;
    private byte[] bitmap;

    public Grupo(String name, List<String> base_users, List<String> admins, long points,
                 long creationDate, String image_uri,String groupId, String privacy, String distrito,
                 boolean isAdmin, boolean isMember, long numbMembers)
    {

        this.name = name;
        this.base_users = base_users;
        this.admins = admins;
        this.points = points;
        this.creationDate = creationDate;
        this.groupId = groupId;
        this.privacy = privacy;
        this.distrito = distrito;
        this.image_uri = image_uri;
        this.isAdmin = isAdmin;
        this.isMember = isMember;
        this.numbMembers = numbMembers;
        imageID = 0;
        bitmap = null;

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

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public void setBase_users(List<String> base_users) {
        this.base_users = base_users;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getName() {
        return name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getDistrito() {
        return distrito;
    }

    public long getNumPessoas(){
        return numbMembers;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public List<String> getBase_users() {
        return base_users;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getPoints() {
        return points;
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public boolean equals(Object obj) {
        Grupo grupo = (Grupo) obj;
        return this.getGroupId().equals(grupo.getGroupId());
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
