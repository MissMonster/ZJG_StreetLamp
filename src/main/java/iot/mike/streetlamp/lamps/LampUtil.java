package iot.mike.streetlamp.lamps;

import iot.mike.streetlamp.sql.SQLManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LampUtil {
    private LampUtil() {}

    private static SQLManager      sqlManager      = SQLManager.getInstance();
    private static Logger          logger          = Logger.getLogger("Lamp");
    private static LampBaseManager lampBaseManager = LampBaseManager.getInstance();

    /**
     * 将命令中的ID去除
     * 
     * @param order
     *            命令
     * @return 去除之后的id
     */
    public static String replaceKeyID(String order) {
        int num = order.charAt(4) - '0';
        order = order.replaceAll(order.substring(4, 5 + num), "");
        return order;
    }

    /**
     * 得到命令中的对象
     * 
     * @param order
     *            命令
     * @return String keyID
     */
    public static String getKeyID(String order) {
        try {
            int num = order.charAt(4) - '0';
            String data = order.substring(5, 5 + num);
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将心跳写入文件
     * 
     * @param id
     *            路灯控制ID
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public static void writeHeartBeat(String id) throws IOException {
        File hearDIR = new File("ZJG_SL" + File.separator + "HeartBeat");
        if (!hearDIR.exists()) {
            hearDIR.mkdirs();
        }
        File lampDir = new File(hearDIR + File.separator +
                                new Date(System.currentTimeMillis()).toString());
        if (!lampDir.exists())
            lampDir.mkdirs();
        File lampFile = new File(lampDir + File.separator + id + ".log");
        if (!lampFile.exists())
            lampFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(
                                                   new FileWriter(lampFile, true));
        long time = System.currentTimeMillis();
        writer.write(id + ":" +
                     new Date(time).toLocaleString() + "\n");
        writer.flush();
        writer.close();
    }

    /**
     * 分类命令
     * 
     * @return ENUM
     */
    public static DealType classifyOrder(String order) {
        String key = order.substring(2, 4);
        if (key.equals("01")) { return DealType.LampHeartBeat; }
        if (key.equals("21") || key.equals("22")) { return DealType.LampStatus; }
        if (key.startsWith("1")) { return DealType.LampControl; }
        if (key.equals("88") || key.equals("99")) { return DealType.LampResponse; }
        return DealType.Wrong;
    }

    /**
     * 将所收到的数据进行写入文件
     * 
     * @param type
     *            文件类型
     * @param order
     *            指令
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public static void writeLog(DealType type, String order)
            throws IOException {
        String id = getKeyID(order);
        if (type == DealType.LampHeartBeat) {
            writeHeartBeat(id);
            return; // 如果是心跳文件就写入另外的文件
        }
        File logDIR = new File("ZJG_SL" + File.separator + "Log");
        if (!logDIR.exists()) {
            logDIR.mkdirs();
        }

        File lampDir = new File(logDIR + File.separator +
                                new Date(System.currentTimeMillis()).toString());
        if (!lampDir.exists())
            lampDir.mkdirs();
        File lampFile = new File(lampDir + File.separator + id + ".log");
        if (!lampFile.exists())
            lampFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new
                                                   FileWriter(lampFile, true));
        long time = System.currentTimeMillis();
        writer.write(classifyOrder(order) + ":" +
                     getKeyID(order) + ":" +
                     new Date(time).toLocaleString() + ":" +
                     order + "\n");
        writer.flush();
        writer.close();
    }

    /**
     * 写入错误日志
     * 
     * @param order
     *            命令
     * @param time
     *            时间
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public static void writeError(String order, long time, String error)
            throws IOException {
        File errorDIR = new File("ZJG_SL" + File.separator + "Error");
        if (!errorDIR.exists()) {
            errorDIR.mkdirs();
        }
        File lampDir = new File(errorDIR + File.separator +
                                new Date(System.currentTimeMillis()).toString());
        if (!lampDir.exists()) {
            lampDir.mkdirs();
        }
        File lampFile = new File(lampDir + File.separator +
                                 getKeyID(order));
        if (!lampFile.exists()) {
            lampFile.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(lampFile, true));
        writer.write("Error:" + getKeyID(order) +
                     new Date(time).toLocaleString() + ":" +
                     error + "\n");
        writer.flush();
        writer.close();
    }

    /**
     * 处理特定消息的方法
     * 
     * @param order
     *            命令
     * @param type
     *            方法种类
     */
    public static void dealOrder(String order, DealType type) {
        String key = getKeyID(order);
        switch (type) {
            case LampHeartBeat: {
                logger.log(Level.INFO, key + ":HeartBeat");
                try {
                    writeHeartBeat(key);
                    updateHeartBeat(key, System.currentTimeMillis());
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getLocalizedMessage());
                }
                break;
            }

            case LampStatus: {
                logger.log(Level.INFO, key + ":Status");
                lampBaseManager.getLampBase(key).writeBack(order); // 回写数据
                if (order.charAt(3) == '1') {// 状态汇报

                } else if (order.charAt(3) == '2') {// 灯泡故障
                    String[] lamps = getLamps(order);
                    for (String lamp : lamps) {
                        sqlManager.writeError2DB(key, lamp);
                    }
                } else {
                    // Do Nothing
                }
                try {
                    writeHeartBeat(key);
                    updateHeartBeat(key, System.currentTimeMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case LampResponse: {
                logger.log(Level.INFO, key + ":Response");
                // DO Nothing
                break;
            }

            default: {
                logger.log(Level.INFO, key + ":No Such Method!");
                try {
                    writeError(order,
                               System.currentTimeMillis(),
                               "No Such Method!");
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getLocalizedMessage());
                }
                break;
            }
        }
    }

    /**
     * 将来的消息进行回传
     * 
     * @param writer
     *            路灯的写出
     * @param order
     *            指令
     * @return 是否成功
     */
    public static String getNewsID(String order) {
        // System.out.println(order);
        String newsID =
                order.substring(order.length() - 5, order.length() - 3);
        // System.out.println(newsID);
        return newsID;
    }

    /**
     * 向数据库中更新心跳
     * 
     * @param id
     *            key值
     * @param time
     *            时间long
     * @return
     */
    public static boolean updateHeartBeat(String id, long time) {
        if (sqlManager.updateHeartBeat2DB(id, time)) {
            logger.log(Level.INFO, id + ":数据库心跳更新完成.");
            return true;
        } else {
            logger.log(Level.WARNING, id + ":数据库心跳跟新失败.");
            return false;
        }
    }

    /**
     * 将PHP服务器给的数据进行一步处理
     * 
     * @param order
     *            命令
     * @return String
     */
    public static String removeKey(String order) {
        String key = getKeyID(order);
        String name = order.substring(4, key.length() + 5);
        // System.out.println(name);
        order = order.replaceFirst(name, "");
        // System.out.println(order);
        return order;
    }

    /**
     * 处理灯泡故障
     * 
     * @param order
     *            消息
     * @return 消息标号 string[]
     */
    public static String[] getLamps(String order) {
        // System.out.println(order);
        String data = removeKey(order);
        // System.out.println(data);
        data = data.substring(5, data.length() - 6);
        // System.out.println(data);
        String LampsID[] = data.split(";");
        for (String string : LampsID) {
            // System.out.println(string);
        }
        return LampsID;
    }
}
