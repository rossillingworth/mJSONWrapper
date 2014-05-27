package com.techmale.oss.js;

import mjson.Json;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MjsonComparatorTest {

    @Test
    public void compareTest1() throws IOException {
        // load JSON string
        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        // populate 2 identical objects
        Json json1 = Json.read(jsonString);
        Json json2 = Json.read(jsonString);

        MjsonComparator mjsonComparator = new MjsonComparator();
        assertEquals(true, mjsonComparator.compare(json1, json2));
        assertEquals(true, mjsonComparator.compare(json2, json1));

    }

    @Test
    public void compareTest2() throws IOException {
        // load JSON string
        String jsonString1 = readFileAsString("/mjsonwrapper/test1.json");
        // populate 2 identical objects
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString1);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString1);
        // modify one of the objects
        mJsonWrapper2.set("x",1);
        // test both variantsMjsonComparator mjsonComparator = new MjsonComparator();
        MjsonComparator mjsonComparator = new MjsonComparator();
        assertEquals(false, mjsonComparator.compare(mJsonWrapper1.get(), mJsonWrapper2.get()));
        String error1 = mjsonComparator.getMsg();
        mjsonComparator.clearMsg();
        assertEquals(false, mjsonComparator.compare(mJsonWrapper2.get(), mJsonWrapper1.get()));
        String error2 = mjsonComparator.getMsg();

        System.out.println(error1);
        System.out.println(error2);
    }



    /**
     * Load file from resouces
     *
     * @param path
     * @return
     * @throws IOException
     */
    public String readFileAsString(String path) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(path));
    }
}
