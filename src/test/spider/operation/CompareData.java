package test.spider.operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import test.spider.common.Params;
import test.spider.dao.JDBCTemplate;
import test.spider.pojo.Brand;
import test.spider.pojo.Model;
import test.spider.pojo.Series;

public class CompareData {
    
    public static void main(String[] args) {
        compareBrand();
        
        //compareAllModel();
        
        //compareSeries(1, 1, "奥迪");
        
    }
    
    public static void compareBrand() {
      //先分别查询出二者的品牌list 
        List<Brand> autohomeBrands = JDBCTemplate.brandList(Params.AUTOHOME_BRAND);
        
        List<Brand> bitautoBrands = JDBCTemplate.brandList(Params.BITAUTO_BRAND);
        
        Map<String,Integer[]> map = new HashMap<>();
        for(int i=0;i<bitautoBrands.size();i++) {
            Integer b_brandId = bitautoBrands.get(i).getId();
            String b_brandName = bitautoBrands.get(i).getName();
            for(int j=0;j<autohomeBrands.size();j++) {
                String a_brandName = autohomeBrands.get(j).getName();
                if(a_brandName.equals(b_brandName)) {
                    Integer a_brandId = autohomeBrands.get(j).getId();
                    //查询系列与型号，并进行对比
                    Integer[] arr = {b_brandId,a_brandId};
                    System.out.println(a_brandName + " : id是： " + b_brandId + " / " + a_brandId);
                    map.put(a_brandName, arr);
                }
            }
        }
        
        for(Map.Entry<String,  Integer[]> entry : map.entrySet()) {
            String brandName = entry.getKey();
            Integer[] ids = entry.getValue();
            Integer b_brandId = ids[0];
            Integer a_brandId = ids[1];
            compareSeries(b_brandId, a_brandId, brandName);
        }
    }
    
    public static void compareSeries(Integer b_brandId, Integer a_brandId, String brandName) {
        //先分别查询出两个数据库中该品牌的系列集合
        List<Series> b_seriesList = JDBCTemplate.listByBrandId(Params.BITAUTO_SERIES, b_brandId);
        
        List<Series> a_seriesList = JDBCTemplate.listByBrandId(Params.AUTOHOME_SERIES, a_brandId);
        
        Map<String,Integer[]> map = new HashMap<>();
        for(int i=0;i<b_seriesList.size();i++) {
            String b_seriesName = b_seriesList.get(i).getSeriesName();
            Integer b_seriesId = b_seriesList.get(i).getId();
            
            for(int j=0;j<a_seriesList.size();j++) {
                String a_seriesName = a_seriesList.get(j).getSeriesName();
                Integer a_seriesId = a_seriesList.get(j).getId();
                if(a_seriesName.equals(b_seriesName)) {
                    Integer[] arr = {b_seriesId,a_seriesId};
                    map.put(brandName+" "+a_seriesName, arr);
                }
            }
        }
        System.err.println("品牌："+brandName+" 下属的系列共有  "+map.size()+" 个对得上！");
        for(Map.Entry<String,  Integer[]> entry : map.entrySet()) {
            String seriesName = entry.getKey();
            Integer[] ids = entry.getValue();
            Integer b_seriesId = ids[0];
            Integer a_seriesId = ids[1];
            
            compareModel(b_seriesId, a_seriesId, seriesName);
            
            //printKeywords(b_seriesId, a_seriesId, seriesName);
        }
        
    }
    
    
    public static void compareModel(Integer b_seriesId, Integer a_seriesId, String seriesName) {
        
        List<Model> b_modelList = JDBCTemplate.listBySeriesId(Params.BITAUTO_MODEL, b_seriesId);
        
        List<Model> a_modelList = JDBCTemplate.listBySeriesId(Params.AUTOHOME_MODEL, a_seriesId);
        
        for(int i=0;i<b_modelList.size();i++) {
            String b_modelName = b_modelList.get(i).getModelName();
            String b_param = b_modelList.get(i).getVehicleParams();
            
            for(int j=0;j<a_modelList.size();j++) {
                String a_modelName = a_modelList.get(j).getModelName();
                String a_param = a_modelList.get(j).getVehicleParams();
                if(a_modelName.equals(b_modelName)) {
                    //型号相同，比较参数
                    //参数有值
                    if(!a_param.contains("_")) {
                       if(a_param.contains("*")) {
                           a_param = a_param.replace("*", "x");
                       }
                       if(a_param.equals(b_param)) {
                           print_TRUE(seriesName, a_modelName, b_param, a_param);
                       }else {
                           print_FALSE(seriesName, a_modelName, b_param, a_param);
                       }
                      
                    }else {
                        System.out.println("汽车之家参数无数据");
                    }
                }else {
                    String new_a_modelName = "";
                    if(a_modelName.contains("年型")) {
                        new_a_modelName = a_modelName.replace("年型", "纪念版");
                    }
                    
                    if(a_modelName.contains("型")) {
                        new_a_modelName = a_modelName.replace("型", "版");
                    }
                    if(new_a_modelName.equals(b_modelName)) {
                      //型号相同，比较参数
                        //参数有值
                        if(!a_param.contains("_")) {
                           if(a_param.contains("*")) {
                               a_param = a_param.replace("*", "x");
                           }
                           if(a_param.equals(b_param)) {
                               print_TRUE(seriesName, a_modelName, b_param, a_param);
                           }else {
                               print_FALSE(seriesName, a_modelName, b_param, a_param);
                           }
                          
                        }else {
                            System.out.println("汽车之家参数无数据");
                        }
                    }
                }
                /*if(a_modelName.equals(b_modelName)) {
                    //型号相同，比较参数
                    if(!b_param.contains("_")) {
                        //参数有值
                        if(!a_param.contains("_")) {
                           if(a_param.contains("*")) {
                               a_param = a_param.replace("*", "x");
                           }
                           //System.out.println("-------------华----------丽----------分---------隔---------线---------------");
                           if(a_param.equals(b_param)) {
                              // System.err.println(seriesName + "/"+ a_modelName + " 结果 :true");
                               print_TRUE(seriesName, a_modelName, b_param, a_param);
                           }else {
                               //System.out.println(seriesName + "/"+ a_modelName + " 结果 :false");
                               print_TRUE(seriesName, a_modelName, b_param, a_param);
                           }
                          // System.out.println("具体参数："+b_param + "-"+a_param);
                           //System.out.println("-------------华----------丽----------分---------隔---------线---------------");
                        }else {
                            System.out.println("汽车之家参数无数据");
                        }
                    }else {
                        System.out.println("易车参数无数据");
                    }
                }*/
            }
        }
    }
    
   
    public static void printKeywords(Integer b_seriesId, Integer a_seriesId, String seriesName) {
        List<Model> b_modelList = JDBCTemplate.listBySeriesId(Params.BITAUTO_MODEL, b_seriesId);
        
        List<Model> a_modelList = JDBCTemplate.listBySeriesId(Params.AUTOHOME_MODEL, a_seriesId);
        
        printKeyword(b_modelList, seriesName);
        
        printKeyword(a_modelList, seriesName);
    }
    
