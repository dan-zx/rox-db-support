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

import com.grayfox.server.dao.PoiDao;
import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

import org.springframework.stereotype.Repository;

@Repository("poiLocalDbDao")
public class PoiJdbcDao extends JdbcDao implements PoiDao {

    @Override
    public void save(Poi poi) {
        getJdbcTemplate().update(getQuery("Poi.save"), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareId(), poi.getFoursquareRating());
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("Poi.findIdByFoursquareId"), Long.class, poi.getFoursquareId());
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        poi.setId(ids.get(0));
        poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("Poi.createIsRelationship"), poi.getFoursquareId(), category.getFoursquareId()));
    }

    @Override
    public void save(Collection<Poi> pois) {
        pois.forEach(poi -> save(poi));
    }

    @Override
    public List<Poi> findAll() {
        List<Poi> pois = getJdbcTemplate().query(getQuery("Poi.findAll"), 
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
        pois.forEach(poi -> poi.setCategories(new HashSet<>(findCategoriesByPoiFoursquareId(poi.getFoursquareId()))));
        return pois;
    }

    @Override
    public List<Poi> findNearLocations(Location... locations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Poi findByFoursquareId(String foursquareId) {
        List<Poi> pois = getJdbcTemplate().query(getQuery("Poi.findByFoursquareId"), 
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
                }, foursquareId);
        if (pois.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        if (!pois.isEmpty()) {
            Poi poi = pois.get(0); 
            poi.setCategories(new HashSet<>(findCategoriesByPoiFoursquareId(poi.getFoursquareId())));
            return poi;
        }
        return null;
    }

    private List<Category> findCategoriesByPoiFoursquareId(String foursquareId) {
        return getJdbcTemplate().query(getQuery("Category.findByPoiFoursquareId"), 
                (ResultSet rs, int i) -> {
                    Category category = new Category();
                    category.setId(rs.getLong(1));
                    category.setDefaultName(rs.getString(2));
                    category.setSpanishName(rs.getString(3));
                    category.setIconUrl(rs.getString(4));
                    category.setFoursquareId(rs.getString(5));
                    return category;
                }, foursquareId);
    }

    @Override
    public boolean exists(String foursquareId) {
        List<Long> ids = getJdbcTemplate().queryForList(getQuery("Poi.findIdByFoursquareId"), Long.class, foursquareId);
        if (ids.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        return !ids.isEmpty();
    }

    @Override
    public void update(Poi poi) {
        getJdbcTemplate().update(getQuery("Poi.update"), poi.getFoursquareId(), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareRating());
        getJdbcTemplate().update(getQuery("Poi.deleteIsRelationships"), poi.getFoursquareId());
        poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("Poi.createIsRelationship"), poi.getFoursquareId(), category.getFoursquareId()));
    }

    @Override
    public void update(Collection<Poi> pois) {
        pois.forEach(poi -> update(poi));
    }

    @Override
    public void delete(Poi poi) {
        getJdbcTemplate().update(getQuery("Poi.delete"), poi.getFoursquareId());
    }

    @Override
    public void delete(Collection<Poi> pois) {
        pois.forEach(poi -> delete(poi));
    }

    @Override
    public void deleteAll() {
        getJdbcTemplate().update(getQuery("Poi.deleteAll"));
    }
}