/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ftpserver.ssl;

import java.util.Properties;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.net.ftp.FTPSClient;

public class MinaCipherSuitesTest extends SSLTestTemplate {

    protected String getAuthValue() {
        return "TLS";
    }
    
    protected Properties createConfig() {
        Properties config = super.createConfig();
        config.setProperty("config.listeners.default.implicitSsl",
        "true");

        config.setProperty("config.listeners.default.ssl.enabledCipherSuites", "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA");

        return config;
    }

    protected FTPSClient createFTPClient() throws Exception {
        return new FTPSClient(true);
    }

    protected void doConnect() throws Exception {
    }

    
    /*
     * Only certain cipher suites will work with the keys and protocol 
     * we're using for this test. 
     * Two suites known to work is:
     *  * SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA
     *  * SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA
     */
    public void testEnabled() throws Exception {
        
        client.setEnabledCipherSuites(new String[]{ 
                "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA"});
        
        super.doConnect();
    }
    
    public void testDisabled() throws Exception {
        
        client.setEnabledCipherSuites(new String[]{ 
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"});
        
        try {
            super.doConnect();
            fail("Must throw SSLHandshakeException"); 
        } catch(SSLHandshakeException e) {
            // OK
        }
    }

}
