package TZJanosi.Repository;

import TZJanosi.Model.Movie;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository implements Repository {
    private MariaDbDataSource dataSource;

    public RatingRepository(MariaDbDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Movie movie, Integer rating) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("insert into ratings (movie_id,rating) values (?,?)")) {
            stmt.setLong(1, movie.getId());
            stmt.setInt(2, rating);
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot insert into rating", se);
        }
    }

    public void save(Movie movie, List<Integer> ratings) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("insert into ratings (movie_id,rating) values (?,?)")) {

            conn.setAutoCommit(false);
            try {
                for (Integer rating : ratings) {
                    if (rating > 10) {
                        throw new IllegalArgumentException("Invalid rating: "+rating);
                    }
                    stmt.setLong(1, movie.getId());
                    stmt.setInt(2, rating);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (IllegalArgumentException iae) {
                conn.rollback();
                throw new IllegalStateException(iae.getMessage());
            }
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot insert into rating", se);
        }
    }

    public List<Integer> findRatingsForMovie(Movie movie) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("select rating from ratings WHERE movie_id=? order by id")) {
            stmt.setLong(1, movie.getId());
            List<Integer> output = getRatingsFromStatement(stmt);
            return output;
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select ratings", se);
        }
    }

    private List<Integer> getRatingsFromStatement(PreparedStatement stmt) throws SQLException {
        List<Integer> output = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int rating = rs.getInt("rating");
            output.add(rating);
        }
        return output;
    }
}
