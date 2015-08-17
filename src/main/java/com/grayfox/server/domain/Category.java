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

public class Category extends Entity<Long> {

    private static final long serialVersionUID = -8204143874909029069L;

    private String defaultName;
    private String spanishName;
    private String iconUrl;
    private String foursquareId;

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public void setSpanishName(String spanishName) {
        this.spanishName = spanishName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getFoursquareId() {
        return foursquareId;
    }

    public void setFoursquareId(String foursquareId) {
        this.foursquareId = foursquareId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultName == null) ? 0 : defaultName.hashCode());
        result = prime * result + ((foursquareId == null) ? 0 : foursquareId.hashCode());
        result = prime * result + ((iconUrl == null) ? 0 : iconUrl.hashCode());
        result = prime * result + ((spanishName == null) ? 0 : spanishName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Category other = (Category) obj;
        if (defaultName == null) {
            if (other.defaultName != null) return false;
        } else if (!defaultName.equals(other.defaultName)) return false;
        if (foursquareId == null) {
            if (other.foursquareId != null) return false;
        } else if (!foursquareId.equals(other.foursquareId)) return false;
        if (iconUrl == null) {
            if (other.iconUrl != null) return false;
        } else if (!iconUrl.equals(other.iconUrl)) return false;
        if (spanishName == null) {
            if (other.spanishName != null) return false;
        } else if (!spanishName.equals(other.spanishName)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [id=" + getId() + ", defaultName=" + defaultName + ", spanishName=" + spanishName + ", iconUrl=" + iconUrl + ", foursquareId=" + foursquareId + "]";
    }
}