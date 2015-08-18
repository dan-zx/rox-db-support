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

import javax.inject.Inject;

import com.grayfox.server.dao.foursquare.CategoryFoursquareDao;
import com.grayfox.server.test.util.HttpStatus;
import com.grayfox.server.test.util.Utils;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.springframework.stereotype.Repository;

@Repository("categoryFoursquareDao")
public class MockCategoryFoursquareDao extends CategoryFoursquareDao {

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
}