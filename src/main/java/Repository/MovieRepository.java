package Repository;

import Model.MovieRowMapper;
import Model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class MovieRepository{
    private JdbcTemplate jdbcTemplate;

    public MovieRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public Optional<Long> saveBasicAndGetGeneratedKey(Movie movie){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> preparedStatementForInsert(con,movie), keyHolder);
        return Optional.ofNullable(keyHolder.getKey().longValue());
    }
    private PreparedStatement preparedStatementForInsert(Connection con, Movie movie) throws SQLException {
        String sql = "insert into movies(title,release_date) values (?,?)";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, movie.getTitle());
        ps.setDate(2, Date.valueOf(movie.getReleaseDate()));;
        return ps;
    }
    public Optional<Movie> findMovie(Movie movie) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select movies.id AS id, movies.title AS title, movies.release_date AS release_date, COUNT(ratings.rating) AS number_of_ratings, AVG(ratings.rating) AS average_of_ratings from movies LEFT JOIN ratings ON movies.id=ratings.movie_id WHERE movies.title LIKE ? AND movies.release_date=?"
                ,new MovieRowMapper(true),movie.getTitle(),Date.valueOf(movie.getReleaseDate())));
    }



}
