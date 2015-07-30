package com.grayfox.server.dao.foursquare;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;
import com.grayfox.server.test.config.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PoiFoursquareDaoTest {

    @Inject private PoiFoursquareDao poiFoursquareDao;

    @Before
    public void setUp() {
        assertThat(poiFoursquareDao).isNotNull();
    }

    @Test
    public void testFetchNearLocations() {
        Poi p1 = new Poi();
        p1.setFoursquareId("4ba952daf964a520d81e3ae3");
        p1.setName("Zócalo");
        p1.setLocation(Location.parse("19.043884224818616,-98.19828987121582"));
        p1.setFoursquareRating(9.2);

        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d164941735");
        category.setDefaultName("Plaza");
        category.setSpanishName("Plaza");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/plaza_88.png");
        p1.setCategories(new HashSet<>(Arrays.asList(category)));

        Poi p2 = new Poi();
        p2.setFoursquareId("4f496cd5e4b07165fa2d73af");
        p2.setName("Centro Histórico");
        p2.setLocation(Location.parse("19.043883250110966,-98.19796749086107"));
        p2.setFoursquareRating(9.1);

        category = new Category();
        category.setFoursquareId("4deefb944765f83613cdba6e");
        category.setDefaultName("Historic Site");
        category.setSpanishName("Lugar histórico");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/historicsite_88.png");
        p2.setCategories(new HashSet<>(Arrays.asList(category)));

        Poi p3 = new Poi();
        p3.setFoursquareId("4c09270ea1b32d7f172297f0");
        p3.setName("Las Quekas");
        p3.setLocation(Location.parse("19.050907767250518,-98.28156293330026"));
        p3.setFoursquareRating(8.5);

        category = new Category();
        category.setFoursquareId("4bf58dd8d48988d143941735");
        category.setDefaultName("Breakfast Spot");
        category.setSpanishName("Café");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/breakfast_88.png");
        p3.setCategories(new HashSet<>(Arrays.asList(category)));

        Poi p4 = new Poi();
        p4.setFoursquareId("51088c2ce4b0c409707a6897");
        p4.setName("Los Choripanes");
        p4.setLocation(Location.parse("19.049072265625,-98.28199768066406"));
        p4.setFoursquareRating(8.6);

        category = new Category();
        category.setFoursquareId("4bf58dd8d48988d107941735");
        category.setDefaultName("Argentinian Restaurant");
        category.setSpanishName("Restaurante argentino");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");
        p4.setCategories(new HashSet<>(Arrays.asList(category)));

        List<Poi> expectedPois = Arrays.asList(p1, p2, p3, p4);

        List<Poi> actualPois = poiFoursquareDao.fetchNearLocations(Location.parse("19.043651,-98.197968"), Location.parse("19.054369,-98.283627"));

        assertThat(actualPois).isNotNull().isNotEmpty().containsOnlyElementsOf(expectedPois);
    }

    @Test
    public void testErrorInFetchNearLocations() {
        assertThatThrownBy(() -> poiFoursquareDao.fetchNearLocations(Location.parse("0,0")))
            .isInstanceOf(DaoException.class)
            .hasMessageStartingWith("Internal error while requesting Foursquare data. Message:");
    }

    @Test
    public void testFetchByFoursquareId() {
        Poi expectedPoi = new Poi();
        expectedPoi.setFoursquareId("4c93d5ee72dd224b18539491");
        expectedPoi.setName("Chimichurri");
        expectedPoi.setLocation(Location.parse("19.052182387137865,-98.21962375203553"));
        expectedPoi.setFoursquareRating(8.1);

        Category category = new Category();
        category.setFoursquareId("4bf58dd8d48988d107941735");
        category.setDefaultName("Argentinian Restaurant");
        category.setSpanishName("Restaurante argentino");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/argentinian_88.png");
        expectedPoi.setCategories(new HashSet<>(Arrays.asList(category)));

        Poi actualPoi = poiFoursquareDao.fetchByFoursquareId(expectedPoi.getFoursquareId());

        assertThat(actualPoi).isNotNull().isEqualTo(expectedPoi);
    }

    @Test
    public void testErrorInFetchByFoursquareId() {
        assertThatThrownBy(() -> poiFoursquareDao.fetchByFoursquareId("unknown"))
            .isInstanceOf(DaoException.class)
            .hasMessageStartingWith("Internal error while requesting Foursquare data. Message:");
    }
}