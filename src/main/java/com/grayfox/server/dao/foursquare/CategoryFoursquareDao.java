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
import java.util.Collection;
import java.util.List;

import com.grayfox.server.dao.CategoryDao;
import com.grayfox.server.domain.Category;

import org.springframework.stereotype.Repository;

@Repository("categoryRemoteDao")
public class CategoryFoursquareDao extends FoursquareDao implements CategoryDao {

    @Override
    public void save(Category entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Collection<Category> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Category> findAll() {
        List<Category> result = new ArrayList<>();
        getCategoryCatalog().forEach((categoryId, category) -> result.add(category));
        return result;
    }

    @Override
    public Category findByFoursquareId(String foursquareId) {
        return getCategoryCatalog().get(foursquareId);
    }

    @Override
    public boolean exists(String foursquareId) {
        return findByFoursquareId(foursquareId) != null;
    }

    @Override
    public void update(Category entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Collection<Category> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Category entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Collection<Category> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}