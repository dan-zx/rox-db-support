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
import java.util.Collection;
import java.util.List;

import com.grayfox.server.dao.CategoryDao;
import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;
import org.springframework.stereotype.Repository;

@Repository("categoryLocalDbDao")
public class CategoryJdbcDao extends JdbcDao implements CategoryDao {

    @Override
    public void save(Category category) {
        getJdbcTemplate().update(getQuery("Category.save"), category.getDefaultName(), category.getSpanishName(), category.getIconUrl(), category.getFoursquareId());
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("Category.findIdByFoursquareId"), Long.class, category.getFoursquareId());
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        category.setId(ids.get(0));
    }

    @Override
    public void save(Collection<Category> categories) {
        categories.forEach(category -> save(category));
    }

    @Override
    public List<Category> findAll() {
        return getJdbcTemplate().query(getQuery("Category.findAll"), 
                (ResultSet rs, int i) -> {
                    Category category = new Category();
                    category.setId(rs.getLong(1));
                    category.setDefaultName(rs.getString(2));
                    category.setSpanishName(rs.getString(3));
                    category.setIconUrl(rs.getString(4));
                    category.setFoursquareId(rs.getString(5));
                    return category;
                });
    }

    @Override
    public Category findByFoursquareId(String foursquareId) {
        List<Category> categories = getJdbcTemplate().query(getQuery("Category.findByFoursquareId"), 
                (ResultSet rs, int i) -> {
                    Category category = new Category();
                    category.setId(rs.getLong(1));
                    category.setDefaultName(rs.getString(2));
                    category.setSpanishName(rs.getString(3));
                    category.setIconUrl(rs.getString(4));
                    category.setFoursquareId(rs.getString(5));
                    return category;
                }, foursquareId);
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
}