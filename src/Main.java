import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import MailUtil.MailUtil;

/**
 * 主方法
 * Created by ericwyn on 17-4-19.
 */
public class Main {
    public static String NEW_LABEL="";

    public static void main(String [] args){
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
        MailUtil.sendEmail("测试邮件3","只有一封！！！","765339587@qq.com");

//        for(String str:list){
//            System.out.println(str+"\n\n\n");
//        }
    }

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
