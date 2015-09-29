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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.foursquare4j.FoursquareApi;
import com.foursquare4j.response.Result;

import com.grayfox.server.dao.DaoException;
import com.grayfox.server.domain.Category;

abstract class FoursquareDao {

    private Map<String, Category> categoryCatalog;

    @Inject private FoursquareApi foursquareApi;

    @PostConstruct
    protected void init() {
        categoryCatalog = allCategories();
    }

    protected FoursquareApi getFoursquareApi() {
        return foursquareApi;
    }

    protected Map<String, Category> getCategoryCatalog() {
        return categoryCatalog;
    }

    private Map<String, Category> allCategories() {
        FoursquareApi foursquareApi = getFoursquareApi();
        Map<String, Category> myCategories = null;
        Map<String, Category> myCategoriesInSpanish = null;
        foursquareApi.setLocale(new Locale("en"));
        Result<com.foursquare4j.response.Category[]> foursquareCategories = foursquareApi.getVenueCategories();
        foursquareApi.setLocale(new Locale("es"));
        Result<com.foursquare4j.response.Category[]> foursquareCategoriesInSpanish = foursquareApi.getVenueCategories();
        foursquareApi.setLocale(null);
        if (foursquareCategories.getMeta().getCode() == 200) myCategories = toCategories(foursquareCategories.getResponse());
        else throw new DaoException.Builder()
                .messageKey("foursquare.request.error")
                .addMessageArgument(foursquareCategories.getMeta().getErrorDetail())
                .build();
        if (foursquareCategoriesInSpanish.getMeta().getCode() == 200) myCategoriesInSpanish = toCategories(foursquareCategoriesInSpanish.getResponse());
        else throw new DaoException.Builder()
                .messageKey("foursquare.request.error")
                .addMessageArgument(foursquareCategoriesInSpanish.getMeta().getErrorDetail())
                .build();

        for (Map.Entry<String, Category> entry : myCategories.entrySet()) {
            entry.getValue().setSpanishName(myCategoriesInSpanish.get(entry.getKey()).getDefaultName());
        }

        return myCategories;
    }

    private Map<String, Category> toCategories(com.foursquare4j.response.Category[] categories) {
        Map<String, Category> myCategories = new HashMap<>();
        for (com.foursquare4j.response.Category foursquareCategory : categories) {
            Category category = new Category();
            category.setDefaultName(foursquareCategory.getName());
            category.setIconUrl(new StringBuilder().append(foursquareCategory.getIcon().getPrefix()).append("88").append(foursquareCategory.getIcon().getSuffix()).toString());
            category.setFoursquareId(foursquareCategory.getId());
            myCategories.put(foursquareCategory.getId(), category);
            for (com.foursquare4j.response.Category foursquareSubCategory : foursquareCategory.getCategories()) {
                Category subCategory = new Category();
                subCategory.setDefaultName(foursquareSubCategory.getName());
                subCategory.setIconUrl(new StringBuilder().append(foursquareCategory.getIcon().getPrefix()).append("88").append(foursquareCategory.getIcon().getSuffix()).toString());
                subCategory.setFoursquareId(foursquareSubCategory.getId());
                myCategories.put(foursquareSubCategory.getId(), subCategory);
                for (com.foursquare4j.response.Category foursquareSubSubCategory : foursquareSubCategory.getCategories()) {
                    Category subSubCategory = new Category();
                    subSubCategory.setDefaultName(foursquareSubSubCategory.getName());
                    subSubCategory.setIconUrl(new StringBuilder().append(foursquareCategory.getIcon().getPrefix()).append("88").append(foursquareCategory.getIcon().getSuffix()).toString());
                    subSubCategory.setFoursquareId(foursquareSubSubCategory.getId());
                    myCategories.put(foursquareSubSubCategory.getId(), subSubCategory);
                }
            }
        }
        return myCategories;
    }
}