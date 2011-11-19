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
package org.hardisonbrewing.maven.core.cli;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.ProjectService;

/**
 * Utility methods for handling {@link CommandLine} execution.
 */
public class CommandLineService {

    protected CommandLineService() {

        // do nothing
    }

    public static final void appendEnvVar( Commandline commandLine, String key, String value ) {

        try {
            Properties systemEnvVars = commandLine.getSystemEnvVars();
            String _value = systemEnvVars.getProperty( key );
            commandLine.addEnvironment( key, value + File.pathSeparator + _value );
        }
        catch (Exception e) {
            throw new IllegalStateException( e.getMessage() );
        }
    }

    /**
     * Build a {@link CommandLine} instance for the specified arguments.
     * </br>The default working directory will be set to {@link ProjectService.getBaseDir()}.
     * @param cmd The arguments for the {@link CommandLine}.
     * @return
     * @throws CommandLineException
     */
    public static final Commandline build( List<String> cmd ) throws CommandLineException {

        if ( cmd.isEmpty() ) {
            throw new CommandLineException( "Argument length must be >= 1. The executable is the first element." );
        }

        Commandline commandLine = new Commandline();
        commandLine.setWorkingDirectory( ProjectService.getBaseDir() );
        commandLine.setExecutable( cmd.get( 0 ) );

        for (int i = 1; i < cmd.size(); i++) {
            commandLine.createArg().setValue( cmd.get( i ) );
        }

        return commandLine;
    }

    /**
     * Execute the specified arguments through a {@link CommandLine}.
     * @param cmd The arguments for the {@link CommandLine}.
     * @return
     * @throws CommandLineException
     */
    public static final int execute( List<String> cmd, StreamConsumer systemOut, StreamConsumer systemErr ) throws CommandLineException {

        return execute( build( cmd ), systemOut, systemErr );
    }

    /**
     * Execute the specified {@link CommandLine}.
     * @param commandLine The {@link CommandLine} to execute.
     * @return
     * @throws CommandLineException
     */
    public static final int execute( Commandline commandLine, StreamConsumer systemOut, StreamConsumer systemErr ) throws CommandLineException {

        return CommandLineUtils.executeCommandLine( commandLine, systemOut, systemErr );
    }
}
