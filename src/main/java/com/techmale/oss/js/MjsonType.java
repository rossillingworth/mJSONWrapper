package com.techmale.oss.js;


import mjson.Json;

/**
 * Simple ENUM to identify the type of an mJson Object.
 */
public enum MjsonType {

    OBJECT,ARRAY,STRING,NUMBER,BOOLEAN,NULL;


    /**
     * Identify the type of a Json object
     *
     * @param json
     * @return
     */
    public static MjsonType typeOf(Json json){
        if(json.isObject()){
            return OBJECT;
        }else if(json.isArray()){
            return ARRAY;
        }else if(json.isString()){
            return STRING;
        }else if(json.isBoolean()){
            return BOOLEAN;
        }else if(json.isNumber()){
            return NUMBER;
        }else if(json.isNull()){
            return NULL;
        }else{
            throw new IllegalStateException(String.format("Unknown type: JSON:%s",json.toString()));
        }
    }
}
