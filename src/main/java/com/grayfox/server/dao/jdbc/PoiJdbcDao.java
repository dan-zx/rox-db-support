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
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import com.grayfox.server.dao.DaoException;
import com.grayfox.server.dao.PoiDao;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("poiLocalDao")
public class PoiJdbcDao extends JdbcDao implements PoiDao {

    @Inject private CategoryJdbcDao categoryDao;

    @Override
    public void save(Poi poi) {
        getJdbcTemplate().update(getQuery("Poi.create"), poi.getName(), poi.getLocation().getLatitude(), poi.getLocation().getLongitude(), poi.getFoursquareId(), poi.getFoursquareRating());
        poi.setId(getJdbcTemplate().queryForObject(getQuery("Poi.findIdByFoursquareId"), Long.class, poi.getFoursquareId()));
        poi.getCategories().forEach(category -> getJdbcTemplate().update(getQuery("Poi.createIsRelationship"), poi.getFoursquareId(), category.getFoursquareId()));
    }

    @Override
    public void save(Collection<Poi> pois) {
        pois.forEach(poi -> save(poi));
    }

    @Override
    public List<Poi> findAll() {
        List<Poi> pois = getJdbcTemplate().query(getQuery("Poi.findAll"), new PoiMapper());
        pois.forEach(poi -> poi.setCategories(new HashSet<>(categoryDao.findByPoiFoursquareId(poi.getFoursquareId()))));
        return pois;
    }

    @Override
    public List<Poi> findNearLocations(Location... locations) {
        throw new DaoException.Builder()
            .messageKey("unsupported.operation.error")
            .build();
    }

    @Override
    public Poi findByFoursquareId(String foursquareId) {
        List<Poi> pois = getJdbcTemplate().query(getQuery("Poi.findByFoursquareId"), new PoiMapper(), foursquareId);
        if (pois.size() > 1) {
            throw new DaoException.Builder()
                .messageKey("data.integrity.error")
                .build();
        }
        if (!pois.isEmpty()) {
            Poi poi = pois.get(0); 
            poi.setCategories(new HashSet<>(categoryDao.findByPoiFoursquareId(poi.getFoursquareId())));
            return poi;
        }
        return null;
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

    private static class PoiMapper implements RowMapper<Poi> {

        @Override
        public Poi mapRow(ResultSet rs, int rowNum) throws SQLException {
            Poi poi = new Poi();
            int columnIndex = 1;
            poi.setId(rs.getLong(columnIndex++));
            poi.setName(rs.getString(columnIndex++));
            poi.setLocation(new Location());
            poi.getLocation().setLatitude(rs.getDouble(columnIndex++));
            poi.getLocation().setLongitude(rs.getDouble(columnIndex++));
            poi.setFoursquareId(rs.getString(columnIndex++));
            poi.setFoursquareRating(rs.getDouble(columnIndex++));
            return poi;
        }
    }
}