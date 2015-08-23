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
package com.grayfox.server.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.grayfox.server.domain.Category;
import com.grayfox.server.test.config.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryRemoteDbDaoTest {

    @Inject @Named("categoryRemoteDbDao") private CategoryDao categoryRemoteDbDao;

    @Before
    public void setUp() {
        assertThat(categoryRemoteDbDao).isNotNull();
    }

    @Test
    public void testFindAll() {
        Category c1 = new Category();
        c1.setFoursquareId("4bf58dd8d48988d164941735");
        c1.setDefaultName("Plaza");
        c1.setSpanishName("Plaza");
        c1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/plaza_88.png");

        Category c2 = new Category();
        c2.setFoursquareId("4deefb944765f83613cdba6e");
        c2.setDefaultName("Historic Site");
        c2.setSpanishName("Lugar histórico");
        c2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/historicsite_88.png");

        Category c3 = new Category();
        c3.setFoursquareId("4bf58dd8d48988d143941735");
        c3.setDefaultName("Breakfast Spot");
        c3.setSpanishName("Café");
        c3.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");

        Category c4 = new Category();
        c4.setFoursquareId("4bf58dd8d48988d107941735");
        c4.setDefaultName("Argentinian Restaurant");
        c4.setSpanishName("Restaurante argentino");
        c4.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");

        List<Category> expectedCategories = Arrays.asList(c1, c2, c3, c4);
        List<Category> actualCategories = categoryRemoteDbDao.findAll();

        assertThat(actualCategories).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedCategories);
    }

    @Test
    public void testFindByFoursquareId() {
        Category expectedCategory = new Category();
        expectedCategory.setFoursquareId("4bf58dd8d48988d143941735");
        expectedCategory.setDefaultName("Breakfast Spot");
        expectedCategory.setSpanishName("Café");
        expectedCategory.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");

        Category actualCategory = categoryRemoteDbDao.findByFoursquareId(expectedCategory.getFoursquareId());

        assertThat(actualCategory).isNotNull().isEqualTo(expectedCategory);
    }
}