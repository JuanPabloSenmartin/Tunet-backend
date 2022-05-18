package tunet.model;

public class idNumbers {
    public static int postID = 1;
    public static int artistListID = 1;

    public idNumbers() {

    }


    public static void addPostID() {
        idNumbers.postID++;
    }
    public static void addArtistListID() {
        idNumbers.artistListID++;
    }


}
