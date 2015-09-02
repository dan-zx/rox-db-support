RoX Database Support
====================

Console application for RoX database maintenance.

Building the Project
--------------------

Clone this repository and build the project using the Gradle wrapper.

```sh
$ git clone https://github.com/dan-zx/rox-db-support.git
$ cd rox-db-support
$ ./gradlew assemble
```

Usage
-----

```sh
$ java -jar rox-db-support.jar
$ Usage: { add [ location1;location2... ] | update [ pois | categories | all ] | delete [ pois | categories | all ] | quit }
```

License
-------

    Copyright 2014-2015 Daniel Pedraza-Arcega

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
