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
package org.hardisonbrewing.maven.core.regex;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;

public class RegexFilenameFilter implements FilenameFilter {

    private Pattern pattern;

    public RegexFilenameFilter() {

        // do nothing
    }

    public RegexFilenameFilter(String regex) {

        setRegex( regex );
    }

    public void setRegex( String regex ) {

        pattern = Pattern.compile( regex );
    }

    @Override
    public boolean accept( File dir, String name ) {

        StringBuffer filePath = new StringBuffer();
        filePath.append( dir );
        filePath.append( File.separator );
        filePath.append( name );
        return matches( new File( filePath.toString() ) );
    }

    public boolean matches( File file ) {

        return matches( file.getPath() );
    }

    public boolean matches( String filePath ) {

        if ( pattern == null ) {
            JoJoMojo.getMojo().getLog().error( "Regex has not been set!" );
            throw new IllegalStateException();
        }

        filePath = FileUtils.getProjectCanonicalPath( filePath );
        return pattern.matcher( filePath ).matches();
    }
}
