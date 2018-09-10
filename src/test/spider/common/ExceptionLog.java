package test.spider.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ExceptionLog {
    
    public static void recordException(Exception e,String functioName) {
        
        try {
            File dir = new File("D:\\Logs\\Spider");
            File f = new File("D:\\Logs\\Spider\\Exception.txt");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if (!f.exists()) { 
                f.createNewFile();// 不存在则创建   
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
            output.write(functioName + " : Debug : "+new Date() + " Caused By : " +e.toString());
            output.write("\r\n");//换行
            output.write("********华*********丽**********的**********分*********隔*********线********\r\n");
            output.write("\r\n");
            output.flush(); 
            output.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
