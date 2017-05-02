package logUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 日志文件的操作类
 * Created by ericwyn on 17-4-20.
 */
public class LogUtil {

    public static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断用户日志文件是否已经存在
     * @param path  传入一个日志文件地址
     * @return  返回值，若为false则表示创建文件失败，ture则表示文件创建成功
     */
    public static boolean createdataPath(String path){
        File file=new File(path);
        if(file.isFile()){
            writeAErrorLog("LogUtil_createdataPath\t\t"+path+"文件已经存在");
            return false;   //文件已经存在，用户已经存在订阅
        }else {
            try {
                file.createNewFile();
            }catch (IOException ioe){
                writeAErrorLog("LogUtil_createdataPath\t\t"+path+"文件无法新建");
                return false;
            }
            return true;
        }
    }

    public static ArrayList<HashMap<String,Object>> readAllUserConfig(){
        return readAllUserConfig(false);
    }

    /**
     * 读取system.config来得到所有的用户的config,包括email和linkurl,以便新建线程
     * @param getSystemConfig  是否读取系统配置，也就是是否读取第一行，默认否
     * @return
     */
    public static ArrayList<HashMap<String,Object>> readAllUserConfig(boolean getSystemConfig){
        ArrayList<HashMap<String,Object>> list=new ArrayList<>();
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("system.config"));
            if(!getSystemConfig){        //如果不需要读取第一行
                bufferedReader.readLine();  //消耗第一行的无关信息
            }else {
                //读取第一行的信息，新建一个和其他不一样的hashmap来存储第一行的信息
                String line=bufferedReader.readLine();
                String[] flags=line.split(" ");
                HashMap<String ,Object> map=new HashMap<>();
                map.put("systemConfig",flags[0]);
                map.put("maxThreadNum",flags[1]);
                list.add(map);
            }
            String line=null;
            while ((line=bufferedReader.readLine())!=null){
                String[] flags=line.split(" ");
                HashMap<String ,Object> map=new HashMap<>();
                map.put("mail",flags[0]);
                map.put("linkUrl",flags[1]);
                list.add(map);
            }
        }catch (FileNotFoundException e1){
            writeAErrorLog("LogUtil_readAllUserConfig\t\t"+"找不到配置文件");
            System.exit(-4);
        }catch (IOException e2){
            writeAErrorLog("LogUtil_readAllUserConfig\t\t"+"发生了IO错误");
        }catch (Exception e3){
            writeAErrorLog("LogUtil_readAllUserConfig\t\t"+"奇奇怪怪的错误");
        }
        return list;
    }



    /**
     * 读取所有已经存在的用户的信息，制作成一个list，返回，方便系统多线程监听开始工作
     * @return  返回所有用户信息，每个map又包含startTime、linkUrl、mail、mailNum四个key
     */
    public static ArrayList<HashMap<String ,Object>> readAllUserLog(){
        ArrayList<HashMap<String ,Object>> list=new ArrayList<>();
        File[] files=new File("userData/").listFiles();
        for(int i=0;i<files.length;i++){
            HashMap<String,Object> map=readAUserLog("userData/"+files[i].getName());
            list.add(map);
        }
        return list;
    }

    /**
     * 读取一个订阅者的信息
     * @param logPath   订阅者的日志文件地址
     * @return  返回订阅者的信息，包含startTime、linkUrl、mail、mailNum四个key
     */
    public static HashMap<String ,Object> readAUserLog(String logPath){
        HashMap<String,Object> map=new HashMap<>();
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(logPath));
            //读取前面4行的信息，分别对应startTime、linkUrl、mail、mailNum
            String startTime=bufferedReader.readLine().replace("startTime:","");
            String linkUrl=bufferedReader.readLine().replace("linkUrl:","");
            String mail=bufferedReader.readLine().replace("mail:","");
            String mailNum=bufferedReader.readLine().replace("mailNum:","");

            map.put("startTime",startTime);
            map.put("linkUrl",linkUrl);
            map.put("mail",mail);
            map.put("mailNum",mailNum);

        }catch (IOException ioe){
            writeAErrorLog("LogUtil_readAUserLog\t\t读取"+logPath+"订阅者日志文件失败");
        }

        return map;
    }


    /**
     * 启动时对错误日志文件的检查，若是不存在错误日志，系统将无法启动
     */
    public static void createErrorLog(){
        File file=new File("userData");
        if(!file.isDirectory()){
            file.mkdir();
        }
        File log=new File("userData/error.log");
        if(!log.isFile()){
            try {
                log.createNewFile();
            }catch (IOException ioe){
                System.out.println("无法新建错误日志文件");
                System.exit(-1);
            }
        }
    }

    /**
     * 新增一行错误日志信息，自动在信息末尾加入时间
     * @param logMsg    错误日志文件信息
     */
    public static void writeAErrorLog(String logMsg){
        try {
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter("userData/error.log",true));
            bufferedWriter.write(sdf.format(new Date())+"\t\t"+logMsg);
            bufferedWriter.flush();
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (IOException e){
            System.out.println("错误文件日志无法写入");
            System.exit(-2);
        }
    }

    /**
     * 新建启动配置的文件
     */
    public static void createConfig(){
        File file=new File("system.config");
        if(!file.isFile()){
            try {
                file.createNewFile();
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("1 19");      //默认以手动模式开启系统
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
            }catch (IOException ioe){
                System.exit(-3);
            }
        }
    }



}
