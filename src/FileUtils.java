import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 封装文件的增删改，和文件、文件夹的复制，粘贴
 *
 * GitHub：https://github.com/Ericwyn/JavaUtil/blob/master/src/FileUtils.java
 * Created by Ericwyn on 17-5-2.
 */
public class FileUtils {

    /**
     * 创建文件
     * @param absolutePath  文件所在路径
     */
    public static void createFile(String absolutePath){
        try {
            File file=new File(absolutePath);
            file.createNewFile();
        }catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("创建文件"+absolutePath+"失败");
        }
    }

    /**
     * 创建文件夹
     * @param absolutePath  文件夹所在路径
     */
    public static void createDir(String absolutePath){
        try {
            File file=new File(absolutePath);
            file.mkdir();
        }catch (Exception ioe){
            ioe.printStackTrace();
            System.out.println("创建文件夹"+absolutePath+"失败");
        }
    }

    /**
     * 删除文件
     * @param filePath  文件路径
     */
    public static boolean deleteFile(String filePath){
        boolean success=(new File(filePath)).delete();
        if(!success){
            System.out.println("删除文件"+filePath+"失败");
            return false;
        }
        return true;
    }

    /**
     * 删除空目录
     * @param dirPath 将要删除的目录路径
     */
    public static void deleteEmptyDir(String dirPath) {
        boolean success = (new File(dirPath)).delete();
        if (success) {
//            System.out.println("Successfully deleted empty directory: " + dirPath);
//        } else {
            System.out.println("删除空目录" + dirPath +"失败");
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dirPath 将要删除的文件目录
     * @return 是否成功删除
     */
    public static boolean deleteDir(String dirPath) {
        File dir=new File(dirPath);
        if (dir.isDirectory()) {
            File[] files=dir.listFiles();
            for(File fileFlag:files){
                if(fileFlag.isDirectory()){
                    if(!deleteDir(fileFlag.getAbsolutePath())){
                        return false;
                    }
                }else {
                    if(!deleteFile(fileFlag.getAbsolutePath())){
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * 使用transferFrom方法对文件进行复制，
     * @param fileFromPath  文件来源路径
     * @param fileToPath    文件复制路径
     * @return  返回成功与否
     */
    public static boolean copyFile(String fileFromPath, String fileToPath){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(fileFromPath)).getChannel();
            outputChannel = new FileOutputStream(new File(fileToPath)).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            try {
                inputChannel.close();
                outputChannel.close();
                return true;
            }catch (IOException ioe){
                ioe.printStackTrace();
                System.out.println("关闭文件复制流"+fileFromPath+"失败");
                return false;
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("复制文件"+fileFromPath+"失败");
            return false;
        }
    }

    /**
     * 递归复制文件夹
     * @param dirFromPath 复制文件夹的路径
     * @param dirToPath   目标文件夹的路径
     * @return  返回成功与否
     */
    public static boolean copyDir(String dirFromPath, String dirToPath) {
        boolean result = false;
        try {
            (new File(dirToPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(dirFromPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (dirFromPath.endsWith(File.separator)) {
                    temp = new File(dirFromPath + file[i]);
                } else {
                    temp = new File(dirFromPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(dirToPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyDir(dirFromPath + "/" + file[i], dirToPath + "/" + file[i]);
                }
            }
            if(new File(dirToPath).exists()){
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean moveFile(String fileFromPath,String fileToPath){
        if(copyFile(fileFromPath,fileToPath)){
            if(deleteFile(fileFromPath)){
                return true;
            }else {
                System.out.println("删除文件"+fileFromPath+"失败");
                deleteFile(fileToPath);
                return false;
            }

        }else {
            System.out.println("移动文件"+fileFromPath+"失败");
            return false;
        }
    }

    public static boolean moveDir(String dirFromPath,String dirToPath){
        if(copyDir(dirFromPath,dirToPath)){
            if(deleteDir(dirFromPath)){
                return true;
            }else {
                System.out.println("删除文件夹"+dirFromPath+"失败");
                deleteDir(dirToPath);
                return false;
            }
        }
        System.out.println("移动文件夹"+dirFromPath+"失败");
        return false;
    }

}

