package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import TZJanosi.Model.ActorRowMapper;
import TZJanosi.Model.Movie;
import TZJanosi.Model.MovieRowMapper;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActorMovieRepository {
    private JdbcTemplate jdbcTemplate;

    public ActorMovieRepository(MariaDbDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(Actor actor, Movie movie){
        jdbcTemplate.update("insert into actors_movies (actor_id,movie_id) values (?,?)",actor.getId(),movie.getId());
    }
    public List<Actor> findActorsForMovie(Movie movie) {
            return jdbcTemplate.query("select actors.id AS id, actors.name AS `name`, actors.yob AS yob from actors JOIN actors_movies ON actors.id=actors_movies.actor_id WHERE actors_movies.movie_id=? order by actors.id"
                    , new ActorRowMapper()
                    ,movie.getId());
    }



    public List<Movie> findMoviesForActor(Actor actor) {
        return jdbcTemplate.query("select movies.id AS id, movies.title AS title, movies.release_date AS release_date from movies JOIN actors_movies ON movies.id=actors_movies.movie_id WHERE actors_movies.actor_id=? order by movies.id"
                , new MovieRowMapper(false)
                ,actor.getId());
    }


}
