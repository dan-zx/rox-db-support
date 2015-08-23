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
import javax.inject.Named;

import com.grayfox.server.dao.CategoryDao;
import com.grayfox.server.dao.PoiDao;
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
    @Inject @Named("poiLocalDbDao")      private PoiDao poiJdbcDao;
    @Inject @Named("categoryLocalDbDao") private CategoryDao categoryJdbcDao;

    @Before
    public void setUp() {
        assertThat(poiService).isNotNull();
        assertThat(poiJdbcDao).isNotNull();
        assertThat(categoryJdbcDao).isNotNull();
    }

    @Test
    @Transactional
    public void testAddPois() {
        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d143941735");
        category.setDefaultName("NOT UPDATED");
        category.setSpanishName("NOT UPDATED");
        category.setIconUrl("https://88.png");
        
        Poi p1 = new Poi();
        p1.setFoursquareId("4c09270ea1b32d7f172297f0");
        p1.setName("NOT UPDATED");
        p1.setLocation(Location.parse("19.34,-98.745"));
        p1.setFoursquareRating(6.4);
        p1.setCategories(new HashSet<>(Arrays.asList(category)));

        categoryJdbcDao.save(category);

        assertThat(category.getId()).isNotNull();

        poiJdbcDao.save(p1);

        assertThat(p1.getId()).isNotNull();

        poiService.addPois(Location.parse("19.054369,-98.283627"));

        p1.setName("Las Quekas");
        p1.setLocation(Location.parse("19.050907767250518,-98.28156293330026"));
        p1.setFoursquareRating(8.5);

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

        List<Poi> expectedPois = Arrays.asList(p1, p2);
        List<Poi> actualPois = poiJdbcDao.findAll();

        assertThat(actualPois).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedPois);

        for (int i = 0; i < expectedPois.size(); i++) {
            assertThat(actualPois.get(i)).isEqualToIgnoringGivenFields(expectedPois.get(i), "id", "categories");
            assertThat(actualPois.get(i).getCategories()).usingElementComparatorIgnoringFields("id").containsOnlyElementsOf(expectedPois.get(i).getCategories());
        }
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

        categoryJdbcDao.save(Arrays.asList(category1, category2));

        assertThat(category1.getId()).isNotNull();
        assertThat(category2.getId()).isNotNull();

        poiJdbcDao.save(poi);

        assertThat(poi.getId()).isNotNull();

        poiService.updatePois();

        poi.setFoursquareId("4c09270ea1b32d7f172297f0");
        poi.setName("Las Quekas");
        poi.setLocation(Location.parse("19.050907767250518,-98.28156293330026"));
        poi.setFoursquareRating(8.5);
        poi.setCategories(new HashSet<>(Arrays.asList(category1)));

        List<Poi> expectedPois = Arrays.asList(poi);

        assertThat(poiJdbcDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedPois).containsOnlyElementsOf(expectedPois);
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

        categoryJdbcDao.save(expectedCategories);

        assertThat(category1.getId()).isNotNull();
        assertThat(category2.getId()).isNotNull();

        poiService.updateCategories();

        category1.setFoursquareId("4bf58dd8d48988d143941735");
        category1.setDefaultName("Breakfast Spot");
        category1.setSpanishName("Café");
        category1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");

        category2.setFoursquareId("4bf58dd8d48988d107941735");
        category2.setDefaultName("Argentinian Restaurant");
        category2.setSpanishName("Restaurante argentino");
        category2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");

        assertThat(categoryJdbcDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedCategories).containsOnlyElementsOf(expectedCategories);
    }

    @Test
    @Transactional
    public void testDeletePois() {
        Poi poi = new Poi();
        poi.setFoursquareId("34jksdf");
        poi.setName("Name");
        poi.setLocation(Location.parse("19.34,-98.745"));
        poi.setFoursquareRating(6.4);

        Category category1 = new Category();
        category1.setFoursquareId("67jgsjhdf34");
        category1.setDefaultName("Name");
        category1.setSpanishName("Name");
        category1.setIconUrl("https://png");

        Category category2 = new Category();
        category2.setFoursquareId("93hsd37y645");
        category2.setDefaultName("Name");
        category2.setSpanishName("Name");
        category2.setIconUrl("https://png");
        poi.setCategories(new HashSet<>(Arrays.asList(category2)));

        List<Category> expectedCategories = Arrays.asList(category1, category2);
        categoryJdbcDao.save(expectedCategories);

        assertThat(category1.getId()).isNotNull();
        assertThat(category2.getId()).isNotNull();

        poiJdbcDao.save(poi);

        assertThat(poi.getId()).isNotNull();

        poiService.deletePois();
        
        assertThat(poiJdbcDao.findAll()).isNotNull().isEmpty();
        assertThat(categoryJdbcDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedCategories).containsOnlyElementsOf(expectedCategories);
    }

    @Test
    @Transactional
    public void testDeleteCategories() {
        Poi poi = new Poi();
        poi.setFoursquareId("34jksdf");
        poi.setName("Name");
        poi.setLocation(Location.parse("19.34,-98.745"));
        poi.setFoursquareRating(6.4);

        Category category1 = new Category();
        category1.setFoursquareId("67jgsjhdf34");
        category1.setDefaultName("Name");
        category1.setSpanishName("Name");
        category1.setIconUrl("https://png");

        Category category2 = new Category();
        category2.setFoursquareId("93hsd37y645");
        category2.setDefaultName("Name");
        category2.setSpanishName("Name");
        category2.setIconUrl("https://png");
        poi.setCategories(new HashSet<>(Arrays.asList(category2)));

        List<Poi> expectedPois = Arrays.asList(poi);
        categoryJdbcDao.save(Arrays.asList(category1, category2));

        assertThat(category1.getId()).isNotNull();
        assertThat(category2.getId()).isNotNull();

        poiJdbcDao.save(poi);

        assertThat(poi.getId()).isNotNull();

        poiService.deleteCategories();

        poi.setCategories(new HashSet<>());
        
        assertThat(poiJdbcDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedPois).containsOnlyElementsOf(expectedPois);
        assertThat(categoryJdbcDao.findAll()).isNotNull().isEmpty();
    }
}