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
import javax.inject.Named;

import com.grayfox.server.dao.CategoryDao;
import com.grayfox.server.dao.PoiDao;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PoiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoiService.class);

    @Inject @Named("poiLocalDbDao")       private PoiDao poiLocalDbDao;
    @Inject @Named("poiRemoteDbDao")      private PoiDao poiRemoteDbDao;
    @Inject @Named("categoryLocalDbDao")  private CategoryDao categoryLocalDbDao;
    @Inject @Named("categoryRemoteDbDao") private CategoryDao categoryRemoteDbDao;

    @Transactional
    public void addPois(Location... locations) {
        LOGGER.info("Retrieving POIs from Foursquare...");
        List<Poi> pois = poiRemoteDbDao.findNearLocations(locations);
        Set<Category> categories = new HashSet<>();
        pois.forEach(poi -> categories.addAll(poi.getCategories()));
        LOGGER.info("Saving POIs...");
        for (Category category : categories) {
            if (!categoryLocalDbDao.exists(category.getFoursquareId())) categoryLocalDbDao.save(category);
            else categoryLocalDbDao.update(category);
        }
        for (Poi poi : pois) {
            if (!poiLocalDbDao.exists(poi.getFoursquareId())) poiLocalDbDao.save(poi);
            else poiLocalDbDao.update(poi);
        }
        LOGGER.info("POIs saved");
    }

    @Transactional
    public void updatePois() {
        List<Poi> pois = poiLocalDbDao.findAll();
        LOGGER.info("Updating POIs info from Foursquare...");
        for (Poi poi : pois) {
            Poi temp = poiRemoteDbDao.findByFoursquareId(poi.getFoursquareId());
            poi.setName(temp.getName());
            poi.setLocation(temp.getLocation());
            poi.setFoursquareId(temp.getFoursquareId());
            poi.setFoursquareRating(temp.getFoursquareRating());
            poi.setCategories(temp.getCategories());
        }
        LOGGER.info("Updating POIs in database...");
        poiLocalDbDao.update(pois);
        LOGGER.info("POIs updated");
    }

    @Transactional
    public void updateCategories() {
        List<Category> categories = categoryLocalDbDao.findAll();
        LOGGER.info("Updating Categories info from Foursquare...");
        for (Category category : categories) {
            Category temp = categoryRemoteDbDao.findByFoursquareId(category.getFoursquareId());
            category.setDefaultName(temp.getDefaultName());
            category.setSpanishName(temp.getSpanishName());
            category.setIconUrl(temp.getIconUrl());
            category.setFoursquareId(temp.getFoursquareId());
        }
        LOGGER.info("Updating Categories in database...");
        categoryLocalDbDao.update(categories);
        LOGGER.info("Categories updated");
    }

    @Transactional
    public void updateAll() {
        updateCategories();
        updatePois();
    }

    @Transactional
    public void deletePois() {
        LOGGER.info("Deleting all POIs from database...");
        poiLocalDbDao.deleteAll();
    }

    @Transactional
    public void deleteCategories() {
        LOGGER.info("Deleting all Categories from database...");
        categoryLocalDbDao.deleteAll();
    }

    @Transactional
    public void deleteAll() {
        deletePois();
        deleteCategories();
    }
}