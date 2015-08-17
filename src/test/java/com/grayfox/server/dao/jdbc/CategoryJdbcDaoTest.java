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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.grayfox.server.domain.Category;
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
public class CategoryJdbcDaoTest {

    @Inject private CategoryJdbcDao categoryJdbcDao;

    @Before
    public void setUp() {
        assertThat(categoryJdbcDao).isNotNull();
    }

    @Test
    @Transactional
    public void testCrud() {
        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d164941735");
        category.setDefaultName("Plaza");
        category.setSpanishName("Plaza");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/plaza_88.png");

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isEmpty();

        List<Category> expectedCategories = Arrays.asList(category);

        // save
        categoryJdbcDao.save(category);

        assertThat(category.getId()).isNotNull();
        assertThat(categoryJdbcDao.exists(category.getFoursquareId())).isTrue();
        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedCategories).containsOnlyElementsOf(expectedCategories);

        category.setDefaultName("OtherName");

        // update
        categoryJdbcDao.update(category);

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedCategories).containsOnlyElementsOf(expectedCategories);
    }
}