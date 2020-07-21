package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final static Map<Integer, User> map = new HashMap<>();

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt(1);
        map.putIfAbsent(id, new User());
        User user = map.get(id);
        user.setId(rs.getInt(1));
        user.setName(rs.getString(2));
        user.setEmail(rs.getString(3));
        user.setPassword(rs.getString(4));
        user.setRegistered(rs.getDate(5));
        user.setEnabled(rs.getBoolean(6));
        user.setCaloriesPerDay(rs.getInt(7));
        Role role = Role.valueOf(rs.getString(9));
        Set<Role> roles = user.getRoles() == null ? new HashSet<>() : user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        return user;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            setRoles(user);
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) != 0) {
            setRoles(user);
        } else {
            return null;
        }
        return user;
    }

    private void setRoles(User user) {
        List<Role> roles = new ArrayList<>(user.getRoles());
        int userId = user.getId();
        jdbcTemplate.update("delete from user_roles where user_id=?", userId);
        jdbcTemplate.batchUpdate("insert into user_roles (user_id, role) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, roles.get(i).toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                }
        );
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        map.clear();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?",
                JdbcUserRepository::mapRow, id);
        return DataAccessUtils.singleResult(map.values());
    }

    @Override
    public User getByEmail(String email) {
        map.clear();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?",
                JdbcUserRepository::mapRow, email);
        return DataAccessUtils.singleResult(map.values());
    }

    @Override
    public List<User> getAll() {
        map.clear();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email",
                JdbcUserRepository::mapRow);
        return new ArrayList<>(map.values());
    }
}
