package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import TZJanosi.Model.ActorRowMapper;
import TZJanosi.Model.Movie;
import TZJanosi.Model.MovieRowMapper;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class MovieRepository implements Repository{
    private JdbcTemplate jdbcTemplate;

    public MovieRepository(MariaDbDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public Optional<Long> saveBasicAndGetGeneratedKey(Movie movie) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
                                @Override
                                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                    PreparedStatement ps =
                                            connection.prepareStatement("insert into movies(title,release_date) values (?,?)",
                                                    Statement.RETURN_GENERATED_KEYS);
                                    ps.setString(1, movie.getTitle());
                                    ps.setDate(2, Date.valueOf(movie.getReleaseDate()));;
                                    return ps;
                                }
                            }, keyHolder
        );

        return Optional.ofNullable(keyHolder.getKey().longValue());
    }
    public Optional<Movie> findMovie(Movie movie) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select movies.id AS id, movies.title AS title, movies.release_date AS release_date, COUNT(ratings.rating) AS number_of_ratings, AVG(ratings.rating) AS average_of_ratings from movies LEFT JOIN ratings ON movies.id=ratings.movie_id WHERE movies.title LIKE ? AND movies.release_date=?"
                ,new MovieRowMapper(true),movie.getTitle(),Date.valueOf(movie.getReleaseDate())));
    }



}
