package com.techmale.oss.js;

import mjson.Json;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MJsonWrapperTest {

    @Test
    public void createEmptyTest() {

        MJsonWrapper mJsonWrapper = new MJsonWrapper();
        assertEquals("{}", mJsonWrapper.get().toString());

    }

    @Test
    public void createEmptyObjectAndSetValuesTest() {

        MJsonWrapper mJsonWrapper = new MJsonWrapper();

        mJsonWrapper.set("a", 1);
        assertEquals(1, mJsonWrapper.get("a").asInteger());

        mJsonWrapper.set("b.c.d.e.f", 1);
        assertEquals(1, mJsonWrapper.get("b.c.d.e.f").asInteger());

    }

    @Test
    /**
     * Simply confirm the test file exists
     * Unneeded really, as it would throw an exception, but nice to have
     */
    public void parseConfirmTestFileExistsTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        assertNotNull(jsonString);

    }

        @Test
    public void parseFile_ExtractData_ConfirmTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        assertEquals(3, mJsonWrapper.get("c").asInteger());
        assertEquals("3", mJsonWrapper.get("c").asString());
        assertEquals(6, mJsonWrapper.get("e.f").asInteger());
        assertEquals(true, mJsonWrapper.get("e.g").asBoolean());

    }

    @Test
    public void createPropertiesTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        mJsonWrapper.set("x.y.z.boolean", true);
        mJsonWrapper.set("x.y.z.integer", 1);
        mJsonWrapper.set("x.y.z.string", "hello world");
        mJsonWrapper.set("x.y.z.char", "c");
        mJsonWrapper.set("x.y.z.map", new HashMap() {{put("key1", "val1");}});

        String expectedString = readFileAsString("/mjsonwrapper/expected_properties.json");
        Json expectedJson = Json.read(expectedString);

        assertTrue(mJsonWrapper.equals(expectedJson));

        assertEquals(true, mJsonWrapper.get("x.y.z.boolean").asBoolean());
        assertEquals(1, mJsonWrapper.get("x.y.z.integer").asInteger());
        assertEquals("hello world", mJsonWrapper.get("x.y.z.string").asString());
        assertEquals('c', mJsonWrapper.get("x.y.z.char").asChar());
        assertEquals(new HashMap() {{put("key1", "val1");}}, mJsonWrapper.get("x.y.z.map").asMap());

    }


    //cause exceptions
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void missingPropertyThrowsExceptionTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        mJsonWrapper.get("e.z");

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void expectedObjectIsValueThrowsExceptionTest() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        mJsonWrapper.get("b.c");

    }

    @Test
    public void typeTest() throws IOException {
        // load JSON string
        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        assertEquals(MjsonType.STRING ,mJsonWrapper.typeOf("types.string"));
        assertEquals(MjsonType.NUMBER ,mJsonWrapper.typeOf("types.number"));
        assertEquals(MjsonType.OBJECT ,mJsonWrapper.typeOf("types.object"));
        assertEquals(MjsonType.ARRAY  ,mJsonWrapper.typeOf("types.array"));
        assertEquals(MjsonType.BOOLEAN,mJsonWrapper.typeOf("types.boolean"));
        assertEquals(MjsonType.NULL   ,mJsonWrapper.typeOf("types.null"));

    }

    @Test
    public void testEqualsWorks() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString);

        assertTrue(mJsonWrapper1.equals(mJsonWrapper2));

    }

    @Test
    public void testEqualsAgainstString() throws IOException {

        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString);
        assertTrue(mJsonWrapper1.equals(jsonString));

    }

    @Test
    public void testMerge() throws IOException {

        String jsonString1 = readFileAsString("/mjsonwrapper/test1.json");
        String jsonString2 = readFileAsString("/mjsonwrapper/test2.json");
        String jsonString3 = readFileAsString("/mjsonwrapper/mergeResult.json");

        MJsonWrapper mJsonWrapper1 = new MJsonWrapper(jsonString1);
        MJsonWrapper mJsonWrapper2 = new MJsonWrapper(jsonString2);

        MJsonWrapper mJsonWrapper3 =mJsonWrapper1.merge(mJsonWrapper2);

        //System.out.println(mJsonWrapper1.toString());

        assertTrue(mJsonWrapper3.equals(jsonString3));

    }

    @Test
    public void testToString(){

        String json = "{\"a\":{\"b\":1}}";
        MJsonWrapper mJsonWrapper = new MJsonWrapper(json);
        String output = mJsonWrapper.toString();

        assertEquals(json,output);
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
