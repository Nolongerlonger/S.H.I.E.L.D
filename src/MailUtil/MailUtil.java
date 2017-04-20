package MailUtil;

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
        mailInfo.setUserName(Pw.MAILACCOUNT);
        mailInfo.setPassword(Pw.SMTPPW);//您的邮箱密码
        //发送邮件地址
        mailInfo.setFromAddress(Pw.MAILACCOUNT);    //邮件地址

        mailInfo.setToAddress(toAddress);   //收件人

        //
        mailInfo.setSubject(title);
        mailInfo.setContent(message);

        //这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextMail(mailInfo);//发送文体格式
//        sms.sendHtmlMail(mailInfo);//发送html格式
    }
}
