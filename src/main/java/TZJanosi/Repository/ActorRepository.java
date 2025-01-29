package TZJanosi.Repository;

import TZJanosi.Model.Actor;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorRepository implements Repository{
    private MariaDbDataSource dataSource;

    public ActorRepository(MariaDbDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Optional<Long> saveBasicAndGetGeneratedKey(Actor actor) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into actors(name,yob) values (?,?)",
                     Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, actor.getName());
            stmt.setInt(2, actor.getYob());
            stmt.executeUpdate();
            return executeAndGetGeneratedKey(stmt);
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert actor: "+actor, sqle);
        }
    }

    public Optional<Actor> findActor(Actor actor) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("select id, name,yob from actors where name LIKE ? and yob=?")){
            stmt.setString(1, actor.getName());
            stmt.setInt(2, actor.getYob());
            return actorFromStatement(stmt);
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error in findActor: "+actor, sqle);
        }
    }
    private Optional<Actor> actorFromStatement(PreparedStatement stmt) throws SQLException{
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                long id= rs.getLong("id");
                String name = rs.getString("name");
                int yob = rs.getInt("yob");
                return Optional.of(new Actor(id, name, yob));
            }
            return Optional.empty();
        }
    }
    public List<Actor> findAllActor(){
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select id, name, yob from actors order by id")) {
            List<Actor> output = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int yob = rs.getInt("yob");
                output.add(new Actor(id, name, yob));
            }
            return output;
        }
        catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }

    }
}
