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
package com.grayfox.server.domain;

import com.grayfox.server.exception.BaseApplicationException;

public class DomainException extends BaseApplicationException {

    private static final long serialVersionUID = 8752604076326015752L;

    private DomainException(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseBuilder<DomainException> {

        @Override
        public DomainException build() {
            return new DomainException(this);
        }
    }
}