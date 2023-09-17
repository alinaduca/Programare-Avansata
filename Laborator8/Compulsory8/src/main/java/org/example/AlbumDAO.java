package org.example;

import java.sql.*;
import java.util.*;

public class AlbumDAO {

    private final Connection connection;

    public AlbumDAO() throws SQLException {
        connection = Database.getConnection();
    }

    public void addAlbum(Album album) {
        String sql = "insert into albums (id, release_year, title, artist_id) values (" + album.getId() +", " + album.getReleaseYear() +", '" + album.getTitle() +"', " + album.getArtist().getId() + ")";
        try {
            Statement stmt = connection.createStatement();
            int rows = stmt.executeUpdate(sql);
            System.out.println(rows + " rows inserted into table albums.");
        } catch (SQLException e) {
            System.out.println("You have this error:");
            e.printStackTrace();
        }
        for(Genre genre : album.getGenres()) {
            String sql2 = "insert into album_genres (album_id, genre_id) values (" + album.getId() + ", " + genre.getId() + ")";
            try {
                Statement stmt = connection.createStatement();
                int rows = stmt.executeUpdate(sql2);
                System.out.println(rows + " rows inserted into table album_genres.");
            } catch (SQLException e) {
                System.out.println("You have this error:");
                e.printStackTrace();
            }
        }
    }

    public List<Album> getAllAlbums() {
        try {
            List<Album> albums = new ArrayList<>();
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM albums";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int albumId = resultSet.getInt("id");
                int albumReleaseYear = resultSet.getInt("release_year");
                String albumTitle = resultSet.getString("title");
                int albumArtistId = resultSet.getInt("artist_id");
                Artist albumArtist = getArtistById(albumArtistId);
                Album album = new Album(albumId, albumReleaseYear, albumTitle, albumArtist, null);
                albums.add(album);
            }
            resultSet.close();
            statement.close();
            for(Album album : albums) {
                List<Genre> genres = getGenresByAlbumId(album.getId());
                album.setGenres(genres);
            }
            return albums;
        } catch(SQLException e) {
            System.out.println("You have this error:");
            e.printStackTrace();
        }
        return null;
    }

    public Album getAlbumById(int albumId) {
        Album album = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM albums WHERE id = ?");
            preparedStatement.setInt(1, albumId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int releaseYear = resultSet.getInt("release_year");
                String title = resultSet.getString("title");
                int artistId = resultSet.getInt("artist_id");
                Artist artist = getArtistById(artistId);
                List<Genre> genres = getGenresByAlbumId(id);
                album = new Album(id, releaseYear, title, artist, genres);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return album;
    }

    private Artist getArtistById(int artistId) {
        ArtistDAO artistDAO = new ArtistDAO(connection);
        try {
            return artistDAO.getArtistById(artistId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Genre> getGenresByAlbumId(int albumId) {
        List<Genre> genres = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM album_genres WHERE album_id = ?");
            statement.setInt(1, albumId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int genreId = resultSet.getInt("genre_id");
                GenreDAO genreDAO = new GenreDAO(connection);
                Genre genre = genreDAO.getGenreById(genreId);
                genres.add(genre);
            }
            resultSet.close();
            statement.close();
            return genres;
        } catch(SQLException e) {
            System.out.println("You have this error:");
            e.printStackTrace();
        }
        return null;
    }
}