package TZJanosi.Repository;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;

public class DB {
    private String schema;
    private String user;
    private String password;

    private MariaDbDataSource dataSource;

    public DB(String schema, String user, String password) {
        this.schema = schema;
        this.user = user;
        this.password = password;
        setDataSource();
    }
    private void setDataSource(){
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/"+schema);
            dataSource.setUser(user);
            dataSource.setPassword(password);
        }
        catch (SQLException se) {
            throw new IllegalStateException("Can not create data source", se);
        }
    }

    public MariaDbDataSource getDataSource() {
        return dataSource;
    }
}
