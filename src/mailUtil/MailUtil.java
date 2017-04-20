package mailUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import logUtil.*;

/**
 * 邮件发送工具类
 * Created by ericwyn on 17-4-20.
 */
public class MailUtil {

    public static void sendEmail(String title,String message,String toAddress){
        MailSenderInfo mailInfo = new MailSenderInfo();

        mailInfo.setMailServerHost("smtp.163.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        //认真信息
        mailInfo.setUserName(Pw.MAIL_ACCOUNT);
        mailInfo.setPassword(Pw.SMTP_PW);//您的邮箱密码
        //发送邮件地址
        mailInfo.setFromAddress(Pw.MAIL_ACCOUNT);    //邮件地址

        mailInfo.setToAddress(toAddress);   //收件人

        //设置标题，设置文本
        mailInfo.setSubject(title);
        mailInfo.setContent(message);

        //这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextMail(mailInfo);//发送文体格式
//        sms.sendHtmlMail(mailInfo);//发送html格式
        //更新用户的log数据
        chanceUserDataLog(toAddress,title,message);
    }

    //更新用户的log文件数据
    public static void chanceUserDataLog(String userMail,String mailTitle,String mailText){
        int mailNum=0;
        try {
            //全部读取
            BufferedReader bufferedReader=new BufferedReader(new FileReader("userData/"+userMail+".log"));
            ArrayList<String> list=new ArrayList<>();
            String line=null;
            while ((line=bufferedReader.readLine())!=null){
                list.add(line);
            }
            bufferedReader.close();
            for(int i=0;i<list.size();i++){
                if(list.get(i).contains("mailNum:")){
                    String[] flags=list.get(i).split(":");
                    mailNum=Integer.parseInt(flags[1])+1;
                    //更新邮件数量
                    list.set(i,"mailNum:"+mailNum);

                }
            }

            list.add("mail-"+mailNum);
            list.add("title:"+mailTitle);
            list.add("text:");
            list.add(mailText);
            list.add("\n");
            //全部写入
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter("userData/"+userMail+".log"));
            for (String str:list){
                bufferedWriter.write(str);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();


        }catch (IOException ioe){
            LogUtil.writeAErrorLog("MailUtil_chanceUserDataLog\t"+"无法在初始化时写入用户log文件");
        }catch (Exception e){
            LogUtil.writeAErrorLog("MailUtil_chanceUserDataLog\t"+"发生了奇奇怪怪的错误");
        }
    }
}
