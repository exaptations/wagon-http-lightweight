package org.apache.maven.wagon.providers.http;

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
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.shared.http.AbstractHttpClientWagon;
import org.apache.maven.wagon.shared.http.HtmlFileListParser;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id: HttpWagon.java 745730 2009-02-19 05:15:51Z brett $
 */
public class HttpWagon
    extends AbstractHttpClientWagon
{ 
    public List getFileList( String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException 
    {
        if ( destinationDirectory.length() > 0 && !destinationDirectory.endsWith( "/" ) )
        {
            destinationDirectory += "/";
        }

        String url = getRepository().getUrl() + "/" + destinationDirectory;

        GetMethod getMethod = new GetMethod( url );

        try
        {
            int statusCode = execute( getMethod );

            fireTransferDebug( url + " - Status code: " + statusCode );

            // TODO [BP]: according to httpclient docs, really should swallow the output on error. verify if that is required
            switch ( statusCode )
            {
                case HttpStatus.SC_OK:
                    break;

                case SC_NULL:
                    throw new TransferFailedException( "Failed to transfer file: " );

                case HttpStatus.SC_FORBIDDEN:
                    throw new AuthorizationException( "Access denied to: " + url );

                case HttpStatus.SC_UNAUTHORIZED:
                    throw new AuthorizationException( "Not authorized." );

                case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
                    throw new AuthorizationException( "Not authorized by proxy." );

                case HttpStatus.SC_NOT_FOUND:
                    throw new ResourceDoesNotExistException( "File: " + url + " does not exist" );

                    //add more entries here
                default :
                    throw new TransferFailedException(
                        "Failed to transfer file: " + url + ". Return code is: " + statusCode );
            }

            InputStream is = null;
            
            is = getMethod.getResponseBodyAsStream();

            return HtmlFileListParser.parseFileList( url, is );
        }
        catch ( IOException e )
        {
            throw new TransferFailedException( "Could not read response body.", e );
        }
        finally
        {
            getMethod.releaseConnection();
        }
    }
}
