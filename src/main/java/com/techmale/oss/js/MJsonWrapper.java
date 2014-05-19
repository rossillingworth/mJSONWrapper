package com.techmale.oss.js;

import mjson.Json;

import java.util.Iterator;
import java.util.Map;

/**
 * Note:
 * asString will fail if called on an object rather than a primitive
 * use toString instead
 */
public class MJsonWrapper {

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

    /**
     * Compare current data against a stringified JSON object
     *
     * @param json
     * @return
     */
    public boolean compare(String json){
        return compare(Json.read(json));
    }

    /**
     * Compare current data against another Json object
     * @param json
     * @return
     */
    public boolean compare(Json json){
        return compare(this.json,json);
    }


    /**
     * Compare 2 JSON objects in String form
     *
     * @param json1
     * @param json2
     * @return
     */
    public static boolean compare(String json1, String json2){
        return compare(Json.read(json1),Json.read(json2));
    }

    /**
     * Compare 2 Json objects
     * Uses a tree walk, so may be a bit slow
     *
     * @param json1
     * @param json2
     * @return
     */
    public static boolean compare(Json json1, Json json2){

        Map<String,Object> map1 = json1.asMap();
        Map<String,Object> map2 = json2.asMap();

        // check size
        boolean ok = map1.size() == map2.size();

        Iterator<String> it = map1.keySet().iterator();
        while(ok && it.hasNext()){
            String key = it.next();
            if(json1.at(key).isObject()){
                if(json2.has(key) && json2.at(key).isObject()){
                    ok = compare(json1.at(key),json2.at(key));
                }else{
                    ok = false;
                }
            }else{
                String v1 = json1.at(key).asString();
                String v2 = json2.at(key).asString();
                ok = v1.equals(v2);
            }
        }


        return ok;
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