package com.example.yf_04.bluetoothtest.Utils;

/**
 * Created by YF-04 on 2017/7/26.
 */

public class Orders {

    /**
     * 生成指令
     * @param actionCode：指令id ，最大255
     * @return
     */
    public static String generateOrder(int actionCode) {
        String order = null;

        byte[] puchMsg = { 0x01, 0x10, 0x20, 0x00, 0x00, 0x07, 0x0E, 0x00,
                0x00, 0x00, (byte) 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, };


        puchMsg[10] = (byte) actionCode;

        int result=getCrc16(puchMsg);
        String hexString=Integer.toHexString(result).toUpperCase();

        String preString="";
        int length=hexString.length();
        if(length<4){
            int count=4-length;
            for(int i=0;i<count;i++){
                preString+="0";
            }
        }
        System.out.println("preString:"+preString);

        if(!preString.isEmpty()){
            hexString=preString+hexString;
        }

        System.out.println("result:"+result);
        System.out.println("hexString:"+hexString);

        String crcString=hexString.substring(2,hexString.length())+hexString.substring(0,2);
        System.out.println("crcString:"+crcString);

        order=byteArrayToHexStr(puchMsg)+crcString;

        return order;
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * modbus crc16 java 实现
     *
     * @param arr_buff
     * @return
     */
    public static int getCrc16(byte[] arr_buff) {
        int len = arr_buff.length;

        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else{
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
                }

            }
        }
        return crc;
    }

    /**
     *  1 Stand in situ
     */
    public static final String STAND_IN_SITU="01 10 20 00 00 0F 1E 00 00 00 01 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 06";
    /**
     *  2  Tread on the ground
     */
    public static final String TREAD_ON_THE_GROUND="01 10 20 00 00 0F 1E 00 00 00 02 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 C1";
    /**
     *  3 Walk forward
     */
    public static final String WALK_FORWARD="01 10 20 00 00 0F 1E 00 00 00 03 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 43";
    /**
     *  4 Walk backwards
     */
    public static final String WALK_BACKWARDS="01 10 20 00 00 0F 1E 00 00 00 04 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 0F";


    /**
     *  5 The side walk
     */
    public static final String THE_SIDE_WALK="01 10 20 00 00 0F 1E 00 00 00 05 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 8D";
    /**
     *  6 In situ squat down
     */
        public static final String IN_SITU_SQUAT_DOWN="01 10 20 00 00 0F 1E 00 00 00 06 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 4A";
    /**
     *  7 From squat down to stand
     */
    public static final String FROM_SQAT_DOWN_TO_STAND="01 10 20 00 00 0F 1E 00 00 00 07 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 C8";
    /**
     *  8 Place to sit down
     */
    public static final String PLACE_TO_SIT_DOWN="01 10 20 00 00 0F 1E 00 00 00 08 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 D3";


    /**
     *  9 From sitting to standing
     */
    public static final String FROM_SITTING_TO_STANDING="01 10 20 00 00 0F 1E 00 00 00 09 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 51";
    /**
     *  10 Place to lie down
     */
    public static final String PLACE_TO_LIE_DOWN="01 10 20 00 00 0F 1E 00 00 00 0A 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 96";
    /**
     *  11 From lie down to stand
     */
    public static final String FROM_LIE_DOWN_TO_STAND="01 10 20 00 00 0F 1E 00 00 00 0B 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 14";
    /**
     *  12 Put down
     */
    public static final String PUT_DOWN="01 10 20 00 00 0F 1E 00 00 00 0C 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 58";


    /**
     *  13 From the ground to the station
     */
    public static final String FROM_THE_GROUND_TO_THE_STATION="01 10 20 00 00 0F 1E 00 00 00 0D 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 DA";
    /**
     *  21 Bow one's head
     */
    public static final String BOW_ONES_HEAD="01 10 20 00 00 0F 1E 00 00 00 15 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 22";
    /**
     *  28 A word horse
     */
    public static final String A_WORD_HORSE="01 10 20 00 00 0F 1E 00 00 00 1C 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 F7";
    /**
     *  30 Stance
     */
    public static final String STANCE="01 10 20 00 00 0F 1E 00 00 00 1E 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 B2";


    /**
     *  31 Before the leg press
     */
    public static final String BEFORE_THE_LEG_PRESS="01 10 20 00 00 0F 1E 00 00 00 1F 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 30";
    /**
     *  32 Side leg press
     */
    public static final String SIDE_LEG_PRESS="01 10 20 00 00 0F 1E 00 00 00 20 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 9B";
    /**
     *  40 Chest out
     */
    public static final String CHEST_OUT="01 10 20 00 00 0F 1E 00 00 00 28 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 CC";
    /**
     * 41 Stoop
     */
    public static final String  STOOP="01 10 20 00 00 0F 1E 00 00 00 29 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 4E";


    /**
     *  53 Look up
     */
    public static final String LOOK_UP="01 10 20 00 00 0F 1E 00 00 00 35 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 3D";
    /**
     *  56  In situ turning
     */
    public static final String IN_SITU_TURNING="01 10 20 00 00 0F 1E 00 00 00 38 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 63";
    /**
     *  57 Take a right turn
     */
    public static final String TAKE_A_RIGHT_TURN="01 10 20 00 00 0F 1E 00 00 00 39 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 E1";
    /**
     *  58 Lie on your stomach and do push-ups
     */
    public static final String LIE_ON_YOU_STOMACH_AND_DO_PUSH_UPS="01 10 20 00 00 0F 1E 00 00 00 3A 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 26";


    /**
     *  62 Lift my left arm
     */
    public static final String LIFT_MY_LEFT_ARM="01 10 20 00 00 0F 1E 00 00 00 3E 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 AD";
    /**
     *  63 Lift my right arm
     */
    public static final String LIFT_MY_RIGHT_ARM="01 10 20 00 00 0F 1E 00 00 00 3F 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 2F";
    /**
     *  64 Waving your left arm
     */
    public static final String WAVING_YOU_LEFT_ARM="01 10 20 00 00 0F 1E 00 00 00 40 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 BB";
    /**
     *  65 Waving you right arm
     */
    public static final String WAVING_YOU_RIGHT_ARM="01 10 20 00 00 0F 1E 00 00 00 41 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B6 39";


    /**
     *  66 Stretch you left arm
     */
    public static final String STRETCH_YOU_LEFT_ARM="01 10 20 00 00 0F 1E 00 00 00 42 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B7 FE";
    /**
     *  67 Stretch you right arm
     */
    public static final String STRETCH_YOU_RIGHT_ARM="01 10 20 00 00 0F 1E 00 00 00 43 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 49 7C";
    /**
     * 121 Play basketball
     */
    public static final  String PLAY_BASKETBALL="01 10 20 00 00 0F 1E 00 00 00 79 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 DE";

    /**
     * 舞蹈：小苹果
     */
    public static final  String PLAY_APPLE="01 10 20 00 00 07 0E 00 00 00 7B 00 01 00 00 00 00 00 00 00 00 0C DD";

    /**
     * 舞蹈：我是一个机器人
     */
    public static final  String PLAY_ROBOT="01 10 20 00 00 07 0E 00 00 00 7C 00 01 00 00 00 00 00 00 00 00 16 A9";

    /**
     * 舞蹈：Bad Romance
     */
    public static final String PLAY_BAD_ROMANCE="01 10 20 00 00 07 0E 00 00 00 7D 00 01 00 00 00 00 00 00 00 00 12 55";
    /**
     * 舞蹈：Beat It
     */
    public static final String PLAY_BEAT_IT="01 10 20 00 00 07 0E 00 00 00 7E 00 01 00 00 00 00 00 00 00 00 1D 11";
    /**
     * 舞蹈：ponytail
     */
    public static final String PLAY_PONYTAIL="01 10 20 00 00 07 0E 00 00 00 7F 00 01 00 00 00 00 00 00 00 00 19 ED";
    /**
     * 舞蹈：ppap
     */
    public static final String PLAY_PPAP="01 10 20 00 00 07 0E 00 00 00 80 00 01 00 00 00 00 00 00 00 00 2A FA";
    /**
     * 舞蹈：zalababa
     */
    public static final String PLAY_ZALABABA="01 10 20 00 00 07 0E 00 00 00 81 00 01 00 00 00 00 00 00 00 00 2E 06";

    /**
     * 舞蹈 ：恭喜发财
     */
    public static final String PLAY_FELICITATE ="01 10 20 00 00 07 0E 00 00 00 82 00 01 00 00 00 00 00 00 00 00 21 42";


    /**
     * 舞蹈：生日歌
     */
    public static final String PLAY_BIRTHDAY_SONG="01 10 20 00 00 07 0E 00 00 00 83 00 01 00 00 00 00 00 00 00 00 25 BE";


    /**
     * 舞蹈：圣诞歌
     */
    public static final  String PLAY_CHRISTMAS="01 10 20 00 00 07 0E 00 00 00 84 00 01 00 00 00 00 00 00 00 00 3F CA ";

    /**
     * 打断动作
     */
    public static final String INTERRUPT_ACTION="01 10 20 00 00 03 06 00 00 00 01 40 5A AD 7B ";



}
