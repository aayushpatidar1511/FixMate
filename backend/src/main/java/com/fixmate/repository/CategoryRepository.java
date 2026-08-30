package com.fixmate.repository;

import com.fixmate.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setCategoryId(rs.getLong("category_id"));
        c.setName(rs.getString("name"));
        c.setSlug(rs.getString("slug"));
        c.setDescription(rs.getString("description"));
        c.setIcon(rs.getString("icon"));
        c.setIsActive(rs.getBoolean("is_active"));
        c.setDisplayOrder(rs.getInt("display_order"));
        c.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return c;
    };

    public List<Category> findAllActive() {
        String sql = "SELECT * FROM categories WHERE is_active = TRUE ORDER BY display_order ASC";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public List<Category> findAll() {
        String sql = "SELECT * FROM categories ORDER BY display_order ASC";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Optional<Category> findById(Long categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try {
            Category category = jdbcTemplate.queryForObject(sql, categoryRowMapper, categoryId);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long save(Category category) {
        String sql = "INSERT INTO categories (name, slug, description, icon, is_active, display_order) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getSlug());
            ps.setString(3, category.getDescription());
            ps.setString(4, category.getIcon() != null ? category.getIcon() : "wrench");
            ps.setBoolean(5, category.getIsActive() != null ? category.getIsActive() : true);
            ps.setInt(6, category.getDisplayOrder() != null ? category.getDisplayOrder() : 0);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to save category");
    }

    public void update(Category category) {
        String sql = "UPDATE categories SET name = ?, slug = ?, description = ?, icon = ?, is_active = ?, display_order = ? WHERE category_id = ?";
        jdbcTemplate.update(sql, category.getName(), category.getSlug(), category.getDescription(),
                category.getIcon(), category.getIsActive(), category.getDisplayOrder(), category.getCategoryId());
    }

    public void delete(Long categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, categoryId);
    }
}
