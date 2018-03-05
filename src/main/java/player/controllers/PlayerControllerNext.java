package player.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import player.NextRunner;
import player.PlayerFormRunner;
import player.entity.Track;
import player.entity.TrackList;
import player.entity.TrackListInt;
import player.utils.AlbumAnimation;
import player.utils.MoveForm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import player.utils.WriteAndReadFile;

public class PlayerControllerNext {
    private NextRunner mainRunner;

    @FXML
    private Label timeLabel;
    @FXML
    private Label timeLabelEnd;
    @FXML
    private ProgressBar progressBar;
    private ChangeListener<Duration> progressChangeListener;
    @FXML
    private Slider slider;

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
    private Pane toolBarPane;
    @FXML
    private FontAwesomeIconView fontRepeat;
    @FXML
    private FontAwesomeIconView fontRandom;
    @FXML
    private ListView<String> listView = new ListView<>();
    @FXML
    private TableView tableView = new TableView();
    @FXML
    private TableColumn<Track, String> artistColumn;
    @FXML
    private TableColumn<Track, String> titleColumn;
    @FXML
    private AnchorPane playlistPane = new AnchorPane();
    @FXML
    private Label allS = new Label();

    @FXML
    private TableView songs = new TableView();
    @FXML
    private TableColumn<Track, String> artistCol1;
    @FXML
    private TableColumn<Track, String> titleCol1;

    @FXML
    private TableView playlistView = new TableView();
    @FXML
    private TableColumn<Track, String> artistCol2;
    @FXML
    private TableColumn<Track, String> titleCol2;

    @FXML
    private TextField textField = new TextField();

    @FXML
    private TableView View = new TableView();
    @FXML
    private TableColumn<Track, String> artistCol3;
    @FXML
    private TableColumn<Track, String> titleCol3;

    @FXML
    private AnchorPane lastPane = new AnchorPane();

    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isMax = false;
    private final double PREF_W = 960.0;
    private final double PREF_H = 590.0;
    private final int MAX_LENTH = 20;
    private File path;
    private boolean isTrackLoad = false;
    private boolean playing = false;
    private ObservableList<Track> tracks, randomList, currentList, buffer;
    private Track currentTrack, prevTrack, nextTrack;
    private boolean repeat = false;
    private boolean random = false;
    private ObservableList<ObservableList<Track>> allPlaylists = FXCollections.observableArrayList();
    private ObservableList<String> items;
    private Map<String,ObservableList<Track>> viewTrackMap = new HashMap();
    ObservableList<Track> playlist;

    private ObservableList<Track> getBuffer() {
        return buffer;
    }
    private void setBuffer (ObservableList<Track> list) {
        buffer = FXCollections.observableArrayList();
        buffer.addAll(list);
    }

