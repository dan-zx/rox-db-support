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
package com.grayfox.server.dao.foursquare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.foursquare4j.FoursquareApi;
import com.foursquare4j.response.ExploreVenueGroups;
import com.foursquare4j.response.ExploreVenueGroups.VenueRecommendation;
import com.foursquare4j.response.Group;
import com.foursquare4j.response.Result;
import com.foursquare4j.response.Venue;

import com.grayfox.server.dao.DaoException;
import com.grayfox.server.dao.PoiDao;
import com.grayfox.server.domain.Category;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;

import org.springframework.stereotype.Repository;

@Repository("poiRemoteDao")
public class PoiFoursquareDao extends FoursquareDao implements PoiDao {

    @Override
    public void save(Poi entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Collection<Poi> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Poi> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Poi> findNearLocations(Location... locations) {
        FoursquareApi foursquareApi = getFoursquareApi();
        List<Poi> pois = new ArrayList<>(locations.length);
        for (Location location : locations) {
            Result<ExploreVenueGroups> exploreResult = foursquareApi.exploreVenues(location.stringValues(), null, null, null, null, null, null, null, 50, null, null, null, null, null, null, null, null, null, null, null, null);
            if (exploreResult.getMeta().getCode() == 200) {
                for (Group<VenueRecommendation> group : exploreResult.getResponse().getGroups()) {
                    Arrays.stream(group.getItems()).forEach(venueRecommendation -> pois.add(toPoi(venueRecommendation.getVenue())));
                }
            } else throw new DaoException.Builder().messageKey("foursquare.request.error").addMessageArgument(exploreResult.getMeta().getErrorDetail()).build();
        }
        return pois;
    }

    @Override
    public Poi findByFoursquareId(String foursquareId) {
        Result<Venue> venueResult = getFoursquareApi().getVenue(foursquareId);
        if (venueResult.getMeta().getCode() == 200) return toPoi(venueResult.getResponse());
        else throw new DaoException.Builder().messageKey("foursquare.request.error").addMessageArgument(venueResult.getMeta().getErrorDetail()).build();
    }

    private Poi toPoi(Venue venue) {
        Poi poi = new Poi();
        poi.setName(venue.getName());
        poi.setLocation(new Location());
        poi.getLocation().setLatitude(venue.getLocation().getLat());
        poi.getLocation().setLongitude(venue.getLocation().getLng());
        poi.setFoursquareId(venue.getId());
        poi.setFoursquareRating(venue.getRating());
        Set<Category> myCategories = new HashSet<>();
        for (com.foursquare4j.response.Category category : venue.getCategories()) {
            Category myCategory = getCategoryCatalog().get(category.getId());
            myCategory.setId(null);
            myCategories.add(myCategory);
        }
        poi.setCategories(myCategories);
        return poi;
    }

    @Override
    public boolean exists(String foursquareId) {
        return findByFoursquareId(foursquareId) != null;
    }

    @Override
    public void update(Poi entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Collection<Poi> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Poi entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Collection<Poi> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}