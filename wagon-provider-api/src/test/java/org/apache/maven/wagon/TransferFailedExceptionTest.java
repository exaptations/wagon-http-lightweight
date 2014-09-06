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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: TransferFailedExceptionTest.java 573459 2007-09-07 05:14:49Z brett $
 */
public class TransferFailedExceptionTest
    extends TestCase
{
    public void testTransferFailedExceptionTest()
    {
        TransferFailedException ae = new TransferFailedException( "message" );

        assertEquals( "message", ae.getMessage() );

        ae = new TransferFailedException( "full-message", new Throwable( "cause" ) );

        assertEquals( "full-message", ae.getMessage() );

        assertEquals( "cause", ae.getCause().getMessage() );
    }
}