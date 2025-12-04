package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrivateConfig {

    public static  String computer = "东京";
    public static  String daiLi = "0";
    public static String password = "sduroelejsyzbbac";
    public static RequestOptions options = new RequestOptions();
    public static final String syncRequestClient = "syncRequestClient";
    public static final String apiKey = "API_KEY";
    public static final String secretKey = "secretKey";
    public static final String spotClient = "spotClient";
    public static String ceShi = "1";
    public static  String genDan_isGenDan = "0";
    public static  String biCoin_isBiCoin = "0";
    public static  String biCoins_isBiCoins = "0";
    public static  String ok_isOk = "0";
    public static List<JSONObject> biCoin_personInfoList = new ArrayList<>();
    public static List<JSONObject> genDan_personInfoList= new ArrayList<>();
    public static List<JSONObject> biCoins_personInfoList= new ArrayList<>();
    public static List<JSONObject> ok_personInfoList= new ArrayList<>();
    public static List<JSONObject> personInfoList= new ArrayList<>();
    static JSONObject config;
    public static void init(String configPath) {

        config = readJsonFile(configPath  + "//info.json");

        if (config.getString("daiLi") != null) {
            daiLi = config.getString("daiLi");
        }

        if(config.get("ceShi") != null){
            ceShi = config.getString("ceShi");
        }

    }

    public static void init1() {

        if(config.getJSONObject("biCoin") != null) {

            JSONArray genDan_personInfo = config.getJSONObject("biCoin").getJSONArray("personInfo");
            for(Object object : genDan_personInfo){
                JSONObject jsonObject = (JSONObject)object;
                jsonObject.put(PrivateConfig.syncRequestClient , SyncRequestClient.create(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString(), options));
                jsonObject.put(PrivateConfig.spotClient, new SpotClientImpl(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString()));
                biCoin_personInfoList.add(jsonObject);
            }

            if (config.getJSONObject("biCoin").getString("isBiCoin") != null) {
                biCoin_isBiCoin = config.getJSONObject("biCoin").getString("isBiCoin");
            }
        }

        if(config.getJSONObject("biCoins") != null) {
            if (config.getJSONObject("biCoins").getString("isBiCoins") != null) {
                biCoins_isBiCoins = config.getJSONObject("biCoins").getString("isBiCoins");
            }

            JSONArray genDan_personInfo = config.getJSONObject("biCoins").getJSONArray("personInfo");
            for(Object object : genDan_personInfo){
                JSONObject jsonObject = (JSONObject)object;
                jsonObject.put(PrivateConfig.syncRequestClient , SyncRequestClient.create(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString(), options));
                jsonObject.put(PrivateConfig.spotClient, new SpotClientImpl(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString()));
                biCoins_personInfoList.add(jsonObject);
            }

        }

        if(config.getJSONObject("ok") != null) {
            if (config.getJSONObject("ok").getString("isOk") != null) {
                ok_isOk = config.getJSONObject("ok").getString("isOk");
            }

            JSONArray genDan_personInfo = config.getJSONObject("ok").getJSONArray("personInfo");
            for(Object object : genDan_personInfo){
                JSONObject jsonObject = (JSONObject)object;
                jsonObject.put(PrivateConfig.syncRequestClient , SyncRequestClient.create(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString(), options));
                jsonObject.put(PrivateConfig.spotClient, new SpotClientImpl(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString()));
                ok_personInfoList.add(jsonObject);
            }

        }

        if(config.getJSONObject("genDan") != null){

            if(config.getJSONObject("genDan").getString("isGenDan") != null){
                genDan_isGenDan = config.getJSONObject("genDan").getString("isGenDan");
            }
            JSONArray genDan_personInfo = config.getJSONObject("genDan").getJSONArray("personInfo");
            for(Object object : genDan_personInfo){
                JSONObject jsonObject = (JSONObject)object;
                jsonObject.put(PrivateConfig.syncRequestClient , SyncRequestClient.create(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString(), options));
                jsonObject.put(PrivateConfig.spotClient, new SpotClientImpl(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString()));
                genDan_personInfoList.add(jsonObject);
            }

        }

        JSONArray personInfo = config.getJSONArray("personInfo");
        if(personInfo!=null){
            List<JSONObject> list = new ArrayList<>();
            for(Object object : personInfo){
                JSONObject jsonObject = (JSONObject)object;
                jsonObject.put(PrivateConfig.syncRequestClient , SyncRequestClient.create(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString(), options));
                jsonObject.put(PrivateConfig.spotClient, new SpotClientImpl(jsonObject.get(PrivateConfig.apiKey).toString(), jsonObject.get(PrivateConfig.secretKey).toString()));
                list.add(jsonObject);
            }
            personInfoList = list;
        }
    }

    public static JSONObject readJsonFile(String fileName) {
        String jsonStr = "";
        try {
//            fileWriter.write("du qu json" + "\n");
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            JSONObject jobj = JSON.parseObject(jsonStr);
            return jobj;
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

}
