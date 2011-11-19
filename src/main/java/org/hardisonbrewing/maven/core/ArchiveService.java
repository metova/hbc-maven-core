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
import java.io.IOException;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

/**
 * Utility methods for handling archiving and unarchiving.
 */
public class ArchiveService {

    protected ArchiveService() {

        // do nothing
    }

    /**
     * Unarchive a {@link File} to the specified destination directory.
     * @param src The source {@link File}.
     * @param dest The destination directory.
     * @throws ArchiverException
     */
    public static final void unarchive( File src, File dest ) throws ArchiverException {

        unarchive( src, dest, getDefaultArchiverManager() );
    }

    /**
     * Extract a {@link File} to the specified destination directory.
     * @param src The source {@link File}.
     * @param dest The destination directory.
     * @param archiverManager
     * @throws ArchiverException
     */
    public static final void unarchive( File src, File dest, ArchiverManager archiverManager ) throws ArchiverException {

        try {
            unarchive( src, dest, getUnArchiver( src, archiverManager ) );
        }
        catch (NoSuchArchiverException e) {
            throw new ArchiverException( e.getMessage(), e );
        }
    }

    public static final void archive( File src, File dest ) throws ArchiverException {

        archive( src, dest, getDefaultArchiverManager() );
    }

    public static final void archive( File src, File dest, ArchiverManager archiverManager ) throws ArchiverException {

        try {
            archive( src, dest, getArchiver( dest, archiverManager ) );
        }
        catch (NoSuchArchiverException e) {
            throw new ArchiverException( e.getMessage(), e );
        }
    }

    public static final void archive( File src, File dest, Archiver archiver ) throws ArchiverException {

        archiver.addDirectory( src );
        archiver.setDestFile( dest );

        try {
            archiver.createArchive();
        }
        catch (IOException e) {
            throw new ArchiverException( e.getMessage(), e );
        }
    }

    /**
     * Extract a {@link File} to the specified destination directory.
     * @param src The source {@link File}.
     * @param dest The destination directory.
     * @param unArchiver
     * @throws ArchiverException
     */
    public static final void unarchive( File src, File dest, UnArchiver unArchiver ) throws ArchiverException {

        dest.mkdirs();

        unArchiver.setSourceFile( src );
        unArchiver.setDestDirectory( dest );
        unArchiver.setOverwrite( true );
        unArchiver.extract();
    }

    /**
     * Return the default {@link ArchiverManager} for the current {@link JoJoMojo}.
     * @return
     */
    public static final ArchiverManager getDefaultArchiverManager() {

        return JoJoMojo.getMojo().getArchiverManager();
    }

    /**
     * Return an {@link Archiver} for the specified destination directory.
     * @param dest The destination directory.
     * @param archiverManager
     * @return
     * @throws NoSuchArchiverException
     */
    public static final Archiver getArchiver( File dest, ArchiverManager archiverManager ) throws NoSuchArchiverException {

        return archiverManager.getArchiver( FileUtils.getExtension( dest ) );
    }

    /**
     * Return an {@link Archiver} for the given source directory.
     * @param src The source directory.
     * @param archiverManager
     * @return
     * @throws NoSuchArchiverException
     */
    public static final UnArchiver getUnArchiver( File src, ArchiverManager archiverManager ) throws NoSuchArchiverException {

        return archiverManager.getUnArchiver( FileUtils.getExtension( src ) );
    }
}
