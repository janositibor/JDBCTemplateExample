package TZJanosi.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface Repository {
    default Optional<Long> executeAndGetGeneratedKey(PreparedStatement stmt){
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return Optional.of(rs.getLong(1));
            }
            return Optional.empty();
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by inserting actor", sqle);
        }
    }

}
