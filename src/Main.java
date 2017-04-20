import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    public static void main(String [] args){
        LogUtil.createConfig();     //创建配置文件
        LogUtil.createErrorLog();   //创建日志文件
        setMaxThreadNum();          //设置线程池参数
        for(;;){
            System.out.println("运行模式：新增订阅模式");
            System.out.println("------------------------------------------------------------");
            System.out.println("1，搜索美剧和新增订阅");
            System.out.println("2，开启更新监听系统");
            System.out.println("3，关闭更新监听系统");
            System.out.println("4，切换为自动模式(需要手动重启)");
            System.out.println("5，退出系统");
            Scanner sc=new Scanner(System.in);
            int code=4;
            try {
                code=sc.nextInt();
            }catch (Exception e){
                System.out.println("输入错误，系统退出");
            }

            switch (code){
                case 1:
                    search();
                    break;
                case 2:
                    startRun();
                    break;
                case 3:
                    stopRun();
                    break;
                case 4:
                    break;
                case 5:
                    stopRun();
                    System.exit(0);
                    break;
            }
        }





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


    public static void search(){
        System.out.println("输入你要搜索的关键词");
        Scanner sc=new Scanner(System.in);
        String searchFlag=sc.nextLine();
        ArrayList<HashMap<String,Object>> list=WebUtil.search(searchFlag);
        for(int i=0;i<list.size();i++){
            System.out.println(""+i+":"+list.get(i).get("title"));
//            System.out.println("\t"+list.get(i).get("href"));
        }
        System.out.println("输入编号继续查看");
        int num=0;
        try {
            num=sc.nextInt();
        }catch (Exception e){
            System.out.println("输入错误");
            return;
        }

        try {
            show1((String)list.get(num).get("href"));
        }catch (Exception e){

        }

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

    }

    public static void setMaxThreadNum(){
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
    public static void startRun(){
        ArrayList<HashMap<String,Object>> list=LogUtil.readAllUserConfig();
        timerTasks=new TimerTask[list.size()>MaxThreadNum?MaxThreadNum:list.size()];
        //对文件进行遍历，i小于MaxThreadNum或者list.size()两者中更小的那个，也就是最大无法超过MaxThreadNum
        for(int i=0;i<(list.size()>MaxThreadNum?MaxThreadNum:list.size());i++){
            TimerTask getNews=new GetNews((String)list.get(i).get("mail"),(String)list.get(i).get("linkUrl"));
            timerTasks[i]=getNews;
            timer.schedule(getNews,0L,GetNews.PERIOD_HOUR);//即刻开始线程，而后每隔一个小时这个线程自动运行一遍
        }
        System.out.println("开启了"+(list.size()>MaxThreadNum?MaxThreadNum:list.size())+"线程");
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
