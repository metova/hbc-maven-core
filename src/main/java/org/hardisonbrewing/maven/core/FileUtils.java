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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.DirectoryScanner;

public class FileUtils extends org.codehaus.plexus.util.FileUtils {

    public static final String CURRENT_DIRECTORY_MARKER = "." + File.separator;
    public static final String PARENT_DIRECTORY_MARKER = ".." + File.separator;

    public static final String[] IGNORE_FILES;

    static {

        List<String> ignoreFiles = new ArrayList<String>();
        ignoreFiles.add( ".svn" );
        ignoreFiles.add( ".DS_Store" );
        IGNORE_FILES = new String[ignoreFiles.size()];
        ignoreFiles.toArray( IGNORE_FILES );
    }

    protected FileUtils() {

        // do nothing
    }

    public static final void ensureParentExists( String filePath ) {

        File file = new File( filePath );
        file = file.getParentFile();

        if ( file != null && !file.exists() ) {
            file.mkdirs();
        }
    }

    public static final void copyFile( String srcPath, String destPath ) throws IOException {

        File srcFile = new File( srcPath );
        File destFile = new File( destPath );
        copyFile( srcFile, destFile );
    }

    public static final void copyFile( File source, File destination ) throws IOException {

        if ( !source.exists() ) {
            throw new IllegalStateException( source.getAbsolutePath() + " does not exist." );
        }

        JoJoMojo.getMojo().getLog().info( "Copying " + source + " to " + destination );
        org.codehaus.plexus.util.FileUtils.copyFile( source, destination );

        destination.setLastModified( destination.lastModified() );
    }

    public static final void copyFileToDirectory( String source, String destinationDirectory ) throws IOException {

        JoJoMojo.getMojo().getLog().info( "Copying " + source + " to " + destinationDirectory );
        org.codehaus.plexus.util.FileUtils.copyFileToDirectory( source, destinationDirectory );
    }

    public static final void copyFileToDirectory( File source, File destinationDirectory ) throws IOException {

        if ( !source.exists() ) {
            throw new IllegalStateException( source.getAbsolutePath() + " does not exist." );
        }

        JoJoMojo.getMojo().getLog().info( "Copying " + source + " to " + destinationDirectory );

        StringBuffer destFilePath = new StringBuffer();
        destFilePath.append( destinationDirectory );
        destFilePath.append( File.separator );
        destFilePath.append( source.getName() );
        File destFile = new File( destFilePath.toString() );

        if ( !source.isDirectory() ) {
            org.codehaus.plexus.util.FileUtils.copyFileToDirectory( source, destinationDirectory );
        }
        else {
            destFile.mkdir();
            org.codehaus.plexus.util.FileUtils.copyDirectoryStructure( source, destFile );
        }

        destFile.setLastModified( source.lastModified() );
    }

    /**
     * Return the extenstion type for the specified {@link File}.
     * @param file The {@link File} to return the extenstion type of.
     * @return
     * @throws NoSuchArchiverException
     */
    public static final String getExtension( File file ) throws NoSuchArchiverException {

        return getExtension( file.getName() );
    }

    public static final boolean isCanonical( String filePath ) {

        if ( filePath == null || filePath.length() == 0 ) {
            return false;
        }
        return filePath.charAt( 0 ) != File.separatorChar;
    }

    /**
     * Check if the specified file name exists.
     * @param fileName
     * @return
     */
    public static final boolean exists( String fileName ) {

        if ( fileName.startsWith( PARENT_DIRECTORY_MARKER ) ) {
            throw new IllegalStateException( "Accessing files outside the project domain is not allowed." );
        }

        if ( fileName.startsWith( CURRENT_DIRECTORY_MARKER ) ) {
            fileName = fileName.substring( CURRENT_DIRECTORY_MARKER.length() );
        }

        StringBuffer filePath = new StringBuffer();

        if ( isCanonical( fileName ) ) {
            filePath.append( ProjectService.getBaseDir() );
        }

        filePath.append( File.separator );
        filePath.append( fileName );

        File dependencyFile = new File( filePath.toString() );
        return dependencyFile.exists();
    }

