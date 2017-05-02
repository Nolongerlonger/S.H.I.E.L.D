import java.util.Date;
import java.util.TimerTask;

/**
 * 监听的线程类别
 * Created by ericwyn on 17-4-20.
 */
public class GetNews extends TimerTask  {
    //时间间隔，一个小时
    public static final long PERIOD_HOUR = 60 * 60 * 1000;
    public static final String dataPath="uesrData/";

    //开始的时间
    private Date startDate;

    //已存在的最新剧集
    private String theNewE="";

    //


    //线程用户信息
    private String mailAdress;
    private String url;

    private String threadName="";

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
     *
     * 已经存在
     *
     *
     */
    @Override
    public void run() {
//        this.startDate

    }
}
