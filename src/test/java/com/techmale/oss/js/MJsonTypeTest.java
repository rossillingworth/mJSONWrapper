package com.techmale.oss.js;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static com.techmale.oss.js.MjsonType.*;
import static org.junit.Assert.assertEquals;

public class MJsonTypeTest {

    @Test
    public void typeTest() throws IOException {

        // load JSON string
        String jsonString = readFileAsString("/mjsonwrapper/test1.json");
        MJsonWrapper mJsonWrapper = new MJsonWrapper(jsonString);
        // now test all types
        assertEquals(STRING ,mJsonWrapper.typeOf("types.string"));
        assertEquals(NUMBER ,mJsonWrapper.typeOf("types.number"));
        assertEquals(OBJECT ,mJsonWrapper.typeOf("types.object"));
        assertEquals(ARRAY  ,mJsonWrapper.typeOf("types.array"));
        assertEquals(BOOLEAN,mJsonWrapper.typeOf("types.boolean"));
        assertEquals(NULL   ,mJsonWrapper.typeOf("types.null"));

    }


    /**
     * Load file from resouces
     *
     * @param path
     * @return
     * @throws java.io.IOException
     */
    public String readFileAsString(String path) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(path));
    }
}
