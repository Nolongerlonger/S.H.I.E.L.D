import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logUtil.LogUtil;
import mailUtil.MailUtil;
import mailUtil.TxtUtil;

/**
 * 封装一系列的爬虫方法
 * Created by ericwyn on 17-4-19.
 */
public class WebUtil {
    //自定义的最高请求次数，超过这个请求次数将会停止请求
    public static final int MAX_SEND_NUM=33;
    public static String sendGet(String url) throws FileNotFoundException{
        String result="";
        BufferedReader in=null;
        for(int i=0;i<=MAX_SEND_NUM;i++){
            if(i==MAX_SEND_NUM){
                System.out.println("请求次数超过30次，停止此次请求");
                LogUtil.writeAErrorLog("WebUtil_sendGet\t\t"+"请求"+url+"次数超限制");
                break;
            }
            try {
                URL realURL=new URL(url);
                URLConnection connection=realURL.openConnection();
                connection.setConnectTimeout(5000);
                //机智的伪装成linux下面的chrome，不要问了我最萌
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
//                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8 ");
//                connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
                connection.connect();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line=null;
                while ((line=in.readLine())!=null){
                    result += line+"\n";
                }

            }catch (SocketTimeoutException e){
                e.printStackTrace();
                System.out.println("请求超时，重试");
                continue;
            }catch (FileNotFoundException e){
                throw e;
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

    /**
     * 得到网页上面的ed2k，通过list<Map>返回结果
     * @param webResult 具体的美剧页面
     * @return  返回由《name-名字》《ed2k-具体ed2k链接》组成的Map，所组成的List
     */
    public static ArrayList<HashMap<String ,Object>> getEd2k(String webResult){
        ArrayList<HashMap<String ,Object>> list=new ArrayList<>();
        String[] labels=webResult.split("\n");
        for(int i=0;i<labels.length;i++){
            if(labels[i].contains("ed2k://|file|")){
                String ed2k="";
                String name="";
                HashMap<String,Object> map=new HashMap<>();
                map.put("ed2k",getStringByRegex(labels[i],"ed2k:\\/\\/\\|file\\|(.+?)\\|\\/"));
                map.put("name",
                        getStringByRegex(labels[i].replace(" ",""),"\\>(.+?)\\<\\/a\\>")
                                .replace("</a>","")
                                .replace(">",""));
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 得到最新一集的HashMap的name内容
     * @param url 具体的美剧页面的网址
     * @return  返回一个String，最新一集的name属性的字符串（和getEd2k方法）
     */
    public static HashMap<String,Object> getLastEName(String url) throws FileNotFoundException{
        String result=WebUtil.sendGet(url);
        ArrayList<HashMap<String ,Object>> list=WebUtil.getEd2k(result);
        return list.get(list.size()-1);
    }

    /**
     * 获取待更新的剧集，如果没有待更新的剧集那么返回null
     * @param webResult
     * @return
     */
    public static String getNewE(String webResult){
        String result="";
        String[] labels=webResult.split("\n");
        for(int i=0;i<labels.length-1;i++){
            if(labels[i].contains("ed2k://|file|")){
                if(labels[i+1].contains("<strong>")){
                    return "null";
                }
                if(labels[i+1].contains("ed2k://|file|" )){
                    continue;
                }else {
                    Main.NEW_LABEL=labels[i+1].replace(" ","").replace("<br/>","");
                    return labels[i+1].replace(" ","").replace("<br/>","");
                }
            }

        }
        return result;
    }

    /**
     * 人人美剧的搜索
     * @param searchFlag    搜索的关键词，多个关键词用空格隔开
     * @return  返回搜索结果，包含链接的名称，链接的地址
     */
    public static ArrayList<HashMap<String ,Object>> search(String searchFlag){
        ArrayList<HashMap<String ,Object>> list=new ArrayList<>();

        String[] searchFlags=searchFlag.split(" ");

        String searchParameter="";
        for (String str:searchFlags){
            searchParameter+=str+"+";
        }
        searchParameter=searchParameter.substring(0,searchParameter.length()-1);
        String[] urls=new String[10];       //默认获取10页的结果
        String result="";
        for(int i=1;i<11;i++){
            try {
                String url="http://cn163.net/page/"+i+"/?s="+searchParameter+"&x=0&y=0";
                result+=sendGet(url);
                System.out.println("获取页面数量"+i);
            }catch (FileNotFoundException e){
                break;
            }
        }
        String[] flags=result.split("\n");
        for (String str:flags){
            if(str.contains("rel=\"bookmark\"")
                    && !str.contains("<li>")
                    && !str.contains("<h2>")
                    && !str.contains("<span")){
                String href=getStringByRegex(str,"href=\"(.+?)\"");
                String title=getStringByRegex(str,"title=\"(.+?)\"");

                HashMap<String ,Object> map=new HashMap<>();

                map.put("title",title.replace("title=","").replace("\"",""));
                map.put("href",href.replace("href=\"","").replace("\"",""));

                list.add(map);
            }
        }
        return list;
    }

    /**
     * 通过url，订阅的剧集的名称
     * @param url
     * @return
     */
    public static String getBookName(String url){
        try {
            String result=WebUtil.sendGet(url);
//            String[] flags=result.split("\n");
//            for (String str:flags){
//                    String href=getStringByRegex(str,"href=\"(.+?)\"");
            String title=getStringByRegex(result,"class=\"entry_title\">(.+?)</")
                    .replace("class=\"entry_title\">","")
                    .replace("</","");
        //<h2 class="entry_title">神盾局特工第四季/全集Agents Of SHIELD迅雷下载</h2>

//            }
            if(!title.equals("")){
                return title;
            }else {
                return "未知标题剧集";
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
            LogUtil.writeAErrorLog("WebUtil_getBookName"+"\t\t"+"io错误，捕获异常");
        }
        return "S.H.I.E.L.D";
    }

    /**
     * 新增一条book记录，包括添加system.config的信息，以及新建userData里面的配置文件
     * @return 返回是否成功
     */
    public static boolean newABookConfig(String mail,String linkUrl,String title){
        try {

            //新建userData信息
            if(LogUtil.createdataPath("userData/"+mail+".log")){        //创建用户配置文件成功的话

                //添加system.config信息
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter("system.config",true));
                bufferedWriter.write(mail+" "+linkUrl);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();

                //新建用户配置信息
                BufferedWriter bufferedWriter1=new BufferedWriter(new FileWriter("userData/"+mail+".log"));
                String date=LogUtil.sdf.format(new Date());
                bufferedWriter1.write("startTime:"+date);
                bufferedWriter1.newLine();
                bufferedWriter1.write("linkUrl:"+linkUrl);
                bufferedWriter1.newLine();
                bufferedWriter1.write("mail:"+mail);
                bufferedWriter1.newLine();
                bufferedWriter1.write("mailNum:"+0);
                bufferedWriter1.newLine();
                bufferedWriter1.newLine();

                bufferedWriter1.flush();
                bufferedWriter1.close();

                //发送一份订阅邮件
                if(MailUtil.sendEmail(
                        TxtUtil.successSubTitle(),          //邮件的title
                        TxtUtil.successSubText(title,linkUrl,mail,date),     //邮件的详情
                        mail))                  //目标邮箱
                {
                    return true;
                }else {
                    //要删除此次操作的缓存
                    deleteABookConfig(mail,linkUrl);
                    return false;
                }
            }else {
                //这里没有error日志写入，因为createdataPath方法里面已经完成了日志写入了
                return false;
            }
        }catch (IOException ioe){
            LogUtil.writeAErrorLog("WebUtil_newABookConfig\t\t"+"添加System.config或者userData/[userMail].log信息时候发生了错误");
            ioe.printStackTrace();
        }
        return false;
    }

    /**
     * 删除一个用户的配置信息
     * @param mail  需要删除的用户的邮件配置信息,和System
     * @return  返回成功与否
     */
    public static boolean deleteABookConfig(String mail,String url){
        File file=new File("userData/"+mail+".log");
        if(!file.delete()){
            System.out.println("删除配置文件失败");
            LogUtil.writeAErrorLog("WebUtil_deleteABookConfig\t"+"删除配置文件"+"userData/"+mail+".log"+"失败");
            return false;
        }else {
            //读取全部
            ArrayList<HashMap<String,Object>> list=LogUtil.readAllUserConfig(true);
            //找到那一行,并且清除
            for (int i=0;i<list.size();i++){
                if(((String)list.get(i).get("mail")).equals(mail)
                        &&((String)list.get(i).get("linkUrl")).equals(mail)){
                    list.remove(i);
                    break;
                }
            }
            //重新写入全部
            try {
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter("system.config"));
                bufferedWriter.write(list.get(0).get("systemConfig")+" "+list.get(0).get("maxThreadNum"));
                if(list.size()>1){
                    for(int i=1;i<list.size();i++){
                        if(list.get(i)!=null){
                            bufferedWriter.write(list.get(i).get("mail")+" "+list.get(i).get("linkUrl"));
                        }
                    }
                }
                bufferedWriter.close();
                return true;
            }catch (Exception e){
                LogUtil.writeAErrorLog("WebUtil_deleteABookConfig"+"删除system.config条目"+mail+"失败");
                return false;
            }

        }
    }

    /**
     * 完结一个用户的订阅，用户的数据将会移动到finishData文件夹里面
     * @param mail
     * @param linkUrl
     */
    public static void finishABookConfig(String mail,String linkUrl){
        File file=new File("finishData/");
        if(!file.isDirectory()){
            file.mkdir();
        }
        File[] files=new File("UserData/").listFiles();
        for(File fileFlag:files){
            if(fileFlag.getName().contains(mail)){
                String[] readTxt=FileUtils.readTxt(fileFlag.getAbsolutePath()).split("\n");
                if(readTxt[2].equals(linkUrl)){
                    //复制用户日志文件
                    FileUtils.copyFile("UserData/"+fileFlag.getName(),"finishData/"+fileFlag.getName());
                    //删除文件的信息
                    WebUtil.deleteABookConfig(mail,linkUrl);
                    //重启订阅线程池
                    Main.restartRun();
                }
            }
        }


    }

    public static String getStringByRegex(String webResult,String classNme){
//        Pattern p=Pattern.compile("div=\""+classNme+"(.+?)");
        Pattern p=Pattern.compile(classNme);
        Matcher m=p.matcher(webResult);
        if(m.find()){
            return m.group();
        }else {
            return "";
        }
    }

    //自己封装一个class选择器，然而并没有用....
    public static ArrayList<String> chooseByClass(String webResult,String classNme){
        ArrayList<String> list=new ArrayList<>();
        String[] flags=webResult.split("\n");
        int startLine=0;
        int endLine=0;
        for(int i=0;i<flags.length;i++){
            if(flags[i].contains("<div class=\""+classNme+"\">")){
                //其中的一个标签结果
                String textFlag="";
//                startLine=i;

                int labelNum=0;
                for(int j=i;j<flags.length;j++){
                    if(flags[j].replace(" ","").contains("<div>")
                            && flags[j].replace(" ","").contains("</div>")){
                        textFlag+=flags[j]+"\n";
                        continue;
                    }else if(flags[j].replace(" ","").contains("<div>")){
                        labelNum++;
                        textFlag+=flags[j]+"\n";
                        continue;
                    }else if(flags[j].replace(" ","").contains("</div>")){
                        labelNum--;
                        if(labelNum>0){
                            textFlag+=flags[j]+"\n";
                            continue;
                        }else if(labelNum==0){
                            textFlag+=flags[j]+"\n";
                            i=j;
                            break;
                        }
                    }
                    if(!textFlag.equals("")){
                        list.add(textFlag);
                    }
                }
            }
        }
        return list;
    }

}
