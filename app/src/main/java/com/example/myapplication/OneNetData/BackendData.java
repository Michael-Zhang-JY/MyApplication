package com.example.myapplication.OneNetData;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.MQTT.MQTTService;

import java.util.Random;

public class BackendData {
    public static final String TAG = BackendData.class.getCanonicalName();
    public static final String DATASER = "DATAser";                         // 数据流名称
    public static final String DATACHANINFRESULT = "DATAchanInfresult";     // 出货数据
    public static final String DATAVERSION = "DATAVersion";                 // 版本号
    public static final String DATAURL = "DATAUrl";                         // 主动上报上线通知
    public static final String DATAMENDCHANS = "DATAmendChans";             // 上报设备补货信息
    public static final String DATASIMIMSI = "DATAsimimsi";                 // 上报设备补货信息
    public static final String DEALVERSION = "DEALVersion";                 // 协议版本号
    public static final String DATACLEAR = "DataClear";                     // 清空货道
    public static final String DATALIGHT = "DATALight";                     // 灯光数据

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
        return command.toJSONString();
    }

    /**
     * @ 随机生成随机字符串
     * */
    public static String getRandomString(){
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
//        Log.d(TAG, "getRandomString: " + sb.toString());
        return sb.toString();
    }

    /**
     * @ 请求鉴权信息
     */
    public static void AppearDealVersion() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "4");
        jsonObject.put("DealVersionName", "1.2");
        jsonObject.put("ser", "");
        String string = createCommandJSONString(DEALVERSION, jsonObject);
        Log.i(TAG, "upload: AppearDealVersion -> " + string);
        MQTTService.publish("$dp", string);
    }

    /**
     * @ 主动上报上线通知
     */
    public static void AppearDataUrl() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "3");
        jsonObject.put("ser", MQTTService.returnSer());
        String string = createCommandJSONString(DATAURL, jsonObject);
        Log.i(TAG, "upload: AppearDataUrl -> " + string);
        MQTTService.publish("$dp", string);
    }

    /**
     * @ 设备上报补货信息
     * */
    public static void MendChans(String chans){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "8");
        jsonObject.put("chans", chans);
        jsonObject.put("ser", MQTTService.returnSer());
        String str = createCommandJSONString(DATAMENDCHANS, jsonObject);
        Log.i(TAG, "upload: MendChans ->" + str);
//        logger.info("Activity upload_Data: -> " + str);
        MQTTService.publish("$dp", str);
    }

    /**
     * @ 设备上报出货信息
     * */
    public static void uploadShipMentData(String data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "2");
        jsonObject.put("nonce", getRandomString());
        jsonObject.put("order", MQTTService.returnOrder());
        jsonObject.put("ser", MQTTService.returnSer());
        jsonObject.put("chan", data.substring(12 + 6 * 2 + 1, 12 + 6* 2 + 2));   // 固定10位 + n * 2 + 1 || n * 2 + 2
        jsonObject.put("infresult", data.substring(12 + 6 * 2 + 3, 12 + (6 * 2 + 4)));           //8 位 + n * 2 + 3 || n * 2 + 4
        String str = createCommandJSONString(DATACHANINFRESULT, jsonObject);
        Log.d(TAG, "upload: -> uploadShipMentData" + str);
        MQTTService.publish("$dp", str);
    }

    /**
     * @ 设备上报清空货道信息
     * */
    public static void EmptyAisle(String data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "5");
        jsonObject.put("nonce", getRandomString());
        jsonObject.put("ser", MQTTService.returnSer());
        jsonObject.put("chanRes", data.substring(9, 10));   // 固定10位 + n * 2 + 1 || n * 2 + 2
        jsonObject.put("numRes", data.substring(11, 12));           //8 位 + n * 2 + 3 || n * 2 + 4
        String str = createCommandJSONString(DATACLEAR, jsonObject);
        Log.d(TAG, "upload: EmptyAisle -> " + str);
        MQTTService.publish("$dp", str);
    }

    /**
     * @ 设备上报灯光控制结果
     * */
    public static void LEDResult(String data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "12");
        jsonObject.put("ser", MQTTService.returnSer());
        jsonObject.put("ledRes", data);
        String str = createCommandJSONString(DATALIGHT, jsonObject);
        Log.d(TAG, "upload: LEDResult ->" + str);
        MQTTService.publish("$dp", str);
    }
}
