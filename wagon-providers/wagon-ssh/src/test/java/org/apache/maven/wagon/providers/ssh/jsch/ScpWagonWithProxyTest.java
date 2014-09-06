package org.apache.maven.wagon.providers.ssh.jsch;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusTestCase;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

public class ScpWagonWithProxyTest
    extends PlexusTestCase
{
    private boolean handled;

    public void testHttpProxy()
        throws Exception
    {
        handled = false;
        Handler handler = new AbstractHandler()
        {
            public void handle( String target, HttpServletRequest request, HttpServletResponse response, int dispatch )
                throws IOException, ServletException
            {
                assertEquals( "CONNECT", request.getMethod() );

                handled = true;
                ( (Request) request ).setHandled( true );
            }
        };

        Server server = new Server( 0 );
        server.setHandler( handler );
        server.start();

        int port = server.getConnectors()[0].getLocalPort();
        ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setHost( "localhost" );
        proxyInfo.setPort( port );
        proxyInfo.setType( "http" );
        proxyInfo.setNonProxyHosts( null );

        Wagon wagon = (Wagon) lookup( Wagon.ROLE, "scp" );
        try
        {
            wagon.connect( new Repository( "id", "scp://localhost/tmp" ), proxyInfo );
            fail();
        }
        catch ( AuthenticationException e )
        {
            assertTrue( handled );
        }
        finally
        {
            if ( server != null )
            {
                server.stop();
            }
        }
    }

    public void testSocksProxy()
        throws Exception
    {
        handled = false;

        int port = ( Math.abs( new Random().nextInt() ) % 2048 ) + 1024;

        SocksServer s = new SocksServer( port );
        Thread t = new Thread( s );
        t.setDaemon( true );
        t.start();

        ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setHost( "localhost" );
        proxyInfo.setPort( port );
        proxyInfo.setType( "socks_5" );
        proxyInfo.setNonProxyHosts( null );

        Wagon wagon = (Wagon) lookup( Wagon.ROLE, "scp" );
        try
        {
            wagon.connect( new Repository( "id", "scp://localhost/tmp" ), proxyInfo );
            fail();
        }
        catch ( AuthenticationException e )
        {
            assertTrue( handled );
        }
        finally
        {
            t.interrupt();
        }
    }

    private final class SocksServer
        implements Runnable
    {

        private final int port;

        private String userAgent;

        private SocksServer( int port )
        {
            this.port = port;
        }

        public void run()
        {
            ServerSocket ssock = null;
            try
            {
                try
                {
                    ssock = new ServerSocket( port );
                    ssock.setSoTimeout( 2000 );
                }
                catch ( IOException e )
                {
                    return;
                }

                while ( !Thread.currentThread().isInterrupted() )
                {
                    Socket sock = null;
                    try
                    {
                        try
                        {
                            sock = ssock.accept();
                        }
                        catch ( SocketTimeoutException e )
                        {
                            continue;
                        }
                        
                        handled = true;
                    }
                    catch ( IOException e )
                    {
                    }
                    finally
                    {
                        if ( sock != null )
                        {
                            try
                            {
                                sock.close();
                            }
                            catch ( IOException e )
                            {
                            }
                        }
                    }
                }
            }
            finally
            {
                if ( ssock != null )
                {
                    try
                    {
                        ssock.close();
                    }
                    catch ( IOException e )
                    {
                    }
                }
            }
        }
    }
}
