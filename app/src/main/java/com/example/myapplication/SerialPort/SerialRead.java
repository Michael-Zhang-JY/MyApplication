package com.example.myapplication.SerialPort;

import android.util.Log;


import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MainDetailsPage;
import com.example.myapplication.MainService;

import java.io.IOException;

public class SerialRead extends Thread {
    private static final String TAG = SerialRead.class.getSimpleName();
    private StringBuffer buffer = new StringBuffer();

    private final String FunctionTwo = "A2";
    private final String FunctionFive = "A5";
    private final String FunctionNine = "A9";

    public static int groupCount = 0;
    public static int DataMistake = 0;

    /**
     * @ 串口读取线程
     * */
    @Override
    public void run (){
        super.run();
        while (MainService.isStart) {
            if (MainService.inPut == null) {
                return;
            }
            byte[] readData = new byte[512];
            try  {
                Thread.sleep(10);
                int size = MainService.inPut.read(readData);
                if (size > 0) {
                    String readString = DataUtils.ByteArrToHex(readData, 0, size);
                    if (readString.length() > 0){
                        buffer.append(readString);
                        Log.d(TAG, "read: bufferone ->" + buffer);
                        if (buffer.toString().startsWith("FF") && buffer.toString().endsWith("0D0A")){    // 判断首次出现与最后出现的字符串
                            Log.d(TAG, "read: buffer -> " + buffer);
                            String string =buffer.substring(0, buffer.length());
//                            Log.d(TAG, "run: " + string.substring(4, 6));
//                            Log.d(TAG, "run: " + (AnalysisChans(string.substring(7, 8)) + 15) +  " " + string.length());
                            if (string.substring(4, 6).equals("A1")){                       // 设定通道数量
                                MainActivity.upload_Data(string.substring(6, 8));
//                                Log.d(TAG, "run: " + string.substring(6, 8));
                            }else if (string.substring(4, 6).equals("A2")){
                                MainDetailsPage.uploadShipMentData(string);               // 上报每条出货数据到Onenet
                                MainDetailsPage.ALterRecyclerCall(string);                // 实时修改出货状态
                            }else if (string.substring(4, 6).equals("EE") && string.substring(6, 8).equals("E4")){
//                                MainInterShipment.EleRecyclerCall();
                            }
                            else if (string.substring(4, 6).equals("EE") && string.substring(6, 8).equals("E1")){
//                                MainInterShipment.EleRecyclerCall();
                            }else if (string.substring(4, 6).equals("A5")){
//                                MainInterShipment.AppearEle(string);
                            }else if (string.substring(4, 6).equals("04")){

                            }else if (string.substring(4, 6).equals("0C")){             // 设备从机主动上报门锁状态
                                MainActivity.BFB(string.substring(9, 10));
                            }else if (string.substring(4, 6).equals("AD")){             // 设备主机主动询问门锁状态
                                MainActivity.BFB(string.substring(9, 10));
                            }else if (string.substring(4, 6).equals("0E")){             // 设备主机上报从机补货数据
                                MainService.MendChans(string.substring(9, 10));
//                                MainService.DeviceIdInven(string.substring(9, 10));
                            }else if (string.substring(4, 6).equals("13")){             // 从板请求固件数据包
                                MQTTService.writeSCM(string.substring(8, 10));
                            }else if (string.substring(4, 6).equals("B0")){             // 设备主机询问从机固件版本号
                                MQTTService.AppVersion(string.substring(8, 12));
                            }

//                            Log.d(TAG, "run: " + string.substring(6, 8));
                            buffer = new StringBuffer();
                        } else if (buffer.length() > 36){                   // 黏包处理
                            String string = buffer.substring(0, buffer.length());
                            string = string.substring(string.indexOf("FF00"), string.indexOf("0D0A") + 4);
                            Log.d(TAG, "read: buffer ->: Two " + string);
                            MainDetailsPage.uploadShipMentData(string);               // 上报每条出货数据到Onenet
                            MainDetailsPage.ALterRecyclerCall(string);                // 实时修改出货状态
                            buffer = new StringBuffer();
                        }
                    }
                }
//                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int AnalysisChans(String data){
        int string = Integer.valueOf(data);
        switch (data){
            case "A":
                string = 10;
                break;
            case "B":
                string = 11;
                break;
            case "C":
                string = 12;
                break;
            case "D":
                string = 13;
                break;
            case "E":
                string = 14;
                break;
            case "F":
                string = 15;
                break;
        }
        return string;
    }

}
