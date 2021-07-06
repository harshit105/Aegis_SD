package com.example.aegis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser {
    private HashMap<String,String> parseJSONObject(JSONObject object){
        JSONObject locationObject= null;
        HashMap<String,String> dataList=new HashMap<>();
        try {
            String name=object.getString("name");
            locationObject = object.getJSONObject("geometry").getJSONObject("location");
            String latitude=locationObject.getString("lat");
            String longitude=locationObject.getString("lng");
            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    private List<HashMap<String,String>> parseJSONArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist=new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            try{
                HashMap<String,String> data=parseJSONObject((JSONObject) jsonArray.get(i));
                datalist.add(data);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray=null;
        try {
            jsonArray=object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJSONArray(jsonArray);
    }
}
