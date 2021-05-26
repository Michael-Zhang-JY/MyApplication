package com.example.myapplication.SerialPort;

import android.util.Log;

import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MainService;

import java.util.ArrayList;
import java.util.Arrays;

public class SerialDataSend {
    public static final String TAG = SerialDataSend.class.getCanonicalName();

    private static int chansNum = 0;

    /**
     * @ 构建十六进制数据下发到下位机 （通道数量设定）
     */
    public static Byte [] byTesArray (int chansnum){
        chansNum = chansnum;
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x01);
        dataData.add((byte) chansNum);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        return dataArray;
    }


    /**
     * @ 构建十六进制数据下发到下位机 （询问从机门锁状态）
     */
    public static Byte [] byTesArray (){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        return dataArray;
    }

    /**
     * @ 读取SCM固件版本号
     * */
    public static Byte[] byTestScmVersions(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x10);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        return dataArray;
    }

    /**
     * 打包串口需要下发的出货数据
     * */
    public static Byte [] byShipMent (){
        MainService.orderCode ++;
        if (MainService.orderCode == 10){
            MainService.orderCode ++;
        }
        if (MainService.orderCode == 65535){
            MainService.orderCode = 0;
        }
        byte byteCode = (byte)(MainService.orderCode / 256);
        byte bytecodeONE = (byte)(MainService.orderCode % 256);
//        OrderCode = byteCode;
        ArrayList<Byte> shipMent = new ArrayList<>();
        shipMent.add((byte) 0xFF);
        shipMent.add((byte) 0x00);
        shipMent.add((byte) 0x02);
        shipMent.add((byte) ((byte) stringByte().length + 2));
        shipMent.add(byteCode);
        shipMent.add(bytecodeONE);
        for (int i = 0; i < stringByte().length; i++){
            shipMent.add(stringByte()[i]);          // 循环赋值后台下发的出货数据
        }
        Byte [] data = new Byte[shipMent.size()];
        shipMent.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        shipMent.add(danum[0]);
        shipMent.add(danum[1]);
        shipMent.add((byte) 0x0D);
        shipMent.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[shipMent.size()];
        shipMent.toArray(dataArray);
//        Log.i(TAG, "Shipment byShipMent: -> " + Arrays.toString(dataArray));
//        logger.info("Shipment byShipMent: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 通知Scm进入bootloader模式 进行固件更新
     * */
    public static Byte[] byTestScmBoot(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x11);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        Log.i(TAG, "Activity byTesArray: -> " + Arrays.toString(dataArray));
//        logger.info("Activity byTesArray: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 通知Scm从板固件发送完成
     * */
    public static Byte[] byTestScmOver(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x14);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        Log.i(TAG, "Activity byTesArray: -> " + Arrays.toString(dataArray));
//        logger.info("Activity byTesArray: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ SCM单片机更新分包添加头尾
     * */
    public static Byte[] byteAdd(byte[] data, int num){
        ArrayList<Byte> dataData = new ArrayList<>();
        byte byteNum = (byte)(num / 256);
        byte byteNumONE = (byte)(num % 256);
        byte byteCode = (byte)(data.length / 256);
        byte bytecodeONE = (byte)(data.length % 256);
        Byte[] datanum = new Byte[data.length + 11];
//        Log.d(TAG, "byteAdd: " + byteCode + " " + bytecodeONE);
        dataData.add((byte) 0x4B);
        dataData.add((byte) 0x54);
        dataData.add(byteNum);
        dataData.add(byteNumONE);
        dataData.add(byteCode);
        dataData.add(bytecodeONE);
        for (int i = 0; i < data.length; i++){
            dataData.add(data[i]);
        }
        Byte [] DATA = new Byte[dataData.size()];
        dataData.toArray(DATA);         // 复制数据给data
        Integer crc = MainService.getCRC(DATA);
        byte [] danum = MainService.intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x43);
        dataData.add((byte) 0x43);
        dataData.add((byte) 0x44);
        Byte [] dataArray =  new Byte[dataData.size()];
//        Log.d(TAG, "byteAdd: " + dataData.size());
        dataData.toArray(dataArray);
        for (int i = 0; i < dataArray.length; i++){
            datanum[i] = dataArray[i];
        }
        Log.d(TAG, "byteAdd: " + Arrays.toString(datanum));
        return datanum;
    }

    /**
     * 清空货道
     * */
    public static Byte [] ByteEmptyAisle(int chan, int num){
        ArrayList<Byte> shipMent = new ArrayList<>();
        shipMent.add((byte) 0xFF);
        shipMent.add((byte) 0x00);
        shipMent.add((byte) 0x09);
        shipMent.add((byte) 0x02);
        shipMent.add((byte) chan);
        shipMent.add((byte) num);
        Byte [] data = new Byte[shipMent.size()];
        shipMent.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        shipMent.add(danum[0]);
        shipMent.add(danum[1]);
        shipMent.add((byte) 0x0D);
        shipMent.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[shipMent.size()];
        shipMent.toArray(dataArray);
        Log.i(TAG, "Shipment byShipMent: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 下发货道没货通知
     * */
    public static Byte [] byTesArrayChans (int chans){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x0F);
        dataData.add((byte) 0x01);
        dataData.add((byte) chans);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        Log.i(TAG, "Activity byTesArray: -> " + Arrays.toString(dataArray));
//        logger.info("Activity byTesArray: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 控制从板重启
     * */
    public static Byte [] byTesArrayReboot (){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x12);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        Log.i(TAG, "Activity byTesArray: -> " + Arrays.toString(dataArray));
//        logger.info("Activity byTesArray: -> " + Arrays.toString(dataArray));
        return dataArray;
    }


    /**
     * @ 赋值后台下发的出货通道
     * */
    public static byte [] stringByte (){
        String dropchan = MQTTService.returnArrayDropchan();
//        Log.d(TAG, "stringByte: " + dropchan);
        String[] split = dropchan.split("");
        byte[] bytes = new byte[split.length - 1];
        for (int i = 1 ; i < split.length; i++) {
//            if (i == chansNum){
//                Log.d(TAG, "stringByte: " + chansNum);
//                bytes[i - 1] = (byte) 1;
//            }else {
            bytes[i - 1] = (byte) Integer.parseInt(split[i]);
//            }
        }
//        Log.d(TAG, "stringByte: " + Arrays.toString(bytes));
        return bytes;
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static Integer getCRC(Byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换，看情况使用（譬如这次规定校验码高位在前低位在后，也就不需要转换高低位)
        //CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
//        System.out.println(Integer.toHexString(CRC));
        return CRC;
    }

    /**
     * @ 转换int数据为byte （两位为一个元素）
     */
    public static byte[] intToDoubleBytes(Integer integer) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (integer / 256);
        bytes[1] = (byte) (integer % 256);
//        System.out.println(Arrays.toString(bytes));
        return bytes;
    }

    /**
     * @ 返回设定通道数
     * */
    public static int returnChansNum(){
        return chansNum;
    }
}
