/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.io.File;

public class SeerGuiUtilsTest {

    public static void main(String[] args) throws Exception {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SeerGuiUtils.openDirectory(new File("E:\\"), "whatever");
    }
}
