
package com.gitmusic.git.music;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;


public class GitMusic {

    public static void main(String[] args) throws IOException, URISyntaxException {
System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
   if(args.length > 0 && args[0].equals("setup")) {
            Setup.setup();
        } else if(args.length > 0) {
            SpotifyClient spotify = new SpotifyClient();
            String song = spotify.getCurrentSong();
            if(song != null) {
                File commitMsgFile = new File(args[0]);
                String currentMsg = new String(Files.readAllBytes(commitMsgFile.toPath()));
                FileWriter fw = new FileWriter(commitMsgFile, java.nio.charset.StandardCharsets.UTF_8);
                fw.write(currentMsg.trim() + " ♪ " + song + "\n");
                fw.close();
            }
        } else {
            SpotifyClient spotify = new SpotifyClient();
            System.out.println(spotify.getCurrentSong());
        }
        
    }
}
