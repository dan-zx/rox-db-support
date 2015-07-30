package com.grayfox.server.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
        Category c1 = new Category();
        c1.setFoursquareId("4bf58dd8d48988d164941735");
        c1.setDefaultName("Plaza");
        c1.setSpanishName("Plaza");
        c1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/plaza_88.png");

        List<Category> expectedCategories = new ArrayList<>(Arrays.asList(c1));

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isEmpty();

        // save
        categoryJdbcDao.saveOrUpdate(expectedCategories);

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedCategories);

        c1.setDefaultName("OtherName");
        
        Category c2 = new Category();
        c2.setFoursquareId("4deefb944765f83613cdba6e");
        c2.setDefaultName("Historic Site");
        c2.setSpanishName("Lugar histórico");
        c2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/historicsite_88.png");

        expectedCategories.add(c2);

        // update and save
        categoryJdbcDao.saveOrUpdate(expectedCategories);

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedCategories);

        c2.setDefaultName("newName");

        // update
        categoryJdbcDao.update(Arrays.asList(c2));

        assertThat(categoryJdbcDao.fetchAll()).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedCategories);
    }
}