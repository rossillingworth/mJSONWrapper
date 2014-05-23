package com.techmale.oss.js;

import mjson.Json;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
     * Compare current data against another MJsonWrapper object
     * @param json
     * @return
     */
    public boolean compare(MJsonWrapper json){
        return compare(this.json,json.get());
    }


    /**
     * Compare 2 Json objects
     * Uses a tree walk, so may be a bit slow
     *
     * @param json1
     * @param json2
     * @return
     */
    public boolean compare(Json json1, Json json2){

        Map<String,Object> map1 = json1.asMap();
        Map<String,Object> map2 = json2.asMap();

        // check size
        boolean ok = map1.size() == map2.size();
        check(ok,"JSON Objects different sizes [%s] [%s]",map1.size(),map2.size());

        // verify names are identical
        if(ok){
            Set keySet1 = map1.keySet();
            Set keySet2 = map2.keySet();
            keySet2.removeAll(keySet1);
            ok = ok && keySet2.isEmpty();
            check(ok,"JSON Keys are different [%s]",keySet2.toString());
        }

        //
        // Loop through keyset,
        // - make sure key exists is 2nd object
        // - if either object is an Object/array/primitive -> make sure both are
        //
        String key = null;
        try{
            Iterator<String> it = map1.keySet().iterator();
            while(ok && it.hasNext()){
                key = it.next();
                if(!json2.has(key)){
                    check(ok,"json2 missing %s",key);
                    ok = false;
                }else{

                    MjsonType type1 = MjsonType.typeOf(json1.at(key));
                    MjsonType type2 = MjsonType.typeOf(json2.at(key));

                    if(!type1.equals(type2)){
                        ok = false;
                        check(ok,"Name[%s] type mismatch",key);
                    }else{
                        switch (type1){
                            case OBJECT:
                            case ARRAY:
                                ok = compare(json1.at(key),json2.at(key));
                                break;
                            case NULL:
                                // do nothing as both nulls
                                break;
                            case BOOLEAN:
                                boolean b1 = json1.at(key).asBoolean();
                                boolean b2 = json2.at(key).asBoolean();
                                ok = b1 == b2;
                                break;
                            case NUMBER:
                            case STRING:
                                try{
                                String v1 = json1.at(key).asString();
                                String v2 = json2.at(key).asString();
                                ok = v1.equals(v2);
                                }catch(UnsupportedOperationException uoe){
                                    uoe.printStackTrace();
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown JSON Type");
                        }
                        check(ok,"Name[%s] mismatch",key);
                    }
                }
            }
        }catch(UnsupportedOperationException uoe){

            System.out.println(String.format("Error js1: %s",json1.toString()));
            System.out.println(String.format("Error js2: %s",json2.toString()));
            System.out.println(String.format("looking for: %s",key));
            uoe.printStackTrace();
        }

        return ok;
    }


    /**
     * TODO: rewrite this
     * TODO: add to an array as you pass into a object, remove as you pass back
     *
     * @param ok
     * @param msg
     * @param args
     */
    private void check(boolean ok, String msg, Object... args){
        if(!ok){
            String formatedMsg = String.format(msg,args);
            System.out.println(formatedMsg);
        }
    }

    /**
     * Compare 2 JSON objects in String form
     *
     * @param json1
     * @param json2
     * @return
     */
    public static boolean compare(String json1, String json2){
        MJsonWrapper mJsonWrapper = new MJsonWrapper(json1);
        return mJsonWrapper.compare(json2);
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