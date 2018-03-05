package player.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import player.entity.Track;

import java.io.*;
import java.util.Scanner;

public class WriteAndReadFile {

    public static boolean writePlayList (ObservableList<Track> tracks, String name, String path){
        try(FileWriter writer = new FileWriter(path + "\\" + name + ".txt", true))
        {
            //marker
            writer.write("JavaFxPlayer" + "\n");

            for (Track track : tracks) {
                 writer.write(track.getFileName() + "|" + track.getPath() + "\n");
            }

            writer.write("end");
            writer.flush();
            writer.close();
            return true;
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        return  false;
    }

    public static ObservableList<Track> readPlayList (File file){
        try {
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader);
            if (!scanner.nextLine().equals("JavaFxPlayer")) return null;
            ObservableList<Track> result = FXCollections.observableArrayList();
            String row = scanner.nextLine();
            while (!row.equals("end")){
                String fileName = row.substring(0, row.indexOf("|"));
                String filePath = row.substring(row.indexOf("|") + 1, row.length());
                Track track = new Track(fileName, filePath);
                MediaPlayer loadImage = new MediaPlayer(track.getMedia());
                loadImage.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println(nTrack.getMedia().getMetadata().get("image"));
                        track.setImage((Image)track.getMedia().getMetadata().get("image"));
                    }
                });
                result.add(track);
                row = scanner.nextLine();
            }
            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] args) {
        try(FileWriter writer = new FileWriter("E:\\Temp\\note1.txt", true))
        {
            // запись всей строки
            String text = "Мама мыла раму, раму мыла мама";
            writer.write(text);
            // запись по символам
            writer.append('\n');
            writer.append('E');

            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
