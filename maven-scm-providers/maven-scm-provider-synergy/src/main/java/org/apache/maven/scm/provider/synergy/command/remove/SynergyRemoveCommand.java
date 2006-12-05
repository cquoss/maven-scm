package org.apache.maven.scm.provider.synergy.command.remove;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.Iterator;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.remove.AbstractRemoveCommand;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.synergy.command.SynergyCommand;
import org.apache.maven.scm.provider.synergy.repository.SynergyScmProviderRepository;
import org.apache.maven.scm.provider.synergy.util.SynergyUtil;

/**
 * @author <a href="mailto:julien.henry@capgemini.com">Julien Henry</a>
 */
public class SynergyRemoveCommand extends AbstractRemoveCommand implements SynergyCommand
{
    protected ScmResult executeRemoveCommand( ScmProviderRepository repository, ScmFileSet fileSet, String message )
            throws ScmException
    {
        getLogger().debug( "executing remove command..." );

        SynergyScmProviderRepository repo = ( SynergyScmProviderRepository ) repository;
        getLogger().debug( "basedir: " + fileSet.getBasedir() );

        String CCM_ADDR = SynergyUtil.start( getLogger(), repo.getUser(), repo.getPassword(), null );

        try
        {
            String project_spec = SynergyUtil.getWorkingProject( getLogger(), repo.getProjectSpec(), repo.getUser(),
                    CCM_ADDR );
            if ( project_spec == null )
            {
                throw new ScmException( "You should checkout project first" );
            }
            File WAPath = SynergyUtil.getWorkArea( getLogger(), project_spec, CCM_ADDR );
            File destPath = new File( WAPath, repo.getProjectName() );
            for ( Iterator i = fileSet.getFileList().iterator(); i.hasNext(); )
            {
                ScmFile f = ( ScmFile ) i.next();
                File source = new File( fileSet.getBasedir(), f.getPath() );
                File dest = new File( destPath, f.getPath() );
                SynergyUtil.delete( getLogger(), dest, CCM_ADDR, false );
                if ( !source.equals( dest ) )
                {
                    getLogger().debug( "Delete file [" + source + "]." );
                    dest.delete();
                }
            }
        }
        finally
        {
            SynergyUtil.stop( getLogger(), CCM_ADDR );
        }

        return new StatusScmResult( "", fileSet.getFileList() );
    }

}