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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import com.grayfox.server.dao.jdbc.CategoryJdbcDao;
import com.grayfox.server.dao.jdbc.PoiJdbcDao;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;
import com.grayfox.server.test.config.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@TransactionConfiguration(defaultRollback = true)
public class PoiServiceTest {

    @Inject private PoiService poiService;
    @Inject private PoiJdbcDao poiJdbcDao;
    @Inject private CategoryJdbcDao categoryJdbcDao;

    @Before
    public void setUp() {
        assertThat(poiService).isNotNull();
    }

    @Test
    @Transactional
    public void testAddPois() {
        poiService.addPois(Location.parse("19.054369,-98.283627"));

        Poi p1 = new Poi();
        p1.setFoursquareId("4c09270ea1b32d7f172297f0");
        p1.setName("Las Quekas");
        p1.setLocation(Location.parse("19.050907767250518,-98.28156293330026"));
        p1.setFoursquareRating(8.5);

        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d143941735");
        category.setDefaultName("Breakfast Spot");
        category.setSpanishName("Café");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");
        p1.setCategories(new HashSet<>(Arrays.asList(category)));

        Poi p2 = new Poi();
        p2.setFoursquareId("51088c2ce4b0c409707a6897");
        p2.setName("Los Choripanes");
        p2.setLocation(Location.parse("19.049072265625,-98.28199768066406"));
        p2.setFoursquareRating(8.6);

        category = new Category();
        category.setFoursquareId("4bf58dd8d48988d107941735");
        category.setDefaultName("Argentinian Restaurant");
        category.setSpanishName("Restaurante argentino");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");
        p2.setCategories(new HashSet<>(Arrays.asList(category)));

        assertThat(poiJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(Arrays.asList(p1, p2));
    }

    @Test
    @Transactional
    public void testUpdatePois() {
        Poi poi = new Poi();
        poi.setFoursquareId("4c09270ea1b32d7f172297f0");
        poi.setName("NOT UPDATED");
        poi.setLocation(Location.parse("19.34,-98.745"));
        poi.setFoursquareRating(6.4);

        Category category1 = new Category();
        category1.setFoursquareId("4bf58dd8d48988d143941735");
        category1.setDefaultName("Breakfast Spot");
        category1.setSpanishName("Café");
        category1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");

        Category category2 = new Category();
        category2.setFoursquareId("4bf58dd8d48988d107941735");
        category2.setDefaultName("Argentinian Restaurant");
        category2.setSpanishName("Restaurante argentino");
        category2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");
        poi.setCategories(new HashSet<>(Arrays.asList(category2)));

        categoryJdbcDao.saveOrUpdate(Arrays.asList(category1, category2));
        poiJdbcDao.saveOrUpdate(Arrays.asList(poi));
        poiService.updatePois();

        poi.setFoursquareId("4c09270ea1b32d7f172297f0");
        poi.setName("Las Quekas");
        poi.setLocation(Location.parse("19.050907767250518,-98.28156293330026"));
        poi.setFoursquareRating(8.5);
        poi.setCategories(new HashSet<>(Arrays.asList(category1)));

        assertThat(poiJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(Arrays.asList(poi));
    }

    @Test
    @Transactional
    public void testUpdateCategories() {
        Category category1 = new Category();
        category1.setFoursquareId("4bf58dd8d48988d143941735");
        category1.setDefaultName("NOT UPDATED");
        category1.setSpanishName("NOT UPDATED");
        category1.setIconUrl("https://88.png");

        Category category2 = new Category();
        category2.setFoursquareId("4bf58dd8d48988d107941735");
        category2.setDefaultName("NOT UPDATED");
        category2.setSpanishName("NOT UPDATED");
        category2.setIconUrl("https://88.png");

        List<Category> expectedCategories = Arrays.asList(category1, category2);

        categoryJdbcDao.saveOrUpdate(expectedCategories);
        poiService.updateCategories();

        category1.setFoursquareId("4bf58dd8d48988d143941735");
        category1.setDefaultName("Breakfast Spot");
        category1.setSpanishName("Café");
        category1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");

        category2.setFoursquareId("4bf58dd8d48988d107941735");
        category2.setDefaultName("Argentinian Restaurant");
        category2.setSpanishName("Restaurante argentino");
        category2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedCategories);
    }
}