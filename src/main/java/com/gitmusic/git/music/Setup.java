package com.gitmusic.git.music;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Scanner;

public class Setup {

    public static void setup() throws IOException, URISyntaxException {

        String currentDir = System.getProperty("user.dir");
        File hooksDir = new File(currentDir + "/.git/hooks");

        if (hooksDir.exists()) {
            File hookFile = new File(hooksDir + "/prepare-commit-msg");
            FileWriter fw = new FileWriter(hookFile);

            String userHome = System.getProperty("user.home");
            String jarPath = userHome + "/git-music.jar";

            String sourceJar = new File(Setup.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getPath();

            fw.write("#!/bin/sh\n");
            fw.write("java -jar \"" + jarPath + "\" \"$1\"\n");
            fw.close();

            Files.copy(
                    new File(sourceJar).toPath(),
                    new File(jarPath).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            Scanner scanner = new Scanner(System.in);

            File configFile = new File(userHome + "/git-music-config.txt");
            if (configFile.exists()) {
                System.out.println("Spotify credentials already configured.");
            } else {
                System.out.print("Enter your Spotify Client ID: ");
                String clientId = scanner.nextLine();
                System.out.print("Enter your Spotify Client Secret: ");
                String clientSecret = scanner.nextLine();
                FileWriter config = new FileWriter(userHome + "/git-music-config.txt");
                config.write(clientId + "\n");
                config.write(clientSecret + "\n");
                config.close();
            }

            File tokenFile = new File(userHome + "/git-music-token.txt");
            if (tokenFile.exists()) {
                System.out.println("Already logged in to Spotify.");
            } else {
                System.out.println("Please login to Spotify.");
                SpotifyClient spotify = new SpotifyClient();
                spotify.login();
                System.out.print("Enter code: ");
                String code = scanner.nextLine();
                spotify.getAccessToken(code);
            }
            System.out.println("Setup complete! Your commits will now include the currently playing song. ♪");
        } else {
            System.out.println("No git project found in this directory!");

        }

    }
}
