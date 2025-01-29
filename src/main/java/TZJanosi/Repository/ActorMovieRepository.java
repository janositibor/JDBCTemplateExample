package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import TZJanosi.Model.Movie;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActorMovieRepository {
    private MariaDbDataSource dataSource;

    public ActorMovieRepository(MariaDbDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Actor actor, Movie movie){
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("insert into actors_movies (actor_id,movie_id) values (?,?)")) {
            stmt.setLong(1, actor.getId());
            stmt.setLong(2, movie.getId());
            stmt.executeUpdate();
        }
        catch (SQLException se) {
            throw new IllegalStateException("Cannot insert into actors_movies", se);
        }
    }
    public List<Actor> findActorsForMovie(Movie movie) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("select actors.id AS id, actors.name AS `name`, actors.yob AS yob from actors JOIN actors_movies ON actors.id=actors_movies.actor_id WHERE actors_movies.movie_id=? order by actors.id")) {
            stmt.setLong(1, movie.getId());
            List<Actor> output = getActorsFromStatement(stmt);
            return output;
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select ratings", se);
        }
    }

    private List<Actor> getActorsFromStatement(PreparedStatement stmt) throws SQLException {
        List<Actor> output = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long id= rs.getLong("id");
            String name = rs.getString("name");
            int yob = rs.getInt("yob");
            output.add(new Actor(id,name,yob));
        }
        return output;
    }

    public List<Movie> findMoviesForActor(Actor actor) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("select movies.id AS id, movies.title AS title, movies.release_date AS release_date from movies JOIN actors_movies ON movies.id=actors_movies.movie_id WHERE actors_movies.actor_id=? order by movies.id")) {
            stmt.setLong(1, actor.getId());
            List<Movie> output = getMoviesFromStatement(stmt);
            return output;
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select ratings", se);
        }
    }

    private List<Movie> getMoviesFromStatement(PreparedStatement stmt) throws SQLException {
        List<Movie> output = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long id= rs.getLong("id");
            String title = rs.getString("title");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            output.add(new Movie(id,title,releaseDate));
        }
        return output;
    }
}
