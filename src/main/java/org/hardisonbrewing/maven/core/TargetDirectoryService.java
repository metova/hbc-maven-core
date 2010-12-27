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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

public final class TargetDirectoryService {

    /**
     * The name of the target directory.
     */
    public static final String TARGET_DIRECTORY_NAME = "target";

    private TargetDirectoryService() {

        // do nothing
    }

    public static final void ensureTargetDirectoryExists() {

        File file = getTargetDirectory();
        if ( !file.exists() ) {
            file.mkdirs();
        }
    }

    /**
     * Return a {@link File} that represents the target directory.
     * @return
     */
    public static final File getTargetDirectory() {

        return ProjectService.getOutputDirectory();
    }

    /**
     * Return the path to the target directory.
     * @return
     */
    public static final String getTargetDirectoryPath() {

        return ProjectService.getOutputDirectoryPath();
    }

    public static final String resolveTargetFilePath( String filePath ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( FileUtils.getProjectCanonicalPath( filePath ) );
        return stringBuffer.toString();
    }

    public static final String getTempPackagePath() {

        MavenProject project = ProjectService.getProject();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( project.getBuild().getFinalName() );
        return stringBuffer.toString();
    }

    public static final String[] getSourceFilePaths() {

        String sourceDirectoryPath = ProjectService.getSourceDirectoryPath();
        String targetDirectoryPath = getTargetDirectoryPath();
        String[] filePaths = ProjectService.getSourceFilePaths();
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = filePaths[i].replace( sourceDirectoryPath, targetDirectoryPath );
        }
        return filePaths;
    }

    public static final String[] getResourceFilePaths() {

        String targetDirectoryPath = getTargetDirectoryPath();
        List<String> resourceFilePaths = new LinkedList<String>();
        for (Resource resource : (List<Resource>) ProjectService.getProject().getResources()) {
            File resourceDirectory = new File( resource.getDirectory() );
            String[] filePaths = FileUtils.listFilePathsRecursive( resourceDirectory );
            for (String filePath : filePaths) {
                filePath = filePath.replace( resource.getDirectory(), targetDirectoryPath );
                resourceFilePaths.add( filePath );
            }
        }
        return resourceFilePaths.toArray( new String[resourceFilePaths.size()] );
    }
}
