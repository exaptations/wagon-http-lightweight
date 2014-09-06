package org.apache.maven.wagon;

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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


/**
 * Variant of FileOutputStream which creates the file only when first portion
 * of data is written.
 *
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id: LazyFileOutputStream.java 668181 2008-06-16 15:05:52Z brett $
 */
public class LazyFileOutputStream
    extends OutputStream
{

    private File file;

    private FileOutputStream delegee;


    public LazyFileOutputStream( String filename )
    {
        this.file = new File( filename );
    }

    public LazyFileOutputStream( File file )
    {
        this.file = file;
    }


    public void close()
        throws IOException
    {
        if ( delegee != null )
        {
            delegee.close();
        }
    }


    public boolean equals( Object obj )
    {
        return delegee.equals( obj );
    }


    public void flush()
        throws IOException
    {
        if ( delegee != null )
        {
            delegee.flush();
        }
    }


    public FileChannel getChannel()
    {
        return delegee.getChannel();
    }


    public FileDescriptor getFD()
        throws IOException
    {
        return delegee.getFD();
    }

    public int hashCode()
    {
        return delegee.hashCode();
    }


    public String toString()
    {
        return delegee.toString();
    }

    public void write( byte[] b )
        throws IOException
    {
        if ( delegee == null )
        {
            initialize();
        }

        delegee.write( b );
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write( byte[] b, int off, int len )
        throws IOException
    {
        if ( delegee == null )
        {
            initialize();
        }

        delegee.write( b, off, len );
    }

    /**
     * @param b
     * @throws java.io.IOException
     */
    public void write( int b )
        throws IOException
    {
        if ( delegee == null )
        {
            initialize();
        }

        delegee.write( b );
    }


    /**
     * 
     */
    private void initialize()
        throws FileNotFoundException
    {
        delegee = new FileOutputStream( file );
    }
}
