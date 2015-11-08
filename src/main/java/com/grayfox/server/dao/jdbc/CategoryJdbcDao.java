/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.server.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.grayfox.server.dao.CategoryDao;
import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("categoryLocalDao")
public class CategoryJdbcDao extends JdbcDao implements CategoryDao {

    @Override
    public void save(Category category) {
        getJdbcTemplate().update(getQuery("Category.create"), category.getDefaultName(), category.getSpanishName(), category.getIconUrl(), category.getFoursquareId());
        category.setId(getJdbcTemplate().queryForObject(getQuery("Category.findIdByFoursquareId"), Long.class, category.getFoursquareId()));
    }

    @Override
    public void save(Collection<Category> categories) {
        categories.forEach(category -> save(category));
    }

    @Override
    public List<Category> findAll() {
        return getJdbcTemplate().query(getQuery("Category.findAll"), new CategoryMapper());
    }

    protected List<Category> findByPoiFoursquareId(String foursquareId) {
        return getJdbcTemplate().query(getQuery("Category.findByPoiFoursquareId"), new CategoryMapper(), foursquareId);
    }

    @Override
    public Category findByFoursquareId(String foursquareId) {
        List<Category> categories = getJdbcTemplate().query(getQuery("Category.findByFoursquareId"), new CategoryMapper(), foursquareId);
        if (categories.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        return categories.isEmpty() ? null : categories.get(0);
    }

    @Override
    public boolean exists(String foursquareId) {
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("Category.findIdByFoursquareId"), Long.class, foursquareId);
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        return !ids.isEmpty();
    }

    @Override
    public void update(Category category) {
        getJdbcTemplate().update(getQuery("Category.update"), category.getFoursquareId(), category.getDefaultName(), category.getSpanishName(), category.getIconUrl());
    }

    @Override
    public void update(Collection<Category> categories) {
        categories.forEach(category -> update(category));
    }

    @Override
    public void delete(Category category) {
        getJdbcTemplate().update(getQuery("Category.delete"), category.getFoursquareId());
    }

    @Override
    public void delete(Collection<Category> categories) {
        categories.forEach(category -> delete(category));
    }

    @Override
    public void deleteAll() {
        getJdbcTemplate().update(getQuery("Category.deleteAll"));
    }

    private static class CategoryMapper implements RowMapper<Category> {

        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            int columnIndex = 1;
            category.setId(rs.getLong(columnIndex++));
            category.setDefaultName(rs.getString(columnIndex++));
            category.setSpanishName(rs.getString(columnIndex++));
            category.setIconUrl(rs.getString(columnIndex++));
            category.setFoursquareId(rs.getString(columnIndex++));
            return category;
        }
        
    }
}