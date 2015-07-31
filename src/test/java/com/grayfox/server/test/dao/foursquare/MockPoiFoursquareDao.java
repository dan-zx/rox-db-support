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
package com.grayfox.server.test.dao.foursquare;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.grayfox.server.dao.foursquare.PoiFoursquareDao;
import com.grayfox.server.domain.Location;
import com.grayfox.server.domain.Poi;
import com.grayfox.server.test.util.HttpStatus;
import com.grayfox.server.test.util.Utils;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.springframework.stereotype.Repository;

@Repository
public class MockPoiFoursquareDao extends PoiFoursquareDao {

    private static final List<Location> SUPPORTED_LOCATIONS = Arrays.asList(Location.parse("19.043651,-98.197968"), Location.parse("19.054369,-98.283627"));
    private static final List<String> SUPPORTED_VENUES = Arrays.asList("4c09270ea1b32d7f172297f0", "51088c2ce4b0c409707a6897");

    @Inject private MockWebServer mockWebServer;

    @Override
    protected void init() {
        mockWebServer.enqueue(new MockResponse()
            .setStatus(HttpStatus.OK.toString())
            .setBody(Utils.getContentFromFileInClasspath("responses/categories_en.json")));
        mockWebServer.enqueue(new MockResponse()
            .setStatus(HttpStatus.OK.toString())
            .setBody(Utils.getContentFromFileInClasspath("responses/categories_es.json")));
        super.init();
    }

    @Override
    public List<Poi> fetchNearLocations(Location... locations) {
        boolean hasOneLocation = false;
        if (locations.length > 0) {
            for (Location location : locations) {
                if (SUPPORTED_LOCATIONS.contains(location)) {
                    hasOneLocation = true;
                    mockWebServer.enqueue(new MockResponse()
                        .setStatus(HttpStatus.OK.toString())
                        .setBody(Utils.getContentFromFileInClasspath("responses/explore_" + location.stringValue().replace(',', '_') + ".json")));                    
                }
            }
        } 
        if (!hasOneLocation) {
            mockWebServer.enqueue(new MockResponse()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .setBody(Utils.getContentFromFileInClasspath("responses/generic_error.json")));
        }
        return super.fetchNearLocations(locations);
    }

    @Override
    public Poi fetchByFoursquareId(String foursquareId) {
        if (SUPPORTED_VENUES.contains(foursquareId)) {
            mockWebServer.enqueue(new MockResponse()
                .setStatus(HttpStatus.OK.toString())
                .setBody(Utils.getContentFromFileInClasspath("responses/venue_" + foursquareId + ".json")));                    
        } else {
            mockWebServer.enqueue(new MockResponse()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .setBody(Utils.getContentFromFileInClasspath("responses/generic_error.json")));
        }
        return super.fetchByFoursquareId(foursquareId);
    }
}