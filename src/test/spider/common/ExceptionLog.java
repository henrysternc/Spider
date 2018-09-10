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
                f.createNewFile();// �������򴴽�   
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,��׷��д��text�ı�
            output.write(functioName + " : Debug : "+new Date() + " Caused By : " +e.toString());
            output.write("\r\n");//����
            output.write("********��*********��**********��**********��*********��*********��********\r\n");
            output.write("\r\n");
            output.flush(); 
            output.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
