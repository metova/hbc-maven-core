/**
 * Copyright (c) 2010 Martin M Reed
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

package org.hardisonbrewing.maven.core;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class PluginConfigurationVaildator {

    private final List<String> supported = new LinkedList<String>();
    private final Xpp3Dom xpp3Dom;

    /**
     * 
     * @param key The Plugin.getKey().
     */
    public PluginConfigurationVaildator(String key) {

        xpp3Dom = PluginService.getConfiguration( key );
    }

    public final void support( String config ) {

        if ( supported.contains( config ) ) {
            return;
        }

        supported.add( config );
    }

    public final boolean supported( String config ) {

        return supported.contains( config );
    }

    public final void assertSupported( String config ) {

        if ( !supported( config ) ) {
            throw new IllegalArgumentException( "Configuration element <" + config + "/> is not supported." );
        }
    }

    public final void validate() {

        for (Xpp3Dom child : xpp3Dom.getChildren()) {
            assertSupported( child.getName() );
        }
    }
}
