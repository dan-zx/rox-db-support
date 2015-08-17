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

import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

import org.springframework.stereotype.Repository;

@Repository
public class PoiJdbcDao extends JdbcDao {

    public void save(Poi poi) {
        getJdbcTemplate().update(getQuery("createPoi"), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareId(), poi.getFoursquareRating());
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("poiIdByFoursquareId"), Long.class, poi.getFoursquareId());
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        poi.setId(ids.get(0));
        poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("createIsLink"), poi.getFoursquareId(), category.getFoursquareId()));
    }

    public void save(Collection<Poi> pois) {
        pois.forEach(poi -> save(poi));
    }

    public List<Poi> findAll() {
        List<Poi> pois = getJdbcTemplate().query(getQuery("allPois"), 
                (ResultSet rs, int i) -> {
                    Poi poi = new Poi();
                    poi.setId(rs.getLong(1));
                    poi.setName(rs.getString(2));
                    poi.setLocation(new Location());
                    poi.getLocation().setLatitude(rs.getDouble(3));
                    poi.getLocation().setLongitude(rs.getDouble(4));
                    poi.setFoursquareId(rs.getString(5));
                    poi.setFoursquareRating(rs.getDouble(6));
                    return poi;
                });
        pois.forEach(poi -> {
            List<Category> categories = getJdbcTemplate().query(getQuery("categoriesByPoiFoursquareId"), 
                    (ResultSet rs, int i) -> {
                        Category category = new Category();
                        category.setId(rs.getLong(1));
                        category.setDefaultName(rs.getString(2));
                        category.setSpanishName(rs.getString(3));
                        category.setIconUrl(rs.getString(4));
                        category.setFoursquareId(rs.getString(5));
                        return category;
                    }, poi.getFoursquareId());
            poi.setCategories(new HashSet<>(categories));
        });
        return pois;
    }

    public boolean exists(String foursquareId) {
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("poiIdByFoursquareId"), Long.class, foursquareId);
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        return !ids.isEmpty();
    }

    public void update(Poi poi) {
        getJdbcTemplate().update(getQuery("updatePoi"), poi.getFoursquareId(), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareRating());
        getJdbcTemplate().update(getQuery("deleteIsLinks"), poi.getFoursquareId());
        poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("createIsLink"), poi.getFoursquareId(), category.getFoursquareId()));
    }

    public void update(Collection<Poi> pois) {
        pois.forEach(poi -> update(poi));
    }
}