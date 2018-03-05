package player.controllers;

import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import player.PlayerFormRunner;
import player.utils.AlbumAnimation;
import player.entity.Track;
import player.entity.TrackList;
import player.utils.MoveForm;


import java.io.File;
import java.io.InputStream;

/**
 * This main player class. Goal is playing tracks.
 */
public class MainPlayerController {
    @FXML
    private Label timeLabel;
    @FXML
    private ProgressBar progressBar;
    private ChangeListener<Duration> progressChangeListener;

    //Player controls
    @FXML
    private Button playButton;
    @FXML
    private FontAwesomeIconView playButtonIcon;
    @FXML
    private Label trackInfo;
    @FXML
    private Label artist;
    @FXML
    private ImageView albumImageView;
    @FXML
    private ImageView albumImageViewLeft;
    @FXML
    private ImageView albumImageViewRight;
    @FXML
    private ToolBar toolBar;
    @FXML
    private TableView tableView = new TableView();
    @FXML
    private TableColumn<Track, String> artistColumn;
    @FXML
    private TableColumn<Track, String> titleColumn;

    private File path;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isTrackLoad = false;
    private boolean playing = false;
    private ObservableList<Track> tracks;
    private Track currentTrack, prevTrack, nextTrack;
    private PlayerFormRunner mainRunner;
    private boolean isMax = false;

