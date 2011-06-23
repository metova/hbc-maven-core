/**
 * Copyright (c) 2010-2011 Martin M Reed
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.util.IOUtil;

public class TemplateService {

    protected TemplateService() {

        // do nothing
    }

    public static final Template getTemplate( String resource ) {

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty( "file.resource.loader.class", ClasspathResourceLoader.class.getName() );
        velocityEngine.init();
        return velocityEngine.getTemplate( resource );
    }

    public static final VelocityContext getContext( Properties properties ) {

        VelocityContext velocityContext = new VelocityContext();
        Set<Entry<Object, Object>> entrySet = properties.entrySet();
        for (Entry<Object, Object> entry : entrySet) {
            velocityContext.put( (String) entry.getKey(), (String) entry.getValue() );
        }
        return velocityContext;
    }

    public static final void writeTemplate( File template, Properties properties, File file ) throws IOException {

        VelocityContext velocityContext = getContext( properties );
        writeTemplate( template, velocityContext, file );
    }

    public static final void writeTemplate( File template, VelocityContext velocityContext, File file ) throws IOException {

        Reader reader = new FileReader( template );
        writeTemplate( reader, velocityContext, file );
    }

    public static final void writeTemplate( Template template, Properties properties, File file ) throws IOException {

        VelocityContext velocityContext = getContext( properties );
        writeTemplate( template, velocityContext, file );
    }

    public static final void writeTemplate( Template template, VelocityContext velocityContext, File file ) throws IOException {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter( file );
            template.merge( velocityContext, fileWriter );
        }
        finally {
            IOUtil.close( fileWriter );
        }
    }

    public static final void writeTemplate( Reader reader, VelocityContext velocityContext, File file ) throws IOException {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter( file );
            Velocity.evaluate( velocityContext, fileWriter, "", reader );
        }
        finally {
            IOUtil.close( fileWriter );
        }
    }
}
