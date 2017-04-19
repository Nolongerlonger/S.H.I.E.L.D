import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装一系列的爬虫方法
 * Created by ericwyn on 17-4-19.
 */
public class WebUtil {

    public static String sendGet(String url){
        String result="";
        BufferedReader in=null;
        for(;;){
            try {
                URL realURL=new URL(url);
                URLConnection connection=realURL.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
//                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8 ");
//                connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
                connection.connect();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line=null;
                while ((line=in.readLine())!=null){
                    result+=line+"\n";
                }

            }catch (MalformedURLException e){
                System.out.println("构建URL时候发生异常");
                e.printStackTrace();
                //直接休息5秒，然后再爬
                try{
                    Thread.sleep(5000);
                }catch(Exception any){

                }
                continue;
            }catch (IOException ioe){
                System.out.println("发生IO异常");
                ioe.printStackTrace();
                try{
                    Thread.sleep(5000);
                }catch(Exception any){

                }
                continue;
            }
            try {
                if(in!=null){
                    in.close();
                }
            }catch (IOException e){
                System.out.println("关闭IO流时候发生了异常");
                e.printStackTrace();
            }
            break;
        }

        return result;
    }

    public static ArrayList<HashMap<String ,Object>> getEd2k(String webResult){
        ArrayList<HashMap<String ,Object>> list=new ArrayList<>();
        String[] labels=webResult.split("\n");
        for(int i=0;i<labels.length;i++){
            if(labels[i].contains("ed2k://|file|")){
                String[] flags=labels[i].split("\"");
                String ed2k="";
                String name="";
                for(int j=0;j<flags.length;j++){
                    if(flags[j].contains("ed2k://|file|")){
                        ed2k=flags[j];
                        name=flags[j+1]
                                .replace("</a>","")
                                .replace("<br/>","")
                                .replace(">","")
                                .replace("<","");
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("ed2k",ed2k);
                        map.put("name",name);
                        list.add(map);
                        break;
                    }
                }

            }
        }
        return list;
    }

    public static String RegexString(String targetStr, String patternStr)
    {
        // 定义一个样式模板，此中使用正则表达式，括号中是要抓的内容 // 相当于埋好了陷阱匹配的地方就会掉下去
        Pattern pattern = Pattern.compile(patternStr);
        // 定义一个matcher用来做匹配

        Matcher matcher = pattern.matcher(targetStr);
        // 如果找到了
        if (matcher.find())
        {
            // 打印出结果
            return matcher.group(1);
        }
        return "";
    }


}
