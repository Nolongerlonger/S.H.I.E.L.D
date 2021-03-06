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

import logUtil.LogUtil;

/**
 * 主方法
 * Created by ericwyn on 17-4-19.
 */
public class Main {
    public static String NEW_LABEL="";
    public static int MaxThreadNum=0;
    public static TimerTask[] timerTasks;
    public static final Timer timer=new Timer();

    private static boolean systemRun=false;

    public static void main(String [] args){
        LogUtil.createConfig();     //创建配置文件
        LogUtil.createErrorLog();   //创建日志文件
        setMaxThreadNum();          //设置线程池参数
        if(getRunType()==0){
            startRun();
        }else {
            for(;;){
                System.out.println("运行模式：新增订阅模式");
                System.out.println("------------------------------------------------------------\n");
                System.out.println("1，搜索美剧和新增订阅");
                System.out.println("-------------------------");
                System.out.println("2，开启更新监听系统");
                System.out.println("3，关闭更新监听系统");
                System.out.println("4，重启更新监听系统");
                System.out.println("-------------------------");
                System.out.println("5，切换为自动模式(需要手动重启)");
                System.out.println("6，清除所有订阅数据（自动备份）");
                System.out.println("-------------------------");
                System.out.println("0，退出系统");
                Scanner sc=new Scanner(System.in);
                int code=4;
                try {
                    code=sc.nextInt();
                }catch (Exception e){
                    System.out.println("输入错误，系统退出");
                }

                switch (code){
                    //搜索，新增订阅数据
                    case 1:
                        search();
                        break;

                    //启动运行
                    case 2:
                        startRun();
                        break;

                    //停止运行
                    case 3:
                        stopRun();
                        break;

                    //重启监听
                    case 4:
//                    restartRun();
                        System.out.println("测试功能尚未开放");
                        break;

                    //切换为自动模式(需要手动重启)
                    case 5:
                        break;

                    //清除所有订阅数据
                    case 6:
                        deleteCache();
                        break;

                    //退出系统
                    case 0:
                        stopRun();
                        System.exit(0);
                        break;
                }
            }
        }


    }


