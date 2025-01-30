package TZJanosi.Repository;

import TZJanosi.Model.Movie;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository implements Repository {
    private JdbcTemplate jdbcTemplate;

    public RatingRepository(MariaDbDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(Movie movie, Integer rating) {
        jdbcTemplate.update("insert into ratings (movie_id,rating) values (?,?)",movie.getId(),rating);
    }

    public void save(Movie movie, List<Integer> ratings) {
        for (Integer rating : ratings) {
            if (rating > 10) {
                throw new IllegalStateException("Invalid rating: " + rating);
            }
        }
        String sql="insert into ratings (movie_id,rating) values (?,?)";
        List<Object[]> batchArgs = ratings.stream()
                .map(x -> new Object[]{movie.getId(),x})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public List<Integer> findRatingsForMovie(Movie movie) {
        return jdbcTemplate.query("select rating from ratings WHERE movie_id=? order by id",
        (rs, i) -> rs.getInt("rating"),
                movie.getId());
    }
}
