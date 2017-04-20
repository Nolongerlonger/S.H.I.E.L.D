import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MailUtil.*;
import sun.rmi.runtime.Log;

/**
 * 主方法
 * Created by ericwyn on 17-4-19.
 */
public class Main {
    public static String NEW_LABEL="";
    public static int MaxThreadNum=0;
    public static TimerTask[] timerTasks;
    public static final Timer timer=new Timer();

    public static void main(String [] args){
        LogUtil.createConfig();     //创建配置文件
        LogUtil.createErrorLog();   //创建日志文件
        setMaxThreadNum();          //设置线程池参数


//        System.out.println("输入你要搜索的关键词");
//        Scanner sc=new Scanner(System.in);
//        String searchFlag=sc.nextLine();
//        ArrayList<HashMap<String,Object>> list=WebUtil.search(searchFlag);
//        for(int i=0;i<list.size();i++){
//            System.out.println(""+i+":"+list.get(i).get("title"));
////            System.out.println("\t"+list.get(i).get("href"));
//        }
//        System.out.println("输入编号继续查看");
//        int num=sc.nextInt();
//        try {
//            show1((String)list.get(num).get("href"));
//        }catch (Exception e){
//
//        }
//        System.out.println(TxtUtil.successSubText("神盾局","hah","ericwyn@qq.com","now"));
//        System.out.println(TxtUtil.haveNewText("神盾局","lalalal","ed2k"));

    }
    public static void setMaxThreadNum(){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("sysytem.config"));
            String[] flags=bufferedReader.readLine().split(" ");
            MaxThreadNum=Integer.parseInt(flags[1]);
        }catch (FileNotFoundException e){
            System.exit(-4);
        }catch (Exception e){
            LogUtil.writeAErrorLog("Main_setMaxThreadNum\t\t"+"无法读取线程池数量设置参数");
        }
    }

    /**
     * 开始所有线程任务，每隔一个小时所有线程自动更新一遍
     * 通过config构造GetNew线程（线程里依据userData里面的数据文件完善类自身的信息）
     *
     */
    public static void startRun(){
        ArrayList<HashMap<String,Object>> list=LogUtil.readAllUserConfig();
        timerTasks=new TimerTask[list.size()>MaxThreadNum?MaxThreadNum:list.size()];
        //对文件进行遍历，i小于MaxThreadNum或者list.size()两者中更小的那个，也就是最大无法超过MaxThreadNum
        for(int i=0;i<(list.size()>MaxThreadNum?MaxThreadNum:list.size());i++){
            TimerTask getNews=new GetNews((String)list.get(i).get("mail"),(String)list.get(i).get("linkUrl"));
            timerTasks[i]=getNews;
            timer.schedule(getNews,0L,GetNews.PERIOD_HOUR);//即刻开始线程，而后每隔一个小时这个线程自动运行一遍
        }
    }

    /**
     * 停止由定时器控制的所有线程
     */
    public static void stopRun(){
        timer.cancel();
    }


    //暂时美剧详情页面的具体剧集和最新一集
    public static void show1(String url) throws Exception{
        String result=WebUtil.sendGet(url);
//        System.out.println(result);
        System.out.println("------------------------------------------------------------");
//
        ArrayList<HashMap<String ,Object>> list=WebUtil.getEd2k(result);
        for (HashMap map:list){
            System.out.println((String)map.get("name")+"\t"+(String)map.get("ed2k"));
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("最新一集："+WebUtil.getNewE(result));
    }
}