    public static void search(){
        System.out.println("输入你要搜索的关键词，不输入内容退出搜索");
        Scanner sc=new Scanner(System.in);
        String searchFlag=sc.nextLine();
        if(searchFlag.equals("")){
            System.out.println("请输入搜索内容");
            return;
        }
        ArrayList<HashMap<String,Object>> list=WebUtil.search(searchFlag);
        for(int i=0;i<list.size();i++){
            System.out.println(""+i+":"+list.get(i).get("title"));
//            System.out.println("\t"+list.get(i).get("href"));
        }
        System.out.println("输入编号继续查看，输入其他任意内容退出搜索");
        int num=0;
        try {
            num=sc.nextInt();
        }catch (Exception e){
            System.out.println("输入错误");
            return;
        }
        //是否有新的剧集
        Boolean haveNewE=false;
        try {
            haveNewE=show1((String)list.get(num).get("href"));
        }catch (Exception e){

        }
        //存在新的剧集，可订阅
        if(haveNewE){
            System.out.println("是否订阅(y/n):");
            String code=sc.next();
            switch (code){
                case "y":
                    System.out.print("输入订阅邮箱:");
                    String email=sc.next();
                    if(!email.equals("")
                            && email.matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}")){
                        if(WebUtil.newABookConfig(email,
                                (String)list.get(num).get("href"),      //d订阅的
                                (String)list.get(num).get("title"))){

                            System.out.println("订阅成功");

                        }else {
                            System.out.println("订阅失败，请查看userData/error.log");
                        }
                    }else {
                        System.out.println("请输入正确的邮箱");
                    }

                    break;
                case "n":
                    break;
            }
        }else {
            System.out.println("剧集已完结，不可订阅");
        }
    }

    private static void setMaxThreadNum(){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("system.config"));
            String[] flags=bufferedReader.readLine().split(" ");
            MaxThreadNum=Integer.parseInt(flags[1]);
        }catch (FileNotFoundException e){
            LogUtil.writeAErrorLog("Main_setMaxThreadNum\t\t"+"没有找到system.config配置文件");
            e.printStackTrace();
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
    private static void startRun(){
        if(!systemRun){

            ArrayList<HashMap<String,Object>> list=LogUtil.readAllUserConfig();
            timerTasks=new TimerTask[list.size()>MaxThreadNum?MaxThreadNum:list.size()];
            //对文件进行遍历，i小于MaxThreadNum或者list.size()两者中更小的那个，也就是最大无法超过MaxThreadNum
            for(int i=0;i<(list.size()>MaxThreadNum?MaxThreadNum:list.size());i++){
                if(timerTasks[i]==null){
                    TimerTask getNews=new GetNews((String)list.get(i).get("mail"),(String)list.get(i).get("linkUrl"));
                    timerTasks[i]=getNews;
                }
                timer.schedule(timerTasks[i],0L,GetNews.PERIOD_HOUR);//即刻开始线程，而后每隔一个时间参数这个线程自动运行一遍
            }
            System.out.println("开启了"+(list.size()>MaxThreadNum?MaxThreadNum:list.size())+"线程");
            systemRun=true;
        }else {
            System.out.println("监听系统已开启");
        }

    }

    /**
     * 停止由定时器控制的所有线程
     */
    private static void stopRun(){
        if(systemRun){
            timer.cancel();
            systemRun=false;
            System.out.println("监听系统关闭成功");
        }else {
            System.out.println("监听系统已关闭");
        }
    }

    public static void restartRun(){
        stopRun();
        startRun();
    }
    //

    /**
     * 展示美剧详情页面的具体剧集和最新一集
     * @param url   美剧的链接
     * @return  是否有新剧集，没有的话返回false,有的话返回true
     * @throws Exception
     */
    private static boolean show1(String url) throws Exception{
        String result=WebUtil.sendGet(url);
//        System.out.println(result);
        System.out.println("------------------------------------------------------------");
//
        ArrayList<HashMap<String ,Object>> list=WebUtil.getEd2k(result);
        for (HashMap map:list){
            System.out.println((String)map.get("name")+"\t"+(String)map.get("ed2k"));
        }

        System.out.println("------------------------------------------------------------");
        String newE=WebUtil.getNewE(result);
        System.out.println("待更新剧集为："+newE);
        if(newE.equals("null")){
            return false;
        }
        return true;
    }

    /**
     * 删除所有的订阅数据、日志数据
     * @param backup    是否进行备份
     */
    private static void deleteCache(boolean backup){
        FileUtils.deleteDir("backup_dir");
        if(backup){
            FileUtils.moveDir("userData","backup_dir");
            FileUtils.moveFile("system.config","backup_dir/system.config");
            LogUtil.createConfig();     //创建配置文件
            LogUtil.createErrorLog();   //创建日志文件
        }else {
            FileUtils.deleteDir("userData");
            FileUtils.deleteFile("system.config");
            LogUtil.createConfig();     //创建配置文件
            LogUtil.createErrorLog();   //创建日志文件
        }
    }

    /**
     * 默认删除方法，会进行备份
     */
    private static void deleteCache(){deleteCache(true);}

    /**
     * 获取程序的运行模式，也就是system.config的第一行第一个参数
     * @return  返回运行参数，1代表手动开始模式，0代表自动运行模式,如果找不到文件就以手动开始模式运行
     */
    private static int getRunType(){
        int type=1;
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("system.config"));
            String line=null;
            if((line=bufferedReader.readLine())!=null){
                String tpyeFlag=line.substring(0,1);   //第一行第一个字符就是了
                try {
                    type=Integer.parseInt(tpyeFlag);
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtil.writeAErrorLog("Main_getRunType\t\t"+"转换运行模式代码从字符串到数字失败，以手动模式开始");
                }
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
            LogUtil.writeAErrorLog("Main_getRunType\t\t"+"获取程序运行模式失败，没有系统配置文件，以手动模式开始");
//            System.exit(-4);      //直接手动模式开始吧，手动模式会自动创建配置文件，修复这个错误
        }catch (IOException ioe){
            ioe.printStackTrace();
            LogUtil.writeAErrorLog("Main_getRunType\t\t"+"读取文件中的运行模式失败，以手动模式开始");
        }
        return type;
    }

}
