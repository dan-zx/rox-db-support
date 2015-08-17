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
package com.grayfox.server.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grayfox.server.dao.foursquare.CategoryFoursquareDao;
import com.grayfox.server.dao.foursquare.PoiFoursquareDao;
import com.grayfox.server.dao.jdbc.CategoryJdbcDao;
import com.grayfox.server.dao.jdbc.PoiJdbcDao;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

@Service
public class PoiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoiService.class);

    @Inject private PoiJdbcDao poiJdbcDao;
    @Inject private PoiFoursquareDao poiFoursquareDao;
    @Inject private CategoryJdbcDao categoryJdbcDao;
    @Inject private CategoryFoursquareDao categoryFoursquareDao;

    @Transactional
    public void addPois(Location... locations) {
        LOGGER.info("Retrieving POIs from Foursquare...");
        List<Poi> pois = poiFoursquareDao.findNearLocations(locations);
        Set<Category> categories = new HashSet<>();
        pois.forEach(poi -> categories.addAll(poi.getCategories()));
        LOGGER.info("Saving POIs...");
        for (Category category : categories) {
            if (!categoryJdbcDao.exists(category.getFoursquareId())) categoryJdbcDao.save(category);
            else categoryJdbcDao.update(category);
        }
        for (Poi poi : pois) {
            if (!poiJdbcDao.exists(poi.getFoursquareId())) poiJdbcDao.save(poi);
            else poiJdbcDao.update(poi);
        }
        LOGGER.info("POIs saved");
    }

    @Transactional
    public void updatePois() {
        List<Poi> pois = poiJdbcDao.findAll();
        LOGGER.info("Updating POIs info from Foursquare...");
        for (Poi poi : pois) {
            Poi temp = poiFoursquareDao.findByFoursquareId(poi.getFoursquareId());
            poi.setName(temp.getName());
            poi.setLocation(temp.getLocation());
            poi.setFoursquareId(temp.getFoursquareId());
            poi.setFoursquareRating(temp.getFoursquareRating());
            poi.setCategories(temp.getCategories());
        }
        LOGGER.info("Updating POIs in database...");
        poiJdbcDao.update(pois);
        LOGGER.info("POIs updated");
    }

    @Transactional
    public void updateCategories() {
        List<Category> categories = categoryJdbcDao.findAll();
        LOGGER.info("Updating Categories info from Foursquare...");
        for (Category category : categories) {
            Category temp = categoryFoursquareDao.findByFoursquareId(category.getFoursquareId());
            category.setDefaultName(temp.getDefaultName());
            category.setSpanishName(temp.getSpanishName());
            category.setIconUrl(temp.getIconUrl());
            category.setFoursquareId(temp.getFoursquareId());
        }
        LOGGER.info("Updating Categories in database...");
        categoryJdbcDao.update(categories);
        LOGGER.info("Categories updated");
    }
}