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
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

@Repository
public class PoiJdbcDao extends JdbcDao {

    public List<Poi> fetchAll() {
        List<Poi> pois = getJdbcTemplate().query(getQuery("allPois"), 
                (ResultSet rs, int i) -> {
                    Poi poi = new Poi();
                    poi.setName(rs.getString(1));
                    poi.setLocation(new Location());
                    poi.getLocation().setLatitude(rs.getDouble(2));
                    poi.getLocation().setLongitude(rs.getDouble(3));
                    poi.setFoursquareId(rs.getString(4));
                    poi.setFoursquareRating(rs.getDouble(5));
                    return poi;
                });
        pois.forEach(poi -> poi.setCategories(new HashSet<>(fetchCategoriesByPoiFoursquareId(poi.getFoursquareId()))));
        return pois;
    }

    public void update(Collection<Poi> pois) {
        pois.forEach(poi -> {
            getJdbcTemplate().update(getQuery("updatePoi"), poi.getFoursquareId(), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareRating());
            getJdbcTemplate().update(getQuery("deleteIsLinks"), poi.getFoursquareId());
            poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("createIsLink"), poi.getFoursquareId(), category.getFoursquareId()));
        });
    }

    public void saveOrUpdate(Collection<Poi> pois) {
        pois.forEach(poi -> {
            List<Boolean> exists = getJdbcTemplate().queryForList(getQuery("existsPoi"), Boolean.class, poi.getFoursquareId());
            if (exists.isEmpty()) getJdbcTemplate().update(getQuery("createPoi"), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareId(), poi.getFoursquareRating());
            else getJdbcTemplate().update(getQuery("updatePoi"), poi.getFoursquareId(), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareRating());
            getJdbcTemplate().update(getQuery("deleteIsLinks"), poi.getFoursquareId());
            poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("createIsLink"), poi.getFoursquareId(), category.getFoursquareId()));
        });
    }

    public List<Category> fetchCategoriesByPoiFoursquareId(String foursquareId) {
        return getJdbcTemplate().query(getQuery("categoriesByPoiFoursquareId"), 
                (ResultSet rs, int i) -> {
                    Category category = new Category();
                    category.setDefaultName(rs.getString(1));
                    category.setSpanishName(rs.getString(2));
                    category.setIconUrl(rs.getString(3));
                    category.setFoursquareId(rs.getString(4));
                    return category;
                }, foursquareId);
    }
}