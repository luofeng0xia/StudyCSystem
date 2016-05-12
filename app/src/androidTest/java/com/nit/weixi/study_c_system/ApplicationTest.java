package com.nit.weixi.study_c_system;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.nit.weixi.study_c_system.tools.MyConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test1(){
        boolean test1=true;
        if (test1){
            System.out.println("test:"+test1);
        }else {
            System.out.println("testelse:"+!test1);
        }
    }

    public void testDB(){
        File dbFile=getContext().getDatabasePath(MyConstants.dbName);
        System.out.println(dbFile.getAbsolutePath());
    }


}