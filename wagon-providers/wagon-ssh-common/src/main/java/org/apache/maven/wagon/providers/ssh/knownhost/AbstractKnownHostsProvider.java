package org.apache.maven.wagon.providers.ssh.knownhost;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Common base for known hosts providers.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: AbstractKnownHostsProvider.java 730869 2009-01-02 23:35:51Z dantran $
 */
public abstract class AbstractKnownHostsProvider
    implements KnownHostsProvider
{
    /**
     * Valid values are ask, yes, no.
     * 
     * @plexus.configuration default-value="ask"
     */
    private String hostKeyChecking = "ask";

    /**
     * the known hosts, in the openssh format
     */
    protected String contents;
    
    protected Set knownHosts = new HashSet();

    public void setHostKeyChecking( String hostKeyChecking )
    {
        this.hostKeyChecking = hostKeyChecking;
    }

    public String getHostKeyChecking()
    {
        return hostKeyChecking;
    }

    public String getContents()
    {
        return contents;
    }
    
    public void storeKnownHosts( String contents )
        throws IOException
    {
    }
    
    
}
