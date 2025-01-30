package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import TZJanosi.Model.ActorRowMapper;
import TZJanosi.Model.MovieRowMapper;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorRepository implements Repository{
    private JdbcTemplate jdbcTemplate;

    public ActorRepository(MariaDbDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public Optional<Long> saveBasicAndGetGeneratedKey(Actor actor) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
                                @Override
                                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                    PreparedStatement ps =
                                            connection.prepareStatement("insert into actors(name,yob) values (?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                                    ps.setString(1, actor.getName());
                                    ps.setInt(2, actor.getYob());
                                    return ps;
                                }
                            }, keyHolder
        );

        return Optional.ofNullable(keyHolder.getKey().longValue());
    }

    public Optional<Actor> findActor(Actor actor) {
        List<Actor> actors=jdbcTemplate.query("select id, name,yob from actors where name LIKE ? and yob=?"
                , new ActorRowMapper()
                ,actor.getName(),actor.getYob());
        if(actors.size()<1){
            return Optional.empty();
        }
        return Optional.of(actors.get(0));
    }

    public List<Actor> findAllActor(){
            return jdbcTemplate.query("select id, name, yob from actors order by id", new ActorRowMapper());
    }
}
