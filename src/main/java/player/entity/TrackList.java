package player.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;

/**
 * Singleton class of TrackList. Adding, deleting tracks to/from list.
 */
public class TrackList implements TrackListInt {
    private static TrackList instance = null;
    private ObservableList<Track> tracks = FXCollections.observableArrayList();

    private TrackList() {}

    public static TrackList getInstance(){
        if (instance == null){
            instance = new TrackList();
        }
        return instance;
    }

    public void add(Track track){
        tracks.add(track);
    }

    public void delete (Track track){
        tracks.remove(track);
    }

    public ObservableList<Track> getTracks () {
        return tracks;
    }

    public void testFillTracks () {
        tracks.add(new Track("Bastille – World Gone Mad.mp3", "D:\\files\\music\\Bastille – World Gone Mad.mp3"));
        tracks.get(0).setArtist("Bastille");
        tracks.get(0).setTitle("World Gone Mad");
        tracks.add(new Track("ODESZA – A Moment Apart (#NR).mp3", "D:\\files\\music\\ODESZA – A Moment Apart (#NR).mp3"));
        tracks.get(1).setArtist("ODESZA");
        tracks.get(1).setTitle("A Moment Apart (#NR)");
        tracks.add(new Track("Stromae – Alors On Danse.mp3", "D:\\files\\music\\Stromae – Alors On Danse.mp3"));
        tracks.get(2).setArtist("Stromae");
        tracks.get(2).setTitle("Alors On Danse");
    }

    public int size () {
        return  tracks.size();
    }

    public void printAllTrack () {
        System.out.println("Print all tracks: ");
        for (int i = 0; i < tracks.size(); i++) {
            System.out.println(i + " " + tracks.get(i).getFileName());
        }
        System.out.println("----------");
    }

    public boolean equalTrack (Track track) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).equals(track)){
                return true;
            }
        }
        return  false;
    }

}
