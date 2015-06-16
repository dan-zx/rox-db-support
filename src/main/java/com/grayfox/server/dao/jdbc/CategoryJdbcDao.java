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

import org.springframework.stereotype.Repository;

import com.grayfox.server.domain.Category;

@Repository
public class CategoryJdbcDao extends JdbcDao {

    public List<Category> fetchAll() {
        return getJdbcTemplate().query(getQuery("allCategories"), 
                (ResultSet rs, int i) -> {
                    Category category = new Category();
                    category.setDefaultName(rs.getString(1));
                    category.setSpanishName(rs.getString(2));
                    category.setIconUrl(rs.getString(3));
                    category.setFoursquareId(rs.getString(4));
                    return category;
                });
    }

    public void update(Collection<Category> categories) {
        categories.forEach(category -> getJdbcTemplate().update(getQuery("updateCategory"), category.getFoursquareId(), category.getDefaultName(), category.getSpanishName(), category.getIconUrl()));
    }

    public void saveOrUpdate(Collection<Category> categories) {
        categories.forEach(category -> {
            List<Boolean> exists = getJdbcTemplate().queryForList(getQuery("existsCategory"), Boolean.class, category.getFoursquareId());
            if (exists.isEmpty()) getJdbcTemplate().update(getQuery("createCategory"), category.getDefaultName(), category.getSpanishName(), category.getIconUrl(), category.getFoursquareId());
            else getJdbcTemplate().update(getQuery("updateCategory"), category.getFoursquareId(), category.getDefaultName(), category.getSpanishName(), category.getIconUrl());
        });
    }
}