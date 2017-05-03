import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import mailUtil.MailUtil;
import mailUtil.TxtUtil;

/**
 * 监听的线程类别
 * Created by ericwyn on 17-4-20.
 */
public class GetNews extends TimerTask  {
    //时间间隔，一个小时
    public static final long PERIOD_HOUR = 60 * 1000 * 20;  //多少分钟轮询一次 * 60;
    public static final String dataPath="uesrData/";

    //开始的时间
    private Date startDate;

    //已存在的最新剧集
    private String theNewE="";

    //

    //线程用户信息
    private String mailAdress="";
    private String url="";

    private String threadName="";

    //用户日志的地址
    private String privateDataPath;

    /**
     * 线程开启订阅制度
     * @param email 传进来一个email
     * @param bookUrl   传进来一个订阅的网站地址
     */
    public GetNews(String email,String bookUrl){
        //设置线程参数
        startDate=new Date();
        threadName=email;

        //监听信息
        mailAdress=email;
        url=bookUrl;
        //用户数据和日志文件
        privateDataPath=dataPath+email+".log";
    }

    public String getName(){
        return threadName;
    }

    /**
     * 线程的运行函数
     *
     * 按照这种目的的话，只能监听系统启动后已经更新的剧集，而不能监听系统没有上线时候更新的剧集
     * 没有数据库的话确实比较难办，估计后面的改进应该加上一个小型数据库才行
     */
    @Override
    public void run() {
        //1，确认email和url

        System.out.println("线程:"+mailAdress+"\t开始监听"+url);
        String theLastENew="";
        try {
            //得到最新一集剧集的剧集的信息
            HashMap<String,Object> theNewEHashMap=WebUtil.getLastEName(url);
            theLastENew=(String)theNewEHashMap.get("name");
            if(theNewE.equals("")){
                theNewE=theLastENew;
                System.out.println("线程:"+mailAdress+"\t设定初始剧集："+theLastENew);
            }else {
                //最新剧集发生变化
                if(!theNewE.equals((String)theNewEHashMap.get("name"))){
                    System.out.println("线程:"+mailAdress+"\t获取新剧集成功"+theNewE);
                    theNewE=theLastENew;
                    String bookName=WebUtil.getBookName(url);
                    //发送邮件
                    MailUtil.sendEmail(
                            //新的标题
                            TxtUtil.haveNewTitle(),
                            //构建邮件的内容，需要获取剧集的名称，最新一集的集数，以及ed2k地址
                            TxtUtil.haveNewText(bookName,theNewE,(String)theNewEHashMap.get("ed2k")),
                            //发送邮件的地址
                            mailAdress);
                    //判断是否完结了
                    try {
                        if(WebUtil.getNewE(WebUtil.sendGet(url)).equals("null")){
                            System.out.println(mailAdress+"的一个订阅已经完结");
                            MailUtil.sendEmail(
                                    //新的标题
                                    TxtUtil.finishBookTitle(bookName),
                                    //构建邮件的内容，需要获取剧集的名称，最新一集的集数，以及ed2k地址
                                    TxtUtil.finishBookText(bookName,url),
                                    //发送邮件的地址
                                    mailAdress);
                            WebUtil.finishABookConfig(mailAdress,url);
                        }
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    //调用下载方法。。。这个大坑不知道怎么填了

                }else {
                    System.out.println("线程"+mailAdress+"没有新剧集更新");
                }
                //没有改变就等待下一次轮询
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