    @FXML
    private void initialize () {
        //loadCurrentTrack();

        //add listener on tool bar -> then drag it, forms moves
        MoveForm.addDragListeners(toolBar);
        //load track list to play
        setupTracks();
        //set time label and bar to show progress in plauing music
        setTimeLabelAndProgressBar(mediaPlayer);

        artistColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));
    }

    private void setupTracks () {
        TrackList trackList = TrackList.getInstance();
        trackList.testFillTracks();
        tracks = trackList.getTracks();

        System.out.println("tracks size = " + tracks.size());
        playTrack(tracks.get(0));
        mediaPlayer.stop();

        setupTrackTableView();
    }

    /**
     * Test func to load and set current track to play.
     */
    private void loadCurrentTrack () {
        path = new File("D:\\files\\music\\Bastille – World Gone Mad.mp3");
        media = new Media (path.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        //isTrackLoad = true;
    }

    /**
     * Search and set previous track in PlayList.
     * @param actionEvent
     */
    public void handlePrevButton(ActionEvent actionEvent) {
        if (prevTrack != null) {
            playTrack(prevTrack);
        }
    }

    /**
     * Playing music file.
     * @param actionEvent
     */
    public void handlePlayButton(ActionEvent actionEvent) {
        if(mediaPlayer != null) {
            boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
            if(playing) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
/*
        playing = !playing;
        if (playing){
            if (!isTrackLoad){
                loadCurrentTrack();
                setTimeLabelAndProgressBar(mediaPlayer);
                mediaPlayer.play();
            }
            else {
                mediaPlayer.play();
            }
        }
        else {
            mediaPlayer.pause();
        }*/
    }

    /**
     * Search and set next track in PlayList.
     * @param actionEvent
     */
    public void handleNextButton(ActionEvent actionEvent) {
        if (nextTrack != null) {
            //System.out.println(nextTrack.getMedia().getMetadata().get("image"));
            playTrack(nextTrack);
        }
    }

    private void setTimeLabelAndProgressBar (MediaPlayer player) {
        progressBar.setProgress(0);
        // Add progressbar click listener to change song position
        progressBar.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && false){
                Bounds b1 = progressBar.getLayoutBounds();
                double mouseX = event.getSceneX();
                double percent = (((b1.getMinX() + mouseX -15 ) * 100) / b1.getMaxX());
                if (percent > 100.0) percent = 100.0;
                percent /= 100;
                double totalDuration = player.getTotalDuration().toSeconds();
                double seek = 0.0;

                seek = ((totalDuration) * percent);
                System.out.println(Duration.seconds(seek));
                player.seek(Duration.seconds(seek));
            }
        });

        // Add progressbar listener to show current song percent
        progressChangeListener = (ObservableValue<? extends Duration> observableValue,
                                  Duration oldValue, Duration newValue) -> {
            double currentTimeMillis = player.getCurrentTime().toSeconds();
            double totalDurationMillis = player.getTotalDuration().toSeconds();
            progressBar.setProgress(1.0 * currentTimeMillis / totalDurationMillis);

            // Set time count in label
            double currentTimeSeconds = player.getCurrentTime().toSeconds();
            int minutes = (int) (currentTimeSeconds % 3600) / 60;
            int seconds = (int) currentTimeSeconds % 60;
            String formattedMinutes = String.format("%02d", minutes);
            String formattedSeconds = String.format("%02d", seconds);
            timeLabel.setText(formattedMinutes + ":" + formattedSeconds);
        };
        player.currentTimeProperty().addListener(progressChangeListener);


        //Add player action on finish
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.currentTimeProperty().removeListener(progressChangeListener);
            handleNextTrack();
        });

        // Add player action on pause
        mediaPlayer.setOnPaused(() -> {
            playButtonIcon.setIcon(FontAwesomeIcon.PLAY);
            playButtonIcon.setSize("30");
        });

        // Add player action on playing
        mediaPlayer.setOnPlaying(() -> {
            playButtonIcon.setIcon(FontAwesomeIcon.PAUSE);
            playButtonIcon.setSize("25");
        });
    }

    private void handleNextTrack() {
        if (nextTrack != null) {
            playTrack(nextTrack);
        }
    }

    /**
     * Set current, next and prev track, play current track.
     * @param track
     */
    private void playTrack (Track track) {
        //set cur, next, prev tracks
        currentTrack = track;
        int totalTracks = tracks.size();
        int curTrackNumber = tracks.indexOf(currentTrack);
        int next = curTrackNumber + 1;
        int prev = curTrackNumber - 1;

        if (prev >= 0)
            prevTrack = tracks.get(prev);
        else
            prevTrack = tracks.get(totalTracks-1);

        if (next < totalTracks)
            nextTrack = tracks.get(next);
        else
            nextTrack = tracks.get(0);

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer(currentTrack.getMedia());
        mediaPlayer.play();
        setTimeLabelAndProgressBar(mediaPlayer);

        setInfoFromMedia(prevTrack);
        setInfoFromMedia(currentTrack);
        setInfoFromMedia(nextTrack);
        setMediaInfo();

    }

    /**
     * Set text labels and loading default and actual album images to form.
     */
    private void setMediaInfo (){
        trackInfo.setText(currentTrack.getTitle());
        artist.setText(currentTrack.getArtist());

        InputStream defaultAlbumStream = MainPlayerController.class.getResourceAsStream("/img/noalbumart.png");
        Image defaultAlbumImage = new Image(defaultAlbumStream);

        if(currentTrack.getImage() == null) {
            SequentialTransition transitionImageView =
                    AlbumAnimation.fadeTransition(albumImageView, defaultAlbumImage);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    AlbumAnimation.fadeTransition(albumImageView, currentTrack.getImage());
            transitionImageView.play();
        }

        if(nextTrack == null || nextTrack.getImage() == null) {
            SequentialTransition transitionImageView =
                    AlbumAnimation.translateTransition(albumImageViewRight, defaultAlbumImage, 0, 400);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    AlbumAnimation.translateTransition(albumImageViewRight, nextTrack.getImage(), 0, 400);
            transitionImageView.play();
        }

        if(prevTrack == null || prevTrack.getImage() == null) {
            SequentialTransition transitionImageView =
                    AlbumAnimation.translateTransition(albumImageViewLeft, defaultAlbumImage, 0, -400);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    AlbumAnimation.translateTransition(albumImageViewLeft, prevTrack.getImage(), 0, -400);
            transitionImageView.play();
        }
    }

    /**
     * Set on track album image from media file.
     * @param track
     */
    private void setInfoFromMedia (Track track) {
        Media currentMedia = track.getMedia();

        currentMedia.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> ch) -> {
            if (ch.wasAdded()){
                if (ch.getKey().equals("image")){

                    track.setImage((Image) ch.getValueAdded());
                    if (track.getImage() != null)
                        setMediaInfo();
                }
            }
        });

    }

    /**
     * Setup tracks cells, observers and actions
     *
     */
    private void setupTrackTableView() {
        // Initialize the tracks table with the columns
        /*titleColumn.setCellValueFactory(
                cellData -> cellData.getValue().getTitle();
        artistColumn.setCellValueFactory(
                cellData -> cellData.getValue().getArtist());*/
        tableView.setItems(tracks);

        tableView.setRowFactory( tv -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Track track = row.getItem();
                    playTrack(track);
                }
            });
            return row ;
        });

    }

    /**
     * Form close button.
     * @param actionEvent
     */
    @FXML
    public void handleClose(ActionEvent actionEvent) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Form Maximize button.
     * @param actionEvent
     */
    @FXML
    public void handleMaximize(ActionEvent actionEvent) {
        if (isMax) {
            isMax = false;
            mainRunner.getPrimaryStage().setMaximized(isMax);
            mainRunner.getPrimaryStage().setMaxWidth(1060);
            mainRunner.getPrimaryStage().setMaxHeight(638);
            mainRunner.getPrimaryStage().centerOnScreen();
        }
        else {
            isMax = true;
            mainRunner.getPrimaryStage().setMaximized(isMax);
        }
    }

    /**
     * Form Minimize button.
     * @param actionEvent
     */
    @FXML
    public void handleMinimize(ActionEvent actionEvent) {
        mainRunner.getPrimaryStage().setIconified(true);
    }

    /**
     * Set main Runner.
     * @param mainRunner
     */
    public void setMainRunner(PlayerFormRunner mainRunner) {
        this.mainRunner = mainRunner;
    }
}
