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

import com.grayfox.server.config.MainConfig;
import com.grayfox.server.domain.Location;
import com.grayfox.server.service.PoiService;
import com.grayfox.server.util.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Program {

    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class)) {
            new Program().execute(applicationContext, new Scanner(System.in));
        } catch (Exception ex) {
            LOGGER.error("Error during execution", ex);
        }
    }

    public void execute(ApplicationContext applicationContext, Scanner input) {
        PoiService poiService = applicationContext.getBean(PoiService.class);
        programmLoop: while (true) {
            System.out.println(Messages.get("program.usage"));
            String[] arguments = input.nextLine().split(" ");
            switch (arguments[0]) {
                case "add":
                    String[] locationStrs = arguments[1].split(";");
                    Location[] locations = new Location[locationStrs.length];
                    for (int i = 0; i < locations.length; i++) locations[i] = Location.parse(locationStrs[i]);
                    poiService.addPois(locations);
                    System.out.println(Messages.get("program.op.ok"));
                    break;
                case "update":
                    if (arguments.length == 2) {
                        switch (arguments[1]) {
                            case "pois":
                                poiService.updatePois();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            case "categories":
                                poiService.updateCategories();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            case "all":
                                poiService.updateAll();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            default:
                                System.out.println(Messages.get("program.invalid.option"));
                                break;
                        }
                    } else System.out.println(Messages.get("program.invalid.option"));
                    break;
                case "delete":
                    if (arguments.length == 2) {
                        switch (arguments[1]) {
                            case "pois":
                                poiService.deletePois();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            case "categories":
                                poiService.deleteCategories();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            case "all":
                                poiService.deleteAll();
                                System.out.println(Messages.get("program.op.ok"));
                                break;
                            default:
                                System.out.println(Messages.get("program.invalid.option"));
                                break;
                        }
                    } else System.out.println(Messages.get("program.invalid.option"));
                    break;
                case "quit":
                    System.out.println(Messages.get("program.finish"));
                    break programmLoop;
                default:
                    System.out.println(Messages.get("program.invalid.option"));
                    break;
            }
        }
    }
}