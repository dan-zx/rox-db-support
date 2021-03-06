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
package com.grayfox.server.dao.jdbc;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

abstract class JdbcDao {

    @Inject private DataSource dataSource;
    @Inject private Properties queries;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    protected void init() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected String getQuery(String which) {
        return queries.getProperty(which).trim();
    }
}