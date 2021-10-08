package com.intellias.db;

import com.intellias.api.User;
import com.intellias.core.UserReducer;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import ru.vyarus.guicey.jdbi3.installer.repository.JdbiRepository;
import ru.vyarus.guicey.jdbi3.tx.InTransaction;

@JdbiRepository
public interface UsersDAO {

    @SqlUpdate("CREATE TABLE users (id SERIAL, name VARCHAR(30) NOT NULL, PRIMARY KEY(id))")
    void createTable();

    @SqlUpdate("INSERT INTO users(id, name) VALUES (:id, :name)"
            + "ON CONFLICT ON CONSTRAINT users_pkey DO UPDATE SET name=:name")
    @GetGeneratedKeys
    long insertUser(@BindBean User user);

    @SqlUpdate("UPDATE users SET name=:name WHERE id=:id")
    void updateUser(@BindBean User user);

    @SqlUpdate("DELETE FROM users WHERE id=:id")
    void deleteUser(@Bind("id") long id);

    @InTransaction
    @SqlQuery("SELECT u.id AS u_id, u.name AS u_name, r.id , r.name "
            + "FROM users AS u LEFT JOIN roles AS r ON u.id=r.user_id "
            + "ORDER by u.name")
    @RegisterBeanMapper(value = User.class, prefix = "u")
    @UseRowReducer(UserReducer.class)
    List<User> listUsers();

    @SqlQuery("SELECT EXISTS(SELECT * FROM users WHERE id=:id LIMIT 1)")
    boolean userExists(@Bind("id") long id);

    @InTransaction
    @SqlQuery("SELECT u.id AS u_id, u.name AS u_name, r.id AS r_id, r.name AS r_name "
            + "FROM users AS u LEFT JOIN roles AS r ON u.id=r.user_id "
            + "WHERE u.id=:id")
    @RegisterBeanMapper(value = User.class, prefix = "u")
    @UseRowReducer(UserReducer.class)
    User getUserById(@Bind("id") long id);
}
