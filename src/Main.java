import java.util.ArrayList;
import java.util.HashMap;

/**
 * 主方法
 * Created by ericwyn on 17-4-19.
 */
public class Main {
    public static void main(String [] args){
        String url="http://cn163.net/archives/23794/";
        String result=WebUtil.sendGet(url);
//        String link=WebUtil.RegexString(result,"<span style=\"color: #808000;\">(.+?)</span><br />");
//        System.out.println(result);
//        System.out.println("------------------------------------------------------------");
//        System.out.println(link);

        ArrayList<HashMap<String ,Object>> list=WebUtil.getEd2k(result);
        for (HashMap map:list){
            System.out.println((String)map.get("name")+"\t"+(String)map.get("ed2k"));
        }

    }
}
