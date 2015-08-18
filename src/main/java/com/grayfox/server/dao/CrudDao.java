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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CrudDao<T extends Serializable> {

    void save(T entity);
    void save(Collection<T> entities);
    List<T> findAll();
    void update(T entity);
    void update(Collection<T> entities);
    void delete(T entity);
    void delete(Collection<T> entities);
    void deleteAll();
}