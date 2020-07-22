package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
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

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
            jdbcTemplate.update("delete from user_roles where user_id=?", user.getId());
            setRoles(user);
        } else {
            return null;
        }
        return user;
    }

    private void setRoles(User user) {
        List<Role> roles = new ArrayList<>(user.getRoles());
        int userId = user.getId();
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
        UserRowMapper mapper = new UserRowMapper();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?",
                mapper, id);
        return DataAccessUtils.singleResult(mapper.getResult());
    }

    @Override
    public User getByEmail(String email) {
        UserRowMapper mapper = new UserRowMapper();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?",
                mapper, email);
        return DataAccessUtils.singleResult(mapper.getResult());
    }

    @Override
    public List<User> getAll() {
        UserRowMapper mapper = new UserRowMapper();
        jdbcTemplate.query("SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email",
                mapper);
        return mapper.getResult();
    }

    private static final class UserRowMapper implements RowCallbackHandler {
        private final Map<Integer, User> map = new HashMap<>();
        private final List<User> result = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("id");
            User user = map.computeIfAbsent(id, i -> new User());
            if (user.isNew()) {
                user.setId(id);
                user.setRoles(new HashSet<>());
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRegistered(rs.getDate("registered"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
            }
            String dbRole = rs.getString("role");
            if (dbRole != null) {
                Role role = Role.valueOf(dbRole);
                Set<Role> roles = user.getRoles();
                roles.add(role);
                user.setRoles(roles);
            }
            if (!result.contains(user)) {
                result.add(user);
            }
        }

        public List<User> getResult() {
            return result;
        }
    }
}
