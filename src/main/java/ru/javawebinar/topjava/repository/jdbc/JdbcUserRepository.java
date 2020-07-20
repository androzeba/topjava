package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

//    private final static Map<Integer, User> map = new HashMap<>();

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

//    private static Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//        int id = rs.getInt(1);
//        map.putIfAbsent(id, new User());
//        User user = map.get(id);
//        user.setId(rs.getInt(1));
//        user.setName(rs.getString(2));
//        user.setEmail(rs.getString(3));
//        user.setPassword(rs.getString(4));
//        user.setRegistered(rs.getDate(5));
//        user.setEnabled(rs.getBoolean(6));
//        user.setCaloriesPerDay(rs.getInt(7));
//        Role role = Role.valueOf(rs.getString(9));
//        Set<Role> roles = user.getRoles() == null ? new HashSet<>() : user.getRoles();
//        roles.add(role);
//        user.setRoles(roles);
//        return user;
//    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
//        map.clear();
        return jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
//        jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id ORDER BY name, email",
//                JdbcUserRepository::mapRow);
//        return new ArrayList<>(map.values());
    }

//    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
//        int id = rs.getInt(1);
//        map.putIfAbsent(id, new User());
//        User user = map.get(id);
//        user.setId(rs.getInt(1));
//        user.setName(rs.getString(2));
//        user.setEmail(rs.getString(3));
//        user.setPassword(rs.getString(4));
//        user.setRegistered(rs.getDate(5));
//        user.setEnabled(rs.getBoolean(6));
//        user.setCaloriesPerDay(rs.getInt(7));
//        Role role = Role.valueOf(rs.getString(9));
//        Set<Role> roles = user.getRoles() == null ? new HashSet<>() : user.getRoles();
//        roles.add(role);
//        user.setRoles(roles);
//        return user;
//    }
}
