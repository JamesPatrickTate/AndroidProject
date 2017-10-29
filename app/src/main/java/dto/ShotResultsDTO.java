package dto;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by james on 28/10/2017.
 *
 * Data Transfer Object for the NewShot activity
 * see https://www.youtube.com/watch?v=ZLyJLGkcvFM
 */

public class ShotResultsDTO {

    private String club;
    private String swingLength;
    private  int guid;

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getSwingLength() {
        return swingLength;
    }

    public void setSwingLength(String swingLength) {
        this.swingLength = swingLength;
    }

    public int getGuid() {
        return guid;
    }

    public void setGuid(int guid) {
        this.guid = guid;
    }



    public String toString() {
        return club+":"+swingLength+":"+guid;

    }





}
