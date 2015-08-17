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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

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
public class PoiJdbcDaoTest {

    @Inject private PoiJdbcDao poiJdbcDao;

    @Before
    public void setUp() {
        assertThat(poiJdbcDao).isNotNull();
    }

    @Test
    @Transactional
    public void testCrud() {
        Poi p1 = new Poi();
        p1.setFoursquareId("4ba952daf964a520d81e3ae3");
        p1.setName("Zócalo");
        p1.setLocation(Location.parse("19.043884224818616,-98.19828987121582"));
        p1.setFoursquareRating(9.2);
        p1.setCategories(new HashSet<>());

        assertThat(poiJdbcDao.fetchAll()).isNotNull().isEmpty();

        List<Poi> expectedPois = new ArrayList<>(Arrays.asList(p1));

        // save
        poiJdbcDao.saveOrUpdate(expectedPois);

        assertThat(p1.getId()).isNotNull();
        assertThat(poiJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedPois);

        p1.setName("Zócalo");
        p1.setFoursquareRating(8.2);

        Poi p2 = new Poi();
        p2.setFoursquareId("4f496cd5e4b07165fa2d73af");
        p2.setName("Centro Histórico");
        p2.setLocation(Location.parse("19.043883250110966,-98.19796749086107"));
        p2.setFoursquareRating(9.1);
        p2.setCategories(new HashSet<>());

        expectedPois.add(p2);

        // update and save
        poiJdbcDao.saveOrUpdate(expectedPois);

        assertThat(p2.getId()).isNotNull();
        assertThat(poiJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedPois);

        p2.setName("Other");
        p2.setFoursquareRating(7.2);

        // update
        poiJdbcDao.update(Arrays.asList(p2));

        assertThat(poiJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedPois);
    }
}