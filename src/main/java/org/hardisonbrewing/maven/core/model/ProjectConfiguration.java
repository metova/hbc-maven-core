/**
 * Copyright (c) 2011 Martin M Reed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hardisonbrewing.maven.core.model;

import java.util.Arrays;

public class ProjectConfiguration {

    private String[] additionalSourceDirectories;

    public void addSourceDirectory( String filePath ) {

        String[] _additionalSourceDirectories;

        if ( additionalSourceDirectories == null ) {
            _additionalSourceDirectories = new String[1];
        }
        else {
            int length = additionalSourceDirectories.length;
            _additionalSourceDirectories = Arrays.copyOf( additionalSourceDirectories, length + 1 );
        }

        _additionalSourceDirectories[_additionalSourceDirectories.length - 1] = filePath;
        additionalSourceDirectories = _additionalSourceDirectories;
    }

    public String[] getAdditionalSourceDirectories() {

        return additionalSourceDirectories;
    }
}
