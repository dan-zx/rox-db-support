package com.grayfox.server.test.dao.foursquare;

import javax.inject.Inject;

import com.grayfox.server.dao.foursquare.CategoryFoursquareDao;
import com.grayfox.server.test.util.HttpStatus;
import com.grayfox.server.test.util.Utils;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.springframework.stereotype.Repository;

@Repository
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