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
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;
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
public class PoiLocalDaoTest {

    @Inject @Named("poiLocalDao") private PoiDao poiLocalDao;

    @Before
    public void setUp() {
        assertThat(poiLocalDao).isNotNull();
    }

    @Test
    @Transactional
    public void testCrud() {
        Poi poi = new Poi();
        poi.setFoursquareId("4ba952daf964a520d81e3ae3");
        poi.setName("Zócalo");
        poi.setLocation(Location.parse("19.043884224818616,-98.19828987121582"));
        poi.setFoursquareRating(9.2);
        poi.setCategories(new HashSet<>());

        List<Poi> expectedPois = Arrays.asList(poi);

        // create
        poiLocalDao.save(poi);

        assertThat(poi.getId()).isNotNull();

        // read
        assertThat(poiLocalDao.exists(poi.getFoursquareId())).isTrue();
        assertThat(poiLocalDao.findByFoursquareId(poi.getFoursquareId())).isNotNull().isEqualTo(poi);
        assertThat(poiLocalDao.findAll()).isNotNull().isNotEmpty().doesNotContainNull().hasSameSizeAs(expectedPois).containsOnlyElementsOf(expectedPois);

        poi.setName("OtherName");

        // update
        poiLocalDao.update(poi);

        assertThat(poiLocalDao.findByFoursquareId(poi.getFoursquareId())).isNotNull().isEqualTo(poi);

        // delete
        poiLocalDao.delete(poi);

        assertThat(poiLocalDao.findByFoursquareId(poi.getFoursquareId())).isNull();
    }
}