    public static void printKeyword(List<Model> list, String seriesName) {
        for(int i=0;i<list.size();i++) {
            String modelName = list.get(i).getModelName();
            
            String tmp = seriesName + " " + modelName;
            String modelKeyword = getModelKeyword(tmp);
            writeModelKeyword(modelKeyword,seriesName);
        }
    }
    
    public static String getModelKeyword(String tmp) {
        Segment segment = HanLP.newSegment();
        List<Term> seg = segment.seg(tmp);
        StringBuilder sb = new StringBuilder();
        for (Term term : seg) {
            if(!"".equals(term.word)) {
                sb.append(term.word+"|");
            }
        }
        return sb.toString();
    }
    
    public static void writeModelKeyword(String modelKeyword, String seriesName) {
        try {
            File dir = new File("D:\\Logs\\Spider");
            File f = new File("D:\\Logs\\Spider\\"+seriesName+".txt");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if (!f.exists()) { 
                f.createNewFile();// 不存在则创建   
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
            output.write(modelKeyword);
            output.write("\r\n");//换行
            output.flush(); 
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void compareAllModel() {
        
        List<Model> b_modelList = JDBCTemplate.listAll(Params.BITAUTO_MODEL);
        
        List<Model> a_modelList = JDBCTemplate.listAll(Params.AUTOHOME_MODEL);
        
        for(int i=0;i<b_modelList.size();i++) {
            String b_modelName = b_modelList.get(i).getModelName();
            String b_param = b_modelList.get(i).getVehicleParams();
            if(!b_param.contains("_")) {
                for(int j=0;j<a_modelList.size();j++) {
                    String a_modelName = a_modelList.get(j).getModelName();
                    String a_param = a_modelList.get(j).getVehicleParams();
                    
                    if(a_modelName.equals(b_modelName)) {
                        //型号相同，比较参数
                        //参数有值
                        if(!a_param.contains("_")) {
                           if(a_param.contains("*")) {
                               a_param = a_param.replace("*", "x");
                           }
                           if(a_param.equals(b_param)) {
                               print_TRUE(null, a_modelName, b_param, a_param);
                           }else {
                               print_FALSE(null, a_modelName, b_param, a_param);
                           }
                          
                        }else {
                            System.out.println("汽车之家参数无数据");
                        }
                    }else {
                        String new_a_modelName = "";
                        if(a_modelName.contains("年型")) {
                            new_a_modelName = a_modelName.replace("年型", "版");
                        }
                        
                        if(a_modelName.contains("型")) {
                            new_a_modelName = a_modelName.replace("型", "版");
                        }
                        if(new_a_modelName.equals(b_modelName)) {
                          //型号相同，比较参数
                            //参数有值
                            if(!a_param.contains("_")) {
                               if(a_param.contains("*")) {
                                   a_param = a_param.replace("*", "x");
                               }
                               if(a_param.equals(b_param)) {
                                   print_TRUE(null, a_modelName, b_param, a_param);
                               }else {
                                   print_FALSE(null, a_modelName, b_param, a_param);
                               }
                              
                            }else {
                                System.out.println("汽车之家参数无数据");
                            }
                        }
                    }
                }
            }else {
                System.out.println("易车参数无数据");
            }
        }
    }
    
    public static void print_TRUE(String seriesName, String modelName, String param1,String param2) {
        try {
            File dir = new File("D:\\Logs\\Spider");
            File f = new File("D:\\Logs\\Spider\\ture_record.txt");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if (!f.exists()) { 
                f.createNewFile();// 不存在则创建   
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
            output.write(seriesName + " : 型号 : "+ modelName + " 参数 1: " +param1 + "参数2:"+param2);
            output.write("\r\n");//换行
            output.flush(); 
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void print_FALSE(String seriesName, String modelName, String param1,String param2) {
        try {
            File dir = new File("D:\\Logs\\Spider");
            File f = new File("D:\\Logs\\Spider\\false_record.txt");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if (!f.exists()) { 
                f.createNewFile();// 不存在则创建   
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
            output.write(seriesName + " : 型号 : "+ modelName + " 参数 1: " +param1 + "参数2:"+param2);
            output.write("\r\n");//换行
            output.flush(); 
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
