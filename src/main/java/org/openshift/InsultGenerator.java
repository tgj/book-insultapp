package org.openshift;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class InsultGenerator {

    public String generateInsult() {
        final String vowels = "AEIOU";
        String article = "an";
        String insult = "";

        try {
            String databaseURL = "jdbc:postgresql://";
            databaseURL += System.getenv("POSTGRESQL_SERVICE_HOST");
            databaseURL += "/" + System.getenv("POSTGRESQL_DATABASE");

            String username = System.getenv("POSTGRESQL_USER");
            String password = System.getenv("PGPASSWORD");
            Connection connection = DriverManager.getConnection(databaseURL, username,
                    password);

            if (connection != null) {
                final String SQL = "SELECT a.string AS first, b.string AS second, c.string AS noun  from short_adjective a, long_adjective b, noun c ORDER BY random() limit 1";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL);

                while (resultSet.next()) {
                    if (vowels.indexOf(resultSet.getString("first").charAt(0)) == -1) {
                        article = "a";
                    }

                    insult = String.format("Thou art %s %s %s %s!",
                        article,
                        resultSet.getString("first"),
                        resultSet.getString("second"),
                        resultSet.getString("noun"));
                }

                resultSet.close();
                connection.close();
            }
        } catch (Exception e) {
            return "An error has occurred trying to connect to the database.";
        }

        return insult;
    }

}
