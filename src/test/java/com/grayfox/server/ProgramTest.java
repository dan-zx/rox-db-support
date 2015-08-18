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
package com.grayfox.server;

import static org.assertj.core.api.StrictAssertions.assertThat;

import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

import java.util.Scanner;

import javax.inject.Inject;

import com.grayfox.server.domain.Location;
import com.grayfox.server.service.PoiService;
import com.grayfox.server.test.config.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InOrder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, ProgramTest.MockBeanConfig.class})
@TransactionConfiguration(defaultRollback = true)
public class ProgramTest {

    @Inject private ApplicationContext applicationContext;
    @Inject private PoiService poiService;

    @Before
    public void setUp() {
        assertThat(applicationContext).isNotNull();
        assertThat(poiService).isNotNull();
    }

    @Test
    public void testProgramExecution() {
        String commands = "add 19.054369,-98.283627\n"
                        + "update pois\n"
                        + "update categories\n"
                        + "update all\n"
                        + "delete pois\n"
                        + "delete categories\n"
                        + "delete all\n"
                        + "\n"
                        + "invalid command\n"
                        + "update invalidOption\n"
                        + "update\n"
                        + "quit\n";
        Scanner mockInput = new Scanner(commands);
        new Program().execute(applicationContext, mockInput);

        InOrder inOrder = inOrder(poiService);
        inOrder.verify(poiService).addPois(anyVararg());
        inOrder.verify(poiService).updatePois();
        inOrder.verify(poiService).updateCategories();
        inOrder.verify(poiService).updateAll();
        inOrder.verify(poiService).deletePois();
        inOrder.verify(poiService).deleteCategories();
        inOrder.verify(poiService).deleteAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Configuration
    public static class MockBeanConfig {

        @Bean
        @Primary
        public PoiService mockPoiService() {
            return spy(new PoiService() {

                @Override
                public void addPois(Location... locations) { }

                @Override
                public void updateCategories() { }

                @Override
                public void updatePois() { }

                @Override
                public void updateAll() { }

                @Override
                public void deletePois() { }

                @Override
                public void deleteCategories() { }

                @Override
                public void deleteAll() { }
            });
        }
    }
}