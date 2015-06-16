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

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.grayfox.server.config.MainConfig;
import com.grayfox.server.domain.Location;
import com.grayfox.server.service.PoiService;

public class Program {

    private static final String USAGE = "Usage: { add [ location1;location2... ] | update [ pois | categories | all ] | quit }";
    private static final String INVALID_OPT = "Invalid option";
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    private static final Scanner CONSOLE = new Scanner(System.in);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class)) {
            PoiService poiService = applicationContext.getBean(PoiService.class);
            programmLoop: while (true) {
                System.out.println(USAGE);
                String[] arguments = CONSOLE.nextLine().split(" ");
                if (arguments.length > 0) {
                    switch (arguments[0]) {
                        case "add":
                            String[] locationStrs = arguments[1].split(";");
                            Location[] locations = new Location[locationStrs.length];
                            for (int i = 0; i < locations.length; i++) locations[i] = Location.parse(locationStrs[i]);
                            poiService.addPois(locations);
                            break;
                        case "update":
                            switch (arguments[1]) {
                                case "pois":
                                    poiService.updatePois();
                                    break;
                                case "categories":
                                    poiService.updateCategories();
                                    break;
                                case "all":
                                    poiService.updateCategories();
                                    poiService.updatePois();
                                    break;
                                default:
                                    System.out.println(INVALID_OPT);
                                    break;
                            }
                            break;
                        case "quit": break programmLoop;
                        default:
                            System.out.println(INVALID_OPT);
                            break;
                    }
                } else System.out.println(INVALID_OPT);
            }
        } catch (Exception ex) {
            LOGGER.error("Error during execution", ex);
        }
        System.out.println("Done");
    }
}