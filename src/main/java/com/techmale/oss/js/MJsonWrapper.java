package com.techmale.oss.js;

import mjson.Json;

import java.util.Map;

/**
 * Note:
 * asString will fail if called on an object rather than a primitive
 * use toString instead
 */
public class MJsonWrapper {

    public enum TYPE {STRING,NUMBER,OBJECT,ARRAY,BOOLEAN,NULL};

    private Json json;

    public MJsonWrapper() {
        json = Json.object();
    }

    public MJsonWrapper(String string) {
        json = Json.read(string);
    }

    public MJsonWrapper(Json json) {
        this.json = json;
    }

    /**
     * Get entire mJson object
     *
     * @return
     */
    public Json get() {
        return json;
    }

    /**
     * Get section of JSON object, down to values
     * Use asString, asInteger etc to cast returned value
     *
     * @param name namespace paths are allowed (eg: get("a.b.c.d.e").asString() )
     * @return
     */
    public Json get(String name) {
        return get(name, false);
    }

    /**
     * Access JSON data using js style notation
     *
     * @param name
     * @param createAllowed create path object if missing?
     * @return
     */
    public Json get(String name, boolean createAllowed) {
        Json j = this.json;
        String[] names = name.split("\\.");
        String lastName = null;
        for (String n : names) {
            if (j.isObject()) {
                if (j.has(n)) {
                    j = j.at(n);
                } else if (createAllowed) {
                    j.set(n, Json.object());
                    j = j.at(n);
                } else {
                    throw new IllegalArgumentException(String.format("JSON data is missing expected property['%s'] path[%s] json[%s]", n, name, json.toString()));
                }
            } else {
                // is a value
                throw new IllegalArgumentException(String.format("JSON data property['%s'] is not an object as expected, path[%s] json[%s]", lastName, name, json.toString()));
            }
            lastName = n;
        }
        return j;
    }

    /**
     * Set a value in your JSON
     * Allows you to specify a full path to the property
     * Will create all required object in the JSON heirarchy
     *
     * @param path
     * @param value
     */
    public void set(String path, Object value) {
        String[] names = path.split("\\.");
        String lastName = names[names.length - 1];
        Json j = get(path, true);
        j = j.up();
        j.set(lastName, value);
    }

    public MjsonType typeOf(String path){
        Json json = this.get(path);
        return MjsonType.typeOf(json);
    }


    /**
     * Compare current data against a stringified JSON object
     *
     * @param json
     * @return
     */
    public boolean equals(String json){
        return equals(Json.read(json));
    }



    /**
     * Compare current data against another MJsonWrapper object
     * @param json
     * @return
     */
    public boolean equals(MJsonWrapper json){
        return equals(json.get());
    }

    /**
     * Compare current data against another Json object
     * @param json
     * @return
     */
    public boolean equals(Json json){
        MjsonComparator mjsonComparator = new MjsonComparator();
        return mjsonComparator.compare(this.get(),json);
    }

    /**
     * Merge current JSON with injected JSON
     * and return a new mJSONwrapper object
     *
     * @param json
     * @return a new MjsonWrapper object of overridden JSON data
     */
    public MJsonWrapper merge(MJsonWrapper json){

        Map jsonMap = this.get().asMap();
        Map jsonOverwriteMap = json.get().asMap();

        // inject into jsonMap
        jsonMap.putAll(jsonOverwriteMap);

        Json.DefaultFactory factory = new Json.DefaultFactory();
        return new MJsonWrapper(factory.make(jsonMap));
    }

    /**
     * Return entire JSON object in Stringified form
     *
     * @return
     */
    public String toString() {
        return json.toString();
    }

}