package com.techmale.oss.js;

import mjson.Json;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MJsonWrapperTest {

    @Test
    public void createEmptyTest() {

        MJsonWrapper mJsonWrapper = new MJsonWrapper();
        assertEquals("{}", mJsonWrapper.get().toString());

    }

    @Test
    public void createEmptyAndSetValuesTest() {

        MJsonWrapper mJsonWrapper = new MJsonWrapper();

        mJsonWrapper.set("a", 1);
        assertEquals(1, mJsonWrapper.get("a").asInteger());

        mJsonWrapper.set("b.c.d.e.f", 1);
        assertEquals(1, mJsonWrapper.get("b.c.d.e.f").asInteger());

    }

    @Test
    public void parseFileExtractDataAndConfirmTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        assertNotNull(jsonString);

        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);

        assertEquals(3, mJsonWrapper.get("c.d").asInteger());
        assertEquals("3", mJsonWrapper.get("c.d").asString());

    }

    @Test
    public void compareTest() throws IOException {

        String jsonString1 = readFileAsString("/mjsonwrapper/test1.json");
        String jsonString2 = readFileAsString("/mjsonwrapper/test2.json");
        String jsonString3 = readFileAsString("/mjsonwrapper/test3.json");

        assertTrue(MJsonWrapper.compare(jsonString1, jsonString1));
        assertFalse(MJsonWrapper.compare(jsonString1, jsonString2));
        assertFalse(MJsonWrapper.compare(jsonString1, jsonString3));
    }

    @Test
    @Ignore
    public void createProperties() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        assertNotNull(jsonString);

        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);

        mJsonWrapper.set("x.y.z.boolean", true);
        mJsonWrapper.set("x.y.z.integer", 1);
        mJsonWrapper.set("x.y.z.string", "hello world");
        mJsonWrapper.set("x.y.z.char", "c");
        mJsonWrapper.set("x.y.z.map", new HashMap() {{
            put("key1", "val1");
        }});

        String expect = "{\"e\":{},\"b\":2,\"c\":{\"d\":3},\"a\":1,\"x\":{\"y\":{\"z\":{\"char\":\"c\",\"integer\":1,\"string\":\"hello world\",\"map\":{\"key1\":\"val1\"},\"boolean\":true}}}}";

        Json expectJson = Json.read(expect);
        assertTrue(expectJson.equals(mJsonWrapper.get()));

        assertEquals(true, mJsonWrapper.get("x.y.z.boolean").asBoolean());
        assertEquals(1, mJsonWrapper.get("x.y.z.integer").asInteger());
        assertEquals("hello world", mJsonWrapper.get("x.y.z.string").asString());
        assertEquals('c', mJsonWrapper.get("x.y.z.char").asChar());
        assertEquals(new HashMap() {{
            put("key1", "val1");
        }}, mJsonWrapper.get("x.y.z.map").asMap());

    }


    //cause exceptions
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void missingPropertyThrowsExceptionTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        assertNotNull(jsonString);

        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        mJsonWrapper.get("e.f");

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void expectedObjectIsValueThrowsExceptionTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        assertNotNull(jsonString);

        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        mJsonWrapper.get("b.c");

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
