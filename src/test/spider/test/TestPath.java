package test.spider.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import test.spider.common.ExceptionLog;
import test.spider.common.Params;
import test.spider.dao.JDBCTemplate;
import test.spider.dao.SqlLiteJDBCUtils;
import test.spider.pojo.Brand;
import test.spider.pojo.Model;

public class TestPath {
    
    public static void main(String[] args) {
        
      /*String str = "/quanxinaodia4l/2016";
      
      int lastIndexOf = str.lastIndexOf("/");
      
      System.out.println(lastIndexOf);
      
      String year = str.substring(lastIndexOf+1, str.length());
      System.out.println(year);
      String baseurl = str.substring(0, lastIndexOf+1);
      System.out.println(baseurl);
      String oldUrl = baseurl+"peizhi/"+year;
      System.out.println(oldUrl);*/
        try {
            String imgUrl = "https://car2.autoimg.cn/cardfs/series/g26/M06/AE/B5/100x100_f40_autohomecar__wKgHEVs9u6GAPWN8AAAYsmBsCWs847.png";
            URL url = new URL(imgUrl);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
            byte[] buff = new byte[100];  
            int rc = 0;  
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {  
                swapStream.write(buff, 0, rc);  
            }  
            byte[] byteData = swapStream.toByteArray();
            Brand brand = new Brand();
            brand.setId(6);
            brand.setName("哈哈2");
            brand.setNum(0);
            brand.setUrl("---");
            brand.setImage(byteData);
            JDBCTemplate.saveBrand(brand, Params.AUTOHOME_BRAND);
            /*Integer id = SqlLiteJDBCUtils.saveBrand(brand);
            System.out.println("生成的id是："+id);*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
