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

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryLocalDbDaoTest {

    @Inject @Named("categoryLocalDbDao") private CategoryDao categoryLocalDbDao;

    @Before
    public void setUp() {
        assertThat(categoryLocalDbDao).isNotNull();
    }

    @Test
    @Transactional
    public void testCrud() {
        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d164941735");
        category.setDefaultName("Plaza");
        category.setSpanishName("Plaza");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/plaza_88.png");

        assertThat(categoryLocalDbDao.findAll()).isNotNull().isEmpty();

        List<Category> expectedCategories = Arrays.asList(category);

        // create
        categoryLocalDbDao.save(category);

        assertThat(category.getId()).isNotNull();

        // read
        assertThat(categoryLocalDbDao.exists(category.getFoursquareId())).isTrue();
        assertThat(categoryLocalDbDao.findByFoursquareId(category.getFoursquareId())).isNotNull().isEqualTo(category);
        assertThat(categoryLocalDbDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedCategories).containsOnlyElementsOf(expectedCategories);

        category.setDefaultName("OtherName");

        // update
        categoryLocalDbDao.update(category);

        assertThat(categoryLocalDbDao.findByFoursquareId(category.getFoursquareId())).isNotNull().isEqualTo(category);

        // delete
        categoryLocalDbDao.delete(category);

        assertThat(categoryLocalDbDao.findByFoursquareId(category.getFoursquareId())).isNull();
    }
}