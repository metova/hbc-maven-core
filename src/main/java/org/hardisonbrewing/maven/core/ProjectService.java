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
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

public class ProjectService {

    protected ProjectService() {

        // do nothing
    }

    /**
     * 
     * @return
     */
    public static final String generateSnapshotVersion() {

        String version = getProject().getVersion();
        DateFormat dateFormat = new SimpleDateFormat( "yyyyMMddHHmmssZ" );
        String snapshot = dateFormat.format( new Date() );
        if ( version.contains( "SNAPSHOT" ) ) {
            return version.replace( "SNAPSHOT", snapshot );
        }
        return version + "-" + snapshot;
    }

    /**
     * 
     * @return
     */
    public static final String getProperty( String key ) {

        return getProperties().getProperty( key );
    }

    /**
     * 
     * @return
     */
    public static final Properties getProperties() {

        return getProject().getProperties();
    }

    /**
     * 
     * @return
     */
    public static final String getOutputDirectoryPath() {

        return getProject().getBuild().getOutputDirectory();
    }

    /**
     * 
     * @return
     */
    public static final File getOutputDirectory() {

        return new File( getOutputDirectoryPath() );
    }

    /**
     * 
     * @return
     */
    public static final File getBaseDir() {

        return getProject().getBasedir();
    }

    /**
     * 
     * @return
     */
    public static final String getBaseDirPath() {

        return getBaseDir().getPath();
    }

    /**
     * 
     * @return
     */
    public static final MavenProject getProject() {

        return JoJoMojo.getMojo().getProject();
    }

    /**
     * Create the {@link MavenProject} for the specified {@link Artifact}.
     * @param artifact The {@link Artifact} to create the {@link MavenProject} from.
     * @return
     * @throws ProjectBuildingException
     */
    public static final MavenProject getProject( Artifact artifact ) throws ProjectBuildingException {

        MavenProjectBuilder mavenProjectBuilder = getProjectBuilder();
        return mavenProjectBuilder.buildFromRepository( artifact, DependencyService.getRemoteRepositories(), DependencyService.getLocalRepository() );
    }

    /**
     * 
     * @return
     */
    public static final MavenProjectBuilder getProjectBuilder() {

        return JoJoMojo.getMojo().getProjectBuilder();
    }

    public static final void writeOriginalModel( File dest ) throws IOException {

        FileWriter fileWriter = new FileWriter( dest );

        try {
            getProject().writeOriginalModel( fileWriter );
        }
        finally {
            fileWriter.close();
        }
    }

    public static final String getResolvedVersion() {

        String version = getProject().getVersion();
        if ( version.contains( "-SNAPSHOT" ) ) {
            DateFormat dateFormat = new SimpleDateFormat( "yyyyMMddHHmmss" );
            version = version.replace( "-SNAPSHOT", "." + dateFormat.format( new Date() ) );
        }

        return version;
    }

    public static final File getSourceDirectory() {

        return new File( getSourceDirectoryPath() );
    }

    public static final String getSourceDirectoryPath() {

        return getProject().getBuild().getSourceDirectory();
    }

    public static String[] getSourceFilePaths() {

        return getSourceFilePaths( null, null );
    }

    public static String[] getSourceFilePaths( String[] includes, String[] excludes ) {

        return FileUtils.listFilePathsRecursive( getSourceDirectory(), includes, excludes );
    }

    public static final String[] getResourceFilePaths() {

        List<String> resourceFilePaths = new LinkedList<String>();
        for (Resource resource : (List<Resource>) getProject().getResources()) {
            File resourceDirectory = new File( resource.getDirectory() );
            String[] filePaths = FileUtils.listFilePathsRecursive( resourceDirectory );
            for (String filePath : filePaths) {
                resourceFilePaths.add( filePath );
            }
        }
        return resourceFilePaths.toArray( new String[resourceFilePaths.size()] );
    }
}
