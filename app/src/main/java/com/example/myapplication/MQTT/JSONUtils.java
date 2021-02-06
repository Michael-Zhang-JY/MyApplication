package com.example.myapplication.MQTT;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {
    private static final String TAG = "JSONUtils";

    /**
     * @description: 构建JSON指令
     * @param streamName
     * @param value
     * @return
     */
    public static String createCommandJSONString(String streamName, JSONObject value) {
        JSONObject command = new JSONObject();
        JSONArray datastreams = new JSONArray();
        command.put("datastreams", datastreams);
        JSONObject dataStream = new JSONObject();
        datastreams.add(dataStream);
        dataStream.put("id", streamName);
        JSONArray datapoints = new JSONArray();
        JSONObject datapoin = new JSONObject();
        dataStream.put("datapoints", datapoints);
        datapoints.add(datapoin);
        datapoin.put("value", value);

        String commandStr = command.toJSONString();
//        Log.d(TAG, "构建完成的指令内容: " + commandStr);
        return commandStr;
    }
}
