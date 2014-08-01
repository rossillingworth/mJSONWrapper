package com.techmale.oss.js;

import mjson.Json;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class MjsonComparatorTest {

    @Test
    public void compareIdenticalObjects() throws IOException {
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
    public void changePropertyType() throws IOException {
        // load JSON string
        String jsonString = readFileAsString("/mjsonwrapper/test1.json");

        // populate 2 identical objects
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString);

        mJsonWrapper1.set("a", "999");
        MjsonComparator mjsonComparator = new MjsonComparator();
        // test 1 way
        boolean diff1 = mjsonComparator.compare(mJsonWrapper1, mJsonWrapper2);
        assertEquals(false, diff1);
        String error1 = mjsonComparator.getMsg();

        // clear errors
        mjsonComparator.clearMsg();

        // test reverse way
        boolean diff2 = mjsonComparator.compare(mJsonWrapper2, mJsonWrapper1);
        assertEquals(false, diff1);
        String error2 = mjsonComparator.getMsg();

        String expected = "a -> ERROR: type mismatch";
        assertEquals(expected,error1);
        assertEquals(expected,error2);
    }

    @Test
    public void aSmallChangeToOneSubObjectWithReverseCompare() throws IOException {

        // load JSON string
        String jsonString1 = readFileAsString("/mjsonwrapper/test1.json");
        String expectedErrorMsg = "e -> Properties mismatched: [x]";

        // populate 2 identical objects
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString1);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString1);

        // modify one of the objects
        mJsonWrapper2.set("e.x",1);

        MjsonComparator mjsonComparator = new MjsonComparator();

        // test 1 way round
        boolean diff1 = mjsonComparator.compare(mJsonWrapper1.get(), mJsonWrapper2.get());
        String error1 = mjsonComparator.getMsg();

        assertEquals(false, diff1);
        assertEquals(expectedErrorMsg,error1);

        // now clear msgs
        mjsonComparator.clearMsg();

        // test the other way round
        boolean diff2 = mjsonComparator.compare(mJsonWrapper2.get(), mJsonWrapper1.get());
        String error2 = mjsonComparator.getMsg();

        assertEquals(false, diff2);
        assertEquals(expectedErrorMsg,error2);

    }


    @Test
    public void arraySizeModification() throws IOException {
        // load JSON string
        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        // populate 2 identical objects
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString);

        mJsonWrapper1.get("e.h").asJsonList().add(Json.object());

        MjsonComparator mjsonComparator = new MjsonComparator();
        boolean diff = mjsonComparator.compare(mJsonWrapper1, mJsonWrapper2);
        assertEquals(false, diff);

        String errorMsg = mjsonComparator.getMsg();

        String expected = "h -> e -> Array index mismatch";
        assertEquals(expected,errorMsg);
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
