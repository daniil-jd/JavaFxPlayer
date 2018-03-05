package player.entity;

import javafx.collections.ObservableList;

public interface TrackListInt {

    public void add(Track track);

    public void delete (Track track);

    public ObservableList<Track> getTracks ();

    public void testFillTracks ();

    public int size ();

    public void printAllTrack ();

    public boolean equalTrack (Track track);
}
