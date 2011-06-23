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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.FileUtils;

public class DependencyService {

    protected DependencyService() {

        // do nothing
    }

    /**
     * 
     * @return
     */
    public static final ArtifactInstaller getArtifactInstaller() {

        return JoJoMojo.getMojo().getArtifactInstaller();
    }

    /**
     * 
     * @return
     */
    public static final ArtifactFactory getArtifactFactory() {

        return JoJoMojo.getMojo().getArtifactFactory();
    }

    /**
     * 
     * @return
     */
    public static final ArtifactResolver getArtifactResolver() {

        return JoJoMojo.getMojo().getArtifactResolver();
    }

    /**
     * 
     * @return
     */
    public static final ArtifactRepository getLocalRepository() {

        return JoJoMojo.getMojo().getLocalRepository();
    }

    /**
     * 
     * @return
     */
    public static final List<ArtifactRepository> getRemoteRepositories() {

        return JoJoMojo.getMojo().getRemoteRepositories();
    }

    /**
     * Copy all {@link Dependency} {@link Artifact}s to the specified destination directory.
     * @param dest The destination directory.
     * @throws Exception
     */
    public static final void copyDependencies( File dest ) throws Exception {

        MavenProject mavenProject = ProjectService.getProject();

        Set<Artifact> artifacts = mavenProject.getDependencyArtifacts();
        Iterator<Artifact> iterator = artifacts.iterator();

        while (iterator.hasNext()) {
            Artifact artifact = iterator.next();
            FileUtils.copyFileToDirectory( artifact.getFile(), dest );
        }
    }

    /**
     * Extract the contents of all {@link Dependency} {@link Artifact}s to the specified destination directory.
     * @param dest The destination directory.
     * @throws ArchiverException
     * @throws ProjectBuildingException
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     */
    public static final void extractDependencies( File dest ) throws Exception {

        extractDependencies( ProjectService.getProject(), dest );
    }

    /**
     * Extract the contents of all {@link Dependency} {@link Artifact}s to the specified destination directory.
     * @param mavenProject
     * @param dest The destination directory.
     * @throws ArchiverException
     * @throws ProjectBuildingException
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     */
    public static final void extractDependencies( MavenProject mavenProject, File dest ) throws Exception {

        List<Dependency> dependencies = mavenProject.getDependencies();
        Iterator<Dependency> iterator = dependencies.iterator();

        while (iterator.hasNext()) {
            extractDependency( iterator.next(), dest );
        }
    }

    /**
     * Extract the contents of a {@link Dependency} {@link Artifact} to the specified destination directory.
     * @param dependency The {@link Dependency} to extract.
     * @param dest The destination directory.
     * @throws ArchiverException
     * @throws ProjectBuildingException
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     */
    public static final void extractDependency( Dependency dependency, File dest ) throws Exception {

        Artifact artifact = createResolvedArtifact( dependency );
        File src = artifact.getFile();

        StringBuffer destPath = new StringBuffer();
        destPath.append( dest );
        destPath.append( File.separator );
        destPath.append( artifact.getArtifactId() );
        dest = new File( destPath.toString() );

        ArchiveService.unarchive( src, dest );

        extractDependencies( ProjectService.getProject( artifact ), dest );
    }

    /**
     * Resolve the {@link Artifact} for the specified {@link Dependency}.
     * @param dependency The {@link Dependency} to resolve.
     * @return
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     */
    public static final Artifact createResolvedArtifact( Dependency dependency ) throws ArtifactResolutionException, ArtifactNotFoundException {

        Artifact artifact = createDependencyArtifact( dependency );
        resolve( artifact );
        return artifact;
    }

    /**
     * Create the {#link Artifact} for the specified {@link Dependency}.
     * @param dependency The {@link Dependency} to create the {@link Artifact} for.
     * @return
     */
    public static final Artifact createDependencyArtifact( Dependency dependency ) {

        ArtifactFactory artifactFactory = getArtifactFactory();
        VersionRange versionRange = VersionRange.createFromVersion( dependency.getVersion() );
        return artifactFactory.createDependencyArtifact( dependency.getGroupId(), dependency.getArtifactId(), versionRange, dependency.getType(), dependency.getClassifier(), dependency.getScope(), dependency.isOptional() );
    }

    public static final Artifact createArtifact( Artifact artifact ) {

        ArtifactFactory artifactFactory = getArtifactFactory();
        return artifactFactory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getScope(), artifact.getType() );
    }

    public static final Artifact createArtifactWithClassifier( Artifact artifact, String classifier ) {

        ArtifactFactory artifactFactory = getArtifactFactory();
        return artifactFactory.createArtifactWithClassifier( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), classifier );
    }

    /**
     * Resolve the specified {@link Artifact}.
     * @param artifact The {@link Artifact} to resolve.
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     */
    public static final void resolve( Artifact artifact ) throws ArtifactResolutionException, ArtifactNotFoundException {

        ArtifactResolver artifactResolver = getArtifactResolver();
        artifactResolver.resolve( artifact, getRemoteRepositories(), getLocalRepository() );
    }

    public static final void install( File src ) throws ArtifactInstallationException {

        install( src, ProjectService.getProject().getArtifact() );
    }

    public static final ArtifactMetadata createArtifactMetadata( Artifact artifact ) throws IOException {

        File src = new File( TargetDirectoryService.getTempPackagePath() + ".pom" );
        ProjectService.writeOriginalModel( src );
        return new ProjectArtifactMetadata( artifact, src );
    }

    public static final void install( File src, Artifact artifact ) throws ArtifactInstallationException {

        try {
            artifact.addMetadata( createArtifactMetadata( artifact ) );
        }
        catch (IOException e) {
            throw new ArtifactInstallationException( e.getMessage(), e );
        }

        ArtifactInstaller artifactInstaller = getArtifactInstaller();
        artifactInstaller.install( src, artifact, DependencyService.getLocalRepository() );
    }
}