    /**
     * Convert the specified file path to a canonical path that can be used with
     * {@link ProjectService.getBaseDirPath()} as the new base path.
     * @param filePath
     * @return
     */
    public static final String getProjectCanonicalPath( String filePath ) {

        String baseDirPath = ProjectService.getBaseDirPath();

        if ( filePath.startsWith( baseDirPath ) ) {
            filePath = filePath.substring( baseDirPath.length() );
        }

        if ( filePath.startsWith( File.separator ) ) {
            filePath = filePath.substring( 1 );
        }

        return filePath;
    }

    /**
     * Convert the specified file path to a canonical path that can be used with
     * {@link TargetDirectoryService.getTargetDirectoryPath()} as the new base path.
     * @param filePath
     * @return
     */
    public static final String getTargetCanonicalPath( String filePath ) {

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        if ( filePath.startsWith( targetDirectoryPath ) ) {
            return filePath.substring( targetDirectoryPath.length() );
        }

        return filePath;
    }

    /**
     * If a file path does not exist, check the target directory and return the resolved path.
     * @param filePaths
     * @throws IllegalArgumentException If the file cannot be found.
     */
    public static final void resolveFilePaths( String[] filePaths ) {

        if ( filePaths == null ) {
            return;
        }

        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = resolveFilePath( filePaths[i] );
        }
    }

    public static final void resolveFilePaths( List<String> filePaths ) {

        if ( filePaths == null ) {
            return;
        }

        Iterator<String> iterator = filePaths.iterator();

        for (int i = 0; iterator.hasNext(); i++) {
            filePaths.set( i, resolveFilePath( iterator.next() ) );
        }
    }

    /**
     * If a file path does not exist, check the target directory and return the resolved path.
     * @param filePath
     * @return
     * @throws IllegalArgumentException If the file cannot be found.
     */
    public static final String resolveFilePath( String filePath ) {

        if ( filePath == null ) {
            return filePath;
        }

        filePath = resolveProjectFilePath( filePath );

        if ( exists( filePath ) ) {
            return filePath;
        }

        String resolvedTargetPath = TargetDirectoryService.resolveTargetFilePath( filePath );

        if ( !exists( resolvedTargetPath ) ) {
            throw new IllegalArgumentException( "File[" + filePath + "] cannot be found." );
        }

        return resolvedTargetPath;
    }

    public static final String resolveProjectFilePath( String filePath ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( FileUtils.getProjectCanonicalPath( filePath ) );
        return stringBuffer.toString();
    }

    public static final long lastModified( File file, boolean recursive ) {

        if ( !file.isDirectory() ) {
            return file.lastModified();
        }
        return lastModifiedRecursive0( file, 0 );
    }

    private static final long lastModifiedRecursive0( File file, long lastModified ) {

        if ( !file.isDirectory() ) {
            return Math.max( lastModified, file.lastModified() );
        }

        for (File _file : file.listFiles()) {
            lastModified = lastModifiedRecursive0( _file, lastModified );
        }

        return lastModified;
    }

    public static final String trimSeperators( String filePath ) {

        boolean startsWith = filePath.startsWith( File.separator );
        boolean endsWith = filePath.endsWith( File.separator );

        if ( !startsWith && !endsWith ) {
            return filePath;
        }

        int beginIndex = 0;
        if ( startsWith ) {
            beginIndex++;
        }

        int endIndex = filePath.length();
        if ( endsWith ) {
            endIndex--;
        }

        if ( beginIndex >= endIndex ) {
            return null;
        }
        return filePath.substring( beginIndex, endIndex );
    }

    public static final String trimEndSeperator( String filePath ) {

        if ( !filePath.endsWith( File.separator ) ) {
            return filePath;
        }

        int endIndex = filePath.length() - 1;
        if ( endIndex == 0 ) {
            return null;
        }
        return filePath.substring( 0, endIndex );
    }

    public static final String normalize( String filePath ) {

        filePath = filePath.replace( '/', File.separatorChar );
        return filePath.replace( '\\', File.separatorChar );
    }

    public static String[] convertToPaths( File[] files ) {

        String[] filePaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getPath();
        }
        return filePaths;
    }

    public static File[] convertToFiles( String[] filePaths ) {

        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            files[i] = new File( filePaths[i] );
        }
        return files;
    }

    public static File[] listFilesRecursive( File file, String[] includes, String[] excludes ) {

        return convertToFiles( listFilePathsRecursive( file, includes, excludes ) );
    }

    public static final File[] listFilesRecursive( File file ) {

        return convertToFiles( listFilePathsRecursive( file ) );
    }

    public static String[] listFilePathsRecursive( File file, Collection<String> includes, Collection<String> excludes ) {

        String[] _includes = new String[includes.size()];
        includes.toArray( _includes );

        String[] _excludes = new String[excludes.size()];
        excludes.toArray( _excludes );

        return listFilePathsRecursive( file, _includes, _excludes );
    }

    public static String[] listFilePathsRecursive( File file, String[] includes, String[] excludes ) {

        DirectoryScanner scanner = buildScanner( file, includes, excludes );
        scanner.scan();

        String[] filePaths = scanner.getIncludedFiles();
        for (int i = 0; i < filePaths.length; i++) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( file );
            stringBuffer.append( File.separator );
            stringBuffer.append( filePaths[i] );
            filePaths[i] = stringBuffer.toString();
        }

        return filePaths;
    }

    public static File[] listDirectoriesRecursive( File file ) {

        return convertToFiles( listDirectoryPathsRecursive( file, null, null ) );
    }

    public static File[] listDirectoriesRecursive( File file, String[] includes, String[] excludes ) {

        return convertToFiles( listDirectoryPathsRecursive( file, includes, excludes ) );
    }

    public static String[] listDirectoryPathsRecursive( File file ) {

        return listDirectoryPathsRecursive( file, null, null );
    }

    public static String[] listDirectoryPathsRecursive( File file, String[] includes, String[] excludes ) {

        DirectoryScanner scanner = buildScanner( file, includes, excludes );
        scanner.scan();

        List<String> filePaths = new ArrayList<String>();

        for (String filePath : scanner.getIncludedDirectories()) {

            // may be empty string if returning self
            if ( filePath.length() == 0 ) {
                continue;
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( file );
            stringBuffer.append( File.separator );
            stringBuffer.append( filePath );
            filePaths.add( stringBuffer.toString() );
        }

        String[] _filePaths = new String[filePaths.size()];
        filePaths.toArray( _filePaths );
        return _filePaths;
    }

    public static DirectoryScanner buildScanner( File file, String[] includes, String[] excludes ) {

        List<String> _excludes = new ArrayList<String>();

        for (String ignoreFile : IGNORE_FILES) {
            _excludes.add( "**" + File.separator + ignoreFile );
            _excludes.add( "**" + File.separator + ignoreFile + File.separator + "**" );
        }

        if ( excludes != null ) {
            for (String exclude : excludes) {
                _excludes.add( exclude );
            }
        }

        excludes = new String[_excludes.size()];
        _excludes.toArray( excludes );

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( includes );
        scanner.setExcludes( excludes );
        scanner.setBasedir( file );
        scanner.setCaseSensitive( false );
        scanner.setFollowSymlinks( true );
        return scanner;
    }

    public static final String[] listFilePathsRecursive( File file ) {

        List<String> files = new LinkedList<String>();
        listFilePathsRecursive( file, files );
        return files.toArray( new String[files.size()] );
    }

    private static final void listFilePathsRecursive( File file, List<String> files ) {

        String filePath = file.getPath();
        for (String ignoreFile : IGNORE_FILES) {
            if ( ignoreFile.equalsIgnoreCase( filePath ) ) {
                return;
            }
        }
        if ( !file.exists() ) {
            return;
        }
        if ( !file.isDirectory() ) {
            files.add( filePath );
            return;
        }
        for (File _file : file.listFiles()) {
            listFilePathsRecursive( _file, files );
        }
    }

    public static final String replaceExtension( String filePath, String extension ) {

        return filePath.substring( 0, filePath.lastIndexOf( '.' ) ) + "." + extension;
    }

    public static final String getCanonicalPath( String filePath, String prefix ) {

        if ( prefix == null || filePath.equals( prefix ) || !filePath.startsWith( prefix ) ) {
            return filePath;
        }
        int startIndex = prefix.length();
        if ( filePath.charAt( startIndex ) == File.separatorChar ) {
            startIndex++;
        }
        return filePath.substring( startIndex );
    }
}
