package mailUtil;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import logUtil.LogUtil;


/**
 * 一些邮件的模板
 * Created by ericwyn on 17-4-20.
 */
public class TxtUtil {

    //成功订阅时候提示邮件文本内容sub=Subscribe
    public static String successSubText(String bookName,String bookLink,String bookMail,String stratTime){

        String result="";
        result+="你好\n";
        result+="这封邮件是为了提醒您，您已成功通过S.H.I.E.L.D.美剧订阅系统订阅了相关美剧的提醒\n";
        result+="\n";
        result+="-------------------------------------------------------------------\n\n";
        result+="订阅名称："+bookName+"\n";
        result+="链接地址："+bookLink+"\n";
        result+="订阅邮箱："+bookMail+"\n";
        result+="起始日期："+stratTime+"\n\n";
        result+="-------------------------------------------------------------------\n\n";

        result+="注意，此封邮件由系统自动发送，请勿回复\n";
        result+="若有反馈信息可于 Github 提 issue"+"\n";
        result+="项目地址："+"https://github.com/Ericwyn/S.H.I.E.L.D"+"\n\n";

        result+="发送于"+ LogUtil.sdf.format(new Date());
        return result;
    }

    public static String successSubTitle(){
        return "您已成功订阅S.H.I.E.L.D.美剧更新提醒";
    }

    public static String haveNewTitle(){
        return "您订阅的美剧已有新剧集";
    }

    public static String haveNewText(String bookName,String newEName,String ed2k){
        String result="";
        result+="你好\n";
        result+="你订阅的美剧已有更新\n";
        result+="\n";
        result+="-------------------------------------------------------------------\n\n";
        result+="订阅名称："+bookName+"\n";
        result+="更新剧集："+newEName+"\n";
        result+="下载链接："+ed2k+"\n\n";
        result+="-------------------------------------------------------------------\n\n";

        result+="注意，此封邮件由系统自动发送，请勿回复\n";
        result+="若有反馈信息可于 Github 提 issue"+"\n";
        result+="项目地址："+"https://github.com/Ericwyn/S.H.I.E.L.D"+"\n\n";

        result+="发送于"+ LogUtil.sdf.format(new Date());
        return result;
    }

    public static String finishBookTitle(String bookName){return "您订阅的美剧"+bookName+"已完结";}

    public static String finishBookText(String bookName,String bookLink){
        String result="";
        result+="你好\n";
        result+="你订阅的美剧已完结\n";
        result+="\n";
        result+="-------------------------------------------------------------------\n\n";
        result+="订阅名称："+bookName+"\n";
        result+="订阅地址："+bookLink+"\n\n";
        result+="-------------------------------------------------------------------\n\n";

        result+="注意，此封邮件由系统自动发送，请勿回复\n";
        result+="若有反馈信息可于 Github 提 issue"+"\n";
        result+="项目地址："+"https://github.com/Ericwyn/S.H.I.E.L.D"+"\n\n";

        result+="发送于"+ LogUtil.sdf.format(new Date());
        return result;
    }

}
