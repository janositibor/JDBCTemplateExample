package TZJanosi.Model;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ActorRowMapper implements RowMapper<Actor> {

    @Override
    public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        int yob = rs.getInt("yob");
        return new Actor(id, name, yob);
    }
}
