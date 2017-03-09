package com.novauc;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws SQLException {
        Spark.init();

        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");

//        Statement s = conn.createStatement();
//        s.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR)");
//        s.execute("DROP TABLE IF EXISTS users");
//
//        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name) VALUES (?)");
//        stmt.setString(1, "SQL");
//        stmt.execute();

        Spark.get("/",
            (req, res) -> {
               HashMap m = new HashMap<>();
               m.put("name", "World");
               return new ModelAndView(m, "home.html");
            },
            new MustacheTemplateEngine()
        );

    }
}
