package com.novauc;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:./main");
    }

    public static void createTables() throws SQLException {
        Statement s = getConnection().createStatement();
        s.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR)");
        s.execute("CREATE TABLE IF NOT EXISTS songs (id IDENTITY, title VARCHAR, artist VARCHAR, user_id INT)");
        s.execute("CREATE TABLE IF NOT EXISTS votes (id IDENTITY, rating INT, user_id INT, song_id INT)");
    }

    public static void saveSong(String artistName, String songName) throws SQLException {
        User user = currentUser();

        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO songs (artist, title, user_id) VALUES (?, ?, ?)");
        stmt.setString(1, artistName);
        stmt.setString(2, songName);
        stmt.setInt(3, user.getId());
        stmt.execute();
    }

    public static ArrayList<Song> getAllSongs() throws SQLException {
        PreparedStatement s = getConnection().prepareStatement(
                "SELECT songs.id, songs.artist, songs.title, votes.rating " +
                        "FROM songs LEFT OUTER JOIN votes " +
                        "ON songs.id = votes.song_id " +
                        "WHERE votes.user_id = ? OR votes.user_id IS NULL"
                );
        s.setInt(1, currentUser().getId());

        ArrayList<Song> songs = new ArrayList<>();
        ResultSet r = s.executeQuery();
        while(r.next()) {
            // Step through each result
            int id = r.getInt("id");
            String artist = r.getString("artist");
            String title = r.getString("title");
            int rating = r.getInt("rating");

            songs.add(new Song(id, artist, title, rating));
        }

        return songs;
    }

    public static User currentUser() {
        return new User(1, "jdabbs");
    };

    public static void main(String[] args) throws SQLException {
        Spark.init();

        Server.createWebServer().start();

        createTables();

        Spark.get("/",
            (req, res) -> {
               HashMap m = new HashMap<>();
               m.put("name", "World");
               return new ModelAndView(m, "home.html");
            },
            new MustacheTemplateEngine()
        );

        Spark.post("/enter_song",
                (req, res) -> {
                    String name = req.queryParams("artist");
                    String title = req.queryParams("song");

                    saveSong(name, title);

                    res.redirect("/songs");
                    return "";
                });

        Spark.get("/songs",
                (req, res) -> {
                    HashMap m = new HashMap<>();
                    ArrayList<Song> songs = getAllSongs();
                    m.put("songs", songs);
                    return new ModelAndView(m, "songs.html");
                }, new MustacheTemplateEngine());

        Spark.post("/save_vote",
                (req, res) -> {
                    PreparedStatement s = getConnection().prepareStatement(
                            "INSERT INTO votes (song_id, user_id, rating) VALUES (?, ?, ?)");
                    s.setInt(1, Integer.valueOf(req.queryParams("song")));
                    s.setInt(2, currentUser().getId());
                    s.setInt(3, Integer.valueOf(req.queryParams("rating")));
                    s.execute();

                    res.redirect("/songs");
                    return "";
                });
    }
}
