package player.entity;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

/**
 * Class for a Track.
 */
public class Track {
    private String fileName;
    private String path;
    private Media media;
    private String title;
    private String artist;
    private Image image;

    private Track(){

    }

    public Track (String filename, String filePath){
        this.fileName = filename;
        this.path = filePath;
        this.media = new Media(new File(filePath).toURI().toString());
        setInfoFromMedia();
    }

    //getters

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public Media getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Image getImage() {
        return image;
    }

    public Duration getDuration () {
        return media.getDuration();
    }

    //setters

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private void setInfoFromMedia () {
        if (artist == null){
            artist = fileName.substring(0, fileName.indexOf("–")).trim();
        }
        if (title == null){
            title = fileName.substring(fileName.indexOf("–")+1, fileName.length()-4).trim();
        }
        System.out.println("ar " + artist + " t " + title );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(fileName, track.fileName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileName);
    }
}
