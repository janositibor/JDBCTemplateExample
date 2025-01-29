package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import TZJanosi.Model.Movie;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class MovieRepository implements Repository{
    private MariaDbDataSource dataSource;

    public MovieRepository(MariaDbDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Optional<Long> saveBasicAndGetGeneratedKey(Movie movie) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into movies(title,release_date) values (?,?)",
                     Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            stmt.executeUpdate();
            return executeAndGetGeneratedKey(stmt);
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert movie: "+movie, sqle);
        }
    }
    public Optional<Movie> findMovie(Movie movie) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("select movies.id AS id, movies.title AS title, movies.release_date AS release_date, COUNT(ratings.rating) AS number_of_ratings, AVG(ratings.rating) AS average_of_ratings from movies LEFT JOIN ratings ON movies.id=ratings.movie_id WHERE movies.title LIKE ? AND movies.release_date=?")){
            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            return movieFromStatement(stmt);
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error in findMovie: "+movie, sqle);
        }
    }
    private Optional<Movie> movieFromStatement(PreparedStatement stmt) throws SQLException{
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                long id= rs.getLong("id");
                if(id==0){
                    return Optional.empty();
                }
                String title = rs.getString("title");
                LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
                int numberOfRatings=rs.getInt("number_of_ratings");
                double averageOfRatings=rs.getDouble("average_of_ratings");

                Movie movie=new Movie(id, title, releaseDate);
                movie.setNumberOfRatings(numberOfRatings);
                movie.setAverageOfRatings(averageOfRatings);
                return Optional.of(movie);
            }
            return Optional.empty();
        }
    }


}
