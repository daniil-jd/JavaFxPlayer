package player.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class TestController {
    private boolean isPlaying = false;
    private boolean isTrackLoad = false;
    private File path;
    private Media media;
    private MediaPlayer mediaPlayer;

    private final double TOTAL_SONG_DURATION = 195.761632653;

    @FXML
    private Slider timeSlider;

    private void loadCurrentTrack () {
        path = new File("D:\\files\\music\\Bastille – World Gone Mad.mp3");
        media = new Media (path.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        //timeSlider = new Slider();
        isTrackLoad = true;
    }

    public void playPressed(ActionEvent actionEvent) {
        isPlaying = !isPlaying;
        if (isPlaying) {
            if (!isTrackLoad){
                loadCurrentTrack ();
            }
            double current_total_duration = mediaPlayer.getTotalDuration().toSeconds();

            /*timeSlider.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> current_total_duration < 1? TOTAL_SONG_DURATION : current_total_duration,
                    mediaPlayer.totalDurationProperty()));*/

            timeSlider.setOnMouseClicked((MouseEvent mouseEvent) -> {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                double total_dur = mediaPlayer.getMedia().getDuration().toSeconds();
                double percent = timeSlider.getValue() / timeSlider.getMax();
                if (status.equals(MediaPlayer.Status.PLAYING)){

                    mediaPlayer.pause();
                    mediaPlayer.seek(Duration.seconds(percent * total_dur));

                    mediaPlayer.play();
                }
                else {
                    mediaPlayer.seek(Duration.seconds(percent * total_dur));
                }

            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    double curent_time = newValue.toSeconds();
                    double total_dur = mediaPlayer.getMedia().getDuration().toSeconds();

                    timeSlider.setValue(curent_time * timeSlider.getMax() / total_dur);
                }
            });


            mediaPlayer.play();

        }else {
            mediaPlayer.pause();
        }
    }

    private void setTimeSlider(){
        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable,
                                                       Duration oldValue, Duration newValue) -> {
            timeSlider.setValue(newValue.toSeconds());
        });

        timeSlider.setOnMouseClicked((MouseEvent mouseEvent) -> {
            mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
        });
    }
}
