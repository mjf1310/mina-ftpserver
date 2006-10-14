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

package org.apache.ftpserver.filesystem;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.BaseUser;
import org.apache.ftpserver.util.IoUtils;

public class NativeFileSystemViewTest extends TestCase {

    private static final File TEST_TMP_DIR = new File("test-tmp");

    protected static final File ROOT_DIR = new File(TEST_TMP_DIR, "ftproot");

    private static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    private static final File TEST_FILE1 = new File(ROOT_DIR, "file1");

    private static final File TEST_FILE2_IN_DIR1 = new File(TEST_DIR1, "file2");

    private static final String ROOT_DIR_PATH = ROOT_DIR.getAbsolutePath()
            .replace('\\', '/');

    private static final String FULL_PATH = ROOT_DIR_PATH + "/"
            + TEST_DIR1.getName() + "/" + TEST_FILE2_IN_DIR1.getName();

    private static final String FULL_PATH_NO_CURRDIR = ROOT_DIR_PATH + "/"
            + TEST_FILE2_IN_DIR1.getName();

    private BaseUser user;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        initDirs();

        TEST_DIR1.mkdirs();
        TEST_FILE1.createNewFile();
        TEST_FILE2_IN_DIR1.createNewFile();

        user = new BaseUser();
        user.setHomeDirectory(ROOT_DIR.getAbsolutePath());
    }

    public void testConstructor() throws FtpException {
        NativeFileSystemView view = new NativeFileSystemView(user);
        assertEquals("/", view.getCurrentDirectory().getFullName());
    }
    
    public void testChangeDirectory() throws Exception {
        NativeFileSystemView view = new NativeFileSystemView(user);
        assertEquals("/", view.getCurrentDirectory().getFullName());

        assertTrue(view.changeDirectory(TEST_DIR1.getName()));
        assertEquals("/" + TEST_DIR1.getName(), view.getCurrentDirectory().getFullName());
        
        assertTrue(view.changeDirectory("."));
        assertEquals("/" + TEST_DIR1.getName(), view.getCurrentDirectory().getFullName());

        assertTrue(view.changeDirectory(".."));
        assertEquals("/", view.getCurrentDirectory().getFullName());

        assertTrue(view.changeDirectory("./" + TEST_DIR1.getName()));
        assertEquals("/" + TEST_DIR1.getName(), view.getCurrentDirectory().getFullName());

        assertTrue(view.changeDirectory("~"));
        assertEquals("/", view.getCurrentDirectory().getFullName());
    }

    public void testChangeDirectoryCaseInsensitive() throws Exception {
        NativeFileSystemView view = new NativeFileSystemView(user, true);
        assertEquals("/", view.getCurrentDirectory().getFullName());
        
        assertTrue(view.changeDirectory("/DIR1"));
        assertEquals("/DIR1", view.getCurrentDirectory().getFullName());
        assertTrue(view.getCurrentDirectory().doesExist());
        
        assertTrue(view.changeDirectory("/dir1"));
        assertEquals("/dir1", view.getCurrentDirectory().getFullName());
        assertTrue(view.getCurrentDirectory().doesExist());

        assertTrue(view.changeDirectory("/DiR1"));
        assertEquals("/DiR1", view.getCurrentDirectory().getFullName());
        assertTrue(view.getCurrentDirectory().doesExist());
    }

    public void testConstructorWithNullUser() throws FtpException {
        try{
            new NativeFileSystemView(null);
            fail("Must throw IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    public void testConstructorWithNullHomeDir() throws FtpException {
        user.setHomeDirectory(null);
        try{
            new NativeFileSystemView(user);
            fail("Must throw IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // OK
        }
    }
    
    
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        cleanTmpDirs();
    }

    /**
     * @throws IOException
     */
    protected void initDirs() throws IOException {
        cleanTmpDirs();

        TEST_TMP_DIR.mkdirs();
        ROOT_DIR.mkdirs();
    }

    protected void cleanTmpDirs() throws IOException {
        if (TEST_TMP_DIR.exists()) {
            IoUtils.delete(TEST_TMP_DIR);
        }
    }

}
