package mailUtil;

import javax.mail.*;

/**
 * 邮件信息 封装类
 * Created by ericwyn on 17-4-20.
 */
public class MyAuthenticator extends Authenticator{
    String userName=null;
    String password=null;

    public MyAuthenticator(){
    }
    public MyAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }
}