    @FXML
    private void initialize () {
        //loadCurrentTrack();

        //add listener on tool bar -> then drag it, forms moves
        MoveForm.addDragListeners(toolBarPane);
        //load track list to play
        setupTracks();

        //set time label and bar to show progress in plauing music
        //setTimeLabelAndProgressBar(mediaPlayer);

        items = FXCollections.observableArrayList ("Songs", "Playlists");
        listView.setItems(items);
        playlistPane.setDisable(true);
        playlistPane.setVisible(false);

        lastPane.setDisable(true);
        lastPane.setVisible(false);

        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
                        //System.out.println(new_val);

                        if ("Songs".equals(new_val)){
                            allSongs();
                        }
                        if ("Playlists".equals(new_val)){
                            addPlaylist();
                        }
                    }
                });
        tableView.setVisible(true);
        tableView.setDisable(false);

        artistColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));
    }

    private void allSongs () {
        tableView.setDisable(false);
        tableView.setVisible(true);
        playlistPane.setVisible(false);
        playlistPane.setDisable(true);
        lastPane.setVisible(false);
        lastPane.setDisable(true);

        currentList = tracks;
    }

    private void addPlaylist () {
        tableView.setDisable(true);
        tableView.setVisible(false);
        playlistPane.setVisible(true);
        playlistPane.setDisable(false);
        lastPane.setVisible(false);
        lastPane.setDisable(true);
        textField.setText("");

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (textField.getText().length() > MAX_LENTH) {
                    String s = textField.getText().substring(0, MAX_LENTH);
                    textField.setText(s);
                }
            }
        });

        artistCol1.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
        titleCol1.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));

        artistCol2.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
        titleCol2.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));

        songs.setItems(tracks);
        playlist = FXCollections.observableArrayList();;
        playlistView.setItems(null);


        songs.setRowFactory( tv -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    playlist.add(row.getItem());
                    playlistView.setItems(playlist);
                }
            });
            return row ;
        });

        playlistView.setRowFactory( tv -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    playlist.remove(row.getItem());
                    playlistView.setItems(playlist);
                }
            });
            return row ;
        });

    }

    private void setupTracks () {
        TrackListInt trackList = TrackList.getInstance();
        //trackList.testFillTracks();
        tracks = trackList.getTracks();

        System.out.println("tracks size = " + tracks.size());
        currentList = tracks;
//        playTrack(tracks.get(0));
//        mediaPlayer.stop();
        allPlaylists.add(tracks);
        setupTrackTableView();
    }

    /**
     * Set current, next and prev track, play current track.
     * @param track
     */
    private void playTrack (Track track) {
        //set cur, next, prev tracks
        currentTrack = track;
        //int totalTracks = tracks.size();
        //int curTrackNumber = tracks.indexOf(currentTrack);
        int totalTracks = currentList.size();
        int curTrackNumber = currentList.indexOf(currentTrack);
        int next = curTrackNumber + 1;
        int prev = curTrackNumber - 1;

        if (prev >= 0) {
            //prevTrack = tracks.get(prev);
            prevTrack = currentList.get(prev);
        }
        else {
            //prevTrack = tracks.get(totalTracks - 1);
            prevTrack = currentList.get(totalTracks - 1);
        }

        if (next < totalTracks) {
            //nextTrack = tracks.get(next);
            nextTrack = currentList.get(next);
        }
        else {
            //nextTrack = tracks.get(0);
            nextTrack = currentList.get(0);
        }

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer(currentTrack.getMedia());


        mediaPlayer.setOnReady(new Runnable() {
            private double t = 0;
            @Override
            public void run() {
                //System.out.println("Duration: "+ currentTrack.getMedia().getDuration().toSeconds());
                t = currentTrack.getMedia().getDuration().toSeconds();
                int minutes = (int) (t % 3600) / 60;
                int seconds = (int) t % 60;
                String formattedMinutes = String.format("%02d", minutes);
                String formattedSeconds = String.format("%02d", seconds);
                timeLabelEnd.setText(formattedMinutes + ":" + formattedSeconds);
                /*
                System.out.println("cur image: " + currentTrack.getMedia().getMetadata().get("image"));
                currentTrack.setImage((Image)currentTrack.getMedia().getMetadata().get("image"));
                setInfoFromMedia(currentTrack);*/
                setInfoFromMedia(prevTrack);
                setInfoFromMedia(currentTrack);
                setInfoFromMedia(nextTrack);
            }
        });

        mediaPlayer.play();
        setTimeLabelAndProgressBar(mediaPlayer);

        setInfoFromMedia(prevTrack);
        setInfoFromMedia(currentTrack);
        setInfoFromMedia(nextTrack);
        setMediaInfo();

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

        //set volume
        slider.setOnMouseClicked((MouseEvent mouseEvent) -> {
            mediaPlayer.setVolume(slider.getValue()/100);
            //System.out.println(mediaPlayer.getVolume());

        });
        slider.setOnDragDone((DragEvent dE) -> {
            mediaPlayer.setVolume(slider.getValue()/100);
        });

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

    /**
     * Set text labels and loading default and actual album images to form.
     */
    private void setMediaInfo (){
        trackInfo.setText(currentTrack.getTitle());
        artist.setText(currentTrack.getArtist());
        mediaPlayer.setVolume(slider.getValue()/100);

        //System.out.println((currentTrack.getMedia().getMetadata().get("duration")) + " " + currentTrack.getMedia().getDuration().isUnknown());
        // Set time
        /*double timeSeconds = (currentTrack.getMedia().getDuration().toSeconds());
        System.out.println(timeSeconds);
        int minutes = (int) (timeSeconds % 3600) / 60;
        int seconds = (int) timeSeconds % 60;
        String formattedMinutes = String.format("%02d", minutes);
        String formattedSeconds = String.format("%02d", seconds);
        timeLabelEnd.setText(formattedMinutes + ":" + formattedSeconds);*/

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
                    if (track.getImage() != null) {
                        setMediaInfo();
                    }
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

    public void setMainRunner(NextRunner mainRunner) {
        this.mainRunner = mainRunner;
    }

    private void handleNextTrack() {
        if (!repeat) {
            //if repeat track is OFF

            //if (random){
                //if random is ON
                /*randomList.remove(currentTrack);
                if (!randomList.isEmpty()) {
                    playTrack(randomTrack());
                }
                else {

                    randomList = FXCollections.observableArrayList();
                    randomList.addAll(currentList);
                    playTrack(randomList.get(0));
                }*/
            //}
            //else
                if (nextTrack != null) {
                    //normal work, random and repeat is OFF
                    playTrack(nextTrack);
                }
        }
        else{
                //if repeat track is ON
                playTrack(currentTrack);
        }
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
        //if (random){
            //if random is ON
            /*randomList.remove(currentTrack);
            if (!randomList.isEmpty()) {
                playTrack(randomTrack());
            }
            else {

                randomList = FXCollections.observableArrayList();
                randomList.addAll(currentList);
                playTrack(randomList.get(0));
            }*/
        //}
        //else
            if (nextTrack != null) {
                //System.out.println(nextTrack.getMedia().getMetadata().get("image"));
                playTrack(nextTrack);
            }
    }

    public void handleClose(ActionEvent actionEvent) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        Platform.exit();
        System.exit(0);
    }

    public void handleMaximize(ActionEvent actionEvent) {
        if (isMax) {
            isMax = false;
            mainRunner.getPrimaryStage().setMaximized(isMax);
            mainRunner.getPrimaryStage().setMaxWidth(PREF_W);
            mainRunner.getPrimaryStage().setMaxHeight(PREF_H);
            mainRunner.getPrimaryStage().centerOnScreen();
        }
        else {
            isMax = true;
            mainRunner.getPrimaryStage().setMaximized(isMax);
        }
    }

    public void handleMinimize(ActionEvent actionEvent) {
        mainRunner.getPrimaryStage().setIconified(true);
    }

    /**
     * Button repeat track.
     * @param actionEvent
     */
    public void handleRepeat(ActionEvent actionEvent) {
        repeat = !repeat;
        if (repeat)
            fontRepeat.setStyle("-fx-fill: #4ff7e7;");
        else
            fontRepeat.setStyle("-fx-fill: #ffffff;");
    }

    /**
     * Button Random.
     * @param actionEvent
     */
    public void handleRandom(ActionEvent actionEvent) {
        random =! random;
        if (random){
            fontRandom.setStyle("-fx-fill: #009688;");
            //randomList = FXCollections.observableArrayList();
            //randomList.addAll(currentList);
            //System.out.println("Random begin size is: " + randomList.size());
            setRandomTrackList();
            setBuffer(currentList);
            currentList = randomList;
            playTrack(randomList.get(0));
        }
        else{
            fontRandom.setStyle("-fx-fill: #ffffff;");
            currentList = getBuffer();
            //playTrack(currentList.get(0));
        }
    }

    private void setRandomTrackList () {
        ObservableList<Track> temp = FXCollections.observableArrayList();
        randomList = FXCollections.observableArrayList();
        temp.addAll(currentList);
        int size = temp.size();
        for (int i = 0; i < size; i++) {
            Track tempTrack = getRandomTrack(temp);
            randomList.add(tempTrack);
            temp.remove(tempTrack);
        }
        System.out.println("Random begin size is: " + randomList.size());
    }

    private Track getRandomTrack (ObservableList<Track> list) {
        int random_num = (int) (Math.random() * list.size());
        return list.get(random_num);
    }

    /**
     * Get random track from playlist.
     */
    private Track randomTrack() {
        int random_num = (int) (Math.random() * randomList.size());
        return randomList.get(random_num);
    }

    /**
     * Menu bar, File-Close
     * @param actionEvent
     */
    public void menuClose(ActionEvent actionEvent) {
        handleClose(actionEvent);
    }

    /**
     * Menu bar, File-File Add
     * @param actionEvent
     */
    public void menuFileAdd(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add .mp3");
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> track = fileChooser.showOpenMultipleDialog(mainRunner.getPrimaryStage());
        MediaPlayer loadPlayer;
        if (track != null){
            for (int i = 0; i < track.size(); i++) {
                Track nTrack = new Track(track.get(i).getName(), track.get(i).getPath());
                if (!TrackList.getInstance().equalTrack(nTrack)){
                    loadPlayer = new MediaPlayer(nTrack.getMedia());
                    loadPlayer.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println(nTrack.getMedia().getMetadata().get("image"));
                            nTrack.setImage((Image)nTrack.getMedia().getMetadata().get("image"));
                        }
                    });
                    tracks.add(nTrack);
                    loadPlayer = null;
                }
            }
            playTrack(tracks.get(0));
            mediaPlayer.stop();
            setupTrackTableView();
        }

    }

    /**
     * Menu button About.
     * @param actionEvent
     */
    public void handlerAbout(ActionEvent actionEvent) {
        System.out.println("About");
    }


    /**
     * Create Playlist.
     * @param actionEvent
     */
    public void handleCreatePl(ActionEvent actionEvent) {
        String tField = textField.getText();
        if (playlistView == null)
            return;
        ObservableList<Track> list = FXCollections.observableArrayList(playlistView.getItems()); //playlistView.getItems()
        playlistView.setItems(FXCollections.observableArrayList());
        textField.setText("");
        playlist.clear();

        if (!tField.isEmpty() && list.size() != 0){
            ObservableList<Track> pl = list;
            viewTrackMap.put(tField, pl);
            allPlaylists.add(pl);
            items.add(tField);

            artistCol3.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
            titleCol3.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));

            listView.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<String>() {
                        public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {

                            if (tField.equals(new_val)){
                                System.out.println("works " + tField);

                                tableView.setDisable(true);
                                tableView.setVisible(false);
                                playlistPane.setVisible(false);
                                playlistPane.setDisable(true);

                                lastPane.setDisable(false);
                                lastPane.setVisible(true);

                                View.setItems(viewTrackMap.get(new_val));


                                View.setRowFactory( tv -> {
                                    TableRow<Track> row = new TableRow<>();
                                    row.setOnMouseClicked(event -> {
                                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                            System.out.println(tField);
                                            Track track = row.getItem();

                                            currentList = viewTrackMap.get(getKey(viewTrackMap, View.getItems()));
                                            if (random){
                                                handleRandom(actionEvent);
                                            }
                                            playTrack(track);
                                        }
                                    });
                                    return row ;
                                });
                            }
                        }
                    });

        }
    }

    private String getKey (Map map, ObservableList<Track> list) {

        Set<Map.Entry<String, ObservableList<Track>>> entrySet = map.entrySet();

        ObservableList<Track> desiredObject = list; //что хотим найти
        for (Map.Entry<String, ObservableList<Track>> pair : entrySet) {
            System.out.println(pair.getValue());
            if (desiredObject.equals(pair.getValue())) {
                return pair.getKey();// нашли наше значение и возвращаем  ключ
            }
        }
        return "";
    }

    /**
     * Button to Save playlist to Disk.
     * @param actionEvent
     */
    public void handleSaveToDisk(ActionEvent actionEvent) {
        ObservableList<Track> lastList = View.getItems();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if (dir != null){
            WriteAndReadFile.writePlayList(lastList, getKey(viewTrackMap, lastList), dir.getPath());
        }
    }

    /**
     * Button to Load pl from Disk.
     * @param actionEvent
     */
    public void handleLoadFromDisk(ActionEvent actionEvent) {
        ObservableList<Track> list;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Playlist");
        File file = fileChooser.showOpenDialog(mainRunner.getPrimaryStage());
        String name;
        if (file != null){
            name = file.getName().substring(0, file.getName().length()-4);
            list = WriteAndReadFile.readPlayList(file);
            if (list != null) {
                System.out.println("works");

                if (!name.isEmpty() && list.size() != 0){
                    ObservableList<Track> pl = list;
                    viewTrackMap.put(name, list);
                    items.add(name);

                    artistCol3.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
                    titleCol3.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));

                    listView.getSelectionModel().selectedItemProperty().addListener(
                            new ChangeListener<String>() {
                                public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {

                                    if (name.equals(new_val)){
                                        //System.out.println("works " + tField);

                                        tableView.setDisable(true);
                                        tableView.setVisible(false);
                                        playlistPane.setVisible(false);
                                        playlistPane.setDisable(true);

                                        lastPane.setDisable(false);
                                        lastPane.setVisible(true);

                                        View.setItems(viewTrackMap.get(new_val));


                                        View.setRowFactory( tv -> {
                                            TableRow<Track> row = new TableRow<>();
                                            row.setOnMouseClicked(event -> {
                                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                                    //System.out.println(tField);
                                                    Track track = row.getItem();

                                                    currentList = viewTrackMap.get(getKey(viewTrackMap, View.getItems()));
                                                    if (random){
                                                        handleRandom(actionEvent);
                                                    }
                                                    playTrack(track);
                                                }
                                            });
                                            return row ;
                                        });
                                    }
                                }
                            });

                }

            }
        }

    }

    /**
     * Shows guide.
     * @param actionEvent
     */
    public void handlerGuide(ActionEvent actionEvent) {
        final Stage stage = new Stage();
        AnchorPane root = null;
        try {
            FXMLLoader loader;
            //loader = FXMLLoader.load(getClass().getClassLoader().getResource("fxmls/saveTrack.fxml"));
            //FXMLLoader loadessr = new FXMLLoader(getClass().getResource("fxmls/saveTrack.fxml"));
            loader = new FXMLLoader(PlayerControllerNext.class.getClassLoader().getResource("/fxml/GuideForm.fxml")); //getClass().getResource("../fxml/GuideForm.fxml")
            root = (AnchorPane) loader.load();

            stage.setTitle("Загрузка треков"); //minHeight="300.0" minWidth="430.0"
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((mainRunner.getPrimaryStage()).getScene().getWindow()));

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
