package com.techmale.oss.js;

import mjson.Json;

import java.util.*;


/**
 * Comparator for 2 mJson object
 */
public class MjsonComparator {

    private LinkedList<String> logList = new LinkedList();

    /**
     * Compare 2 MjsonWrapper Objects
     *
     * @param mJsonWrapper1
     * @param mJsonWrapper2
     * @return
     */
    public boolean compare(MJsonWrapper mJsonWrapper1, MJsonWrapper mJsonWrapper2){
        return compare(mJsonWrapper1.get(), mJsonWrapper2.get());
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

        boolean ok = true;

        try{

            MjsonType type1 = MjsonType.typeOf(json1);
            MjsonType type2 = MjsonType.typeOf(json2);

            if(!type1.equals(type2)){
                ok = ok && false;
                check(ok,false,"ERROR: type mismatch");
            }else{
                switch (type1){
                    case OBJECT:
                        // verify maps match
                        // iterate object properties, send each to compare
                        if(compareObjectMaps(json1.asMap(),json2.asMap())){
                            Iterator<String> it = json1.asMap().keySet().iterator();
                            while(ok && it.hasNext()){
                                String key = it.next();
                                logList.push(key);
                                ok = compare(json1.at(key),json2.at(key));
                                check(ok,true,"");
                            }
                        }else{
                            ok = false;
                            check(ok,true,""); // empty msg, as already handled
                        }
                        break;
                    case ARRAY:
                        // verify similar size arrays
                        // iterate array entities, send each to compare
                        List<Json> list1 = json1.asJsonList();
                        List<Json> list2 = json2.asJsonList();
                        if(list1.size() == list2.size()){
                            for(int i = 0; i < list1.size(); i++){
                                logList.push(Integer.toString(i));
                                ok = compare(list1.get(i),list2.get(i));
                                check(ok,true,"Array item match failed");
                            }
                        }else{
                            ok = false;
                            check(ok,true,"Array index mismatch");
                        }
                        break;
                    case NULL:
                        // do nothing as both nulls
                        break;
                    case BOOLEAN:
                        boolean b1 = json1.asBoolean();
                        boolean b2 = json2.asBoolean();
                        ok = b1 == b2;
                        check(ok,false,"Boolean value match failed");
                        break;
                    case NUMBER:
                    case STRING:
                        try{
                            String v1 = json1.asString();
                            String v2 = json2.asString();
                            ok = v1.equals(v2);
                            check(ok,false,"Value match failed");
                        }catch(UnsupportedOperationException uoe){
                            uoe.printStackTrace();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown JSON Type");
                }
            }

        }catch(UnsupportedOperationException uoe){
            System.out.println(getMsg());
            System.out.println(String.format("Error js1: %s",json1.toString()));
            System.out.println(String.format("Error js2: %s",json2.toString()));
            uoe.printStackTrace();
        }

        return ok;
    }

    /**
     * Compare property maps of Json Object
     * @param map1
     * @param map2
     * @return
     */
    public boolean compareObjectMaps(Map<String,Object> map1, Map<String,Object> map2){
        boolean ok = true;
        Set keySet1 = map1.keySet();
        Set keySet2 = map2.keySet();
        Set diff = calculateSymmetricDifference(keySet1,keySet2);
        if(diff.size()>0){
            ok = false;
            check(ok,false,"Properties mismatched: " + diff.toString());
        }

        return ok;
    }

    public Set calculateSymmetricDifference(Set s1,Set s2){
        // build a set containing all items from both sets - union
        Set symmetricDiff = new HashSet();
        symmetricDiff.addAll(s1);
        symmetricDiff.addAll(s2);
        // build a set of items contained in both Sets - intersection
        Set tmp = new HashSet();
        tmp.addAll(s1);
        tmp.retainAll(s2);
        // remove intersection from union, leaving Difference Set
        symmetricDiff.removeAll(tmp);
        return symmetricDiff;
    }

    /**
     * TODO: rewrite this
     * TODO: add to an array as you pass into a object, remove as you pass back
     *
     * @param ok
     * @param msg
     * @param args
     */
    private void check(boolean ok,boolean removeLastLog, String msg, Object... args){
        if(ok){
            if(removeLastLog){
                logList.pop();
            }
        }else{
            String formattedMsg = String.format(msg,args);
            if(formattedMsg.trim().length() > 0){
                logList.add(formattedMsg);
            }
        }
    }

    /**
     * Get path to point of failure in comparator
     * @return
     */
    public String getMsg(){
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for(String inStr : logList) {
            builder.append(prefix + inStr);
            prefix = " -> ";
        }
        return builder.toString();
    }

    public void clearMsg(){
        logList = new LinkedList();
    }

}
