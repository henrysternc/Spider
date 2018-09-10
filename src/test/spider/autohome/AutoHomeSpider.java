package test.spider.autohome;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import test.spider.common.ExceptionLog;
import test.spider.common.Letter;
import test.spider.common.Params;
import test.spider.dao.JDBCTemplate;
import test.spider.dao.SqlLiteJDBCUtils;
import test.spider.pojo.Brand;
import test.spider.pojo.Model;
import test.spider.pojo.Series;

public class AutoHomeSpider {
    
    
    
    public static void main(String[] args) {
        try {
            String rootUrl = "https://www.autohome.com.cn/car/";
            List<Brand> brands = getBrandsHref(rootUrl);
            
            List<Series> seriesByBrand = getSeriesByBrand(brands);
            
            List<Series> resultSeries = saveSeries(seriesByBrand);
            multiThreading(resultSeries,Params.MAX_THREADS );
            
            /****/
            
           /* String testUrl = "https://car.autohome.com.cn/config/series/3170.html#pvareaid=102189";
            listModel(testUrl,"奥迪","奥迪A3",1);*/
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e, "AutoHome");
        }
       
    }
    
    public static String multiThreading(List<Series> list, final int nThreads) throws Exception {  
        if (list == null || list.isEmpty()) {  
            return null;  
        }  
  
        int size = list.size();  
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);  
        List<Future<String>> futures = new ArrayList<Future<String>>(nThreads);  
          
        for (int i = 0; i < nThreads; i++) {  
            final List<Series> subList = list.subList(size / nThreads * i, size / nThreads * (i + 1));  
            Callable<String> task = new Callable<String>() {  
                @Override  
                public String call() throws Exception {  
                    for (Series series : subList) {  
                        String seriesUrl = series.getSeriesUrl();
                        if(!"".equals(seriesUrl)) {
                            listModel(seriesUrl, series.getBrandName(),series.getSeriesName(), series.getId());
                        }
                    }  
                    return "";  
                }  
            };  
            futures.add(executorService.submit(task));  
        }  
       
        executorService.shutdown();  
          
        return "success";  
    }  
    
    /**
     * 查询系列下所有型号的参数（型号名、长宽高）
     * @param paramUrl 参数页链接
     * @param brandName 品牌名
     * @param seriesName 系列名
     * @return
     * @throws Exception
     */
    public static List<Model> listModel(String paramUrl,String brandName,String seriesName,Integer seriesId) throws Exception{
        List<Model> models = new ArrayList<>();
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            List<String> nameList = new ArrayList<>();
            
            List<String> paramsList = new ArrayList<>();
            
            //打开的话，就是执行javaScript/Css
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(true);

            //获取页面
            HtmlPage page = webClient.getPage(paramUrl);
            
            //获取车型名称
           
            String pageXml = page.asXml();
            Jsoup.clean(pageXml, Whitelist.basic());
            /** jsoup解析文档 */
            // 把String转化成document格式
            Document doc = Jsoup.parse(pageXml);
            
            Elements urlelements = doc.select(".carbox div a");
            
            Elements elements = doc.getElementById("222").parent().parent().select("td div");
            
            for (Element element : elements) {
                String param = element.html();
               // System.out.println("型号参数：" + param);
                paramsList.add(param);
            }
            
            for (Element element : urlelements) {
                String tmpurl = "https:" + element.attr("href");
                
                //访问url获取 .information-tit h2标签的名称
                if(!"".equals(tmpurl)) {
                    Document document = Jsoup.connect(tmpurl).timeout(500000).get();
                    Elements select = document.select(".information-tit h2");
                    String typeName = select.get(0).html();
                    nameList.add(typeName);
                }
            }
            
            for(int i=0;i<nameList.size();i++) {
                Model model = new Model();
                model.setModelName(nameList.get(i));
                model.setVehicleParams(paramsList.get(i));
                model.setBrandName(brandName);
                model.setSeriesName(seriesName);
                model.setSeriesId(seriesId);
                //models.add(model);
               // boolean bool = JDBCTemplate.JDBC_insert(model);
                JDBCTemplate.saveModel(model, Params.AUTOHOME_MODEL, Params.AUTOHOME_MODEL_ID_SEQ);
                //SqlLiteJDBCUtils.saveModel(model);
                //JDBCTemplate.AutoHome_Insert(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e, "listModel");
        }finally {
            webClient.close();
        }
        return models;
    }
    
    public static List<Series> saveSeries(List<Series> list) {
        List<Series> result = new ArrayList<>();
        //往数据库里存
        Map<String, List<Series>> map = list.stream().collect(Collectors.groupingBy(Series::getSeriesName));
        for(Map.Entry<String,  List<Series>> entry : map.entrySet()) {
            String seriesName = entry.getKey();
            List<Series> tmpList = entry.getValue();
            Integer brandId = tmpList.get(0).getBrandId();
            Integer generateId = JDBCTemplate.getGenerateId(Params.AUTOHOME_SERIES_ID_SEQ);
            
            Series record = new Series();
            //
            record.setSeriesName(seriesName);
            record.setBrandId(brandId);
            record.setId(generateId);
            JDBCTemplate.saveSeries(record, Params.AUTOHOME_SERIES);
            //Integer generateId  = SqlLiteJDBCUtils.saveSeries(record);
            
            for(int i=0;i<tmpList.size();i++) {
                Series tmpseries = tmpList.get(i);
                tmpseries.setId(generateId);
                result.add(tmpseries);
            }
        }
        return result;
    }
    
    /**
     * 根据品牌查询系列集合
     * @param url 品牌系列链接
     * @param brandName 品牌名称
     * @return
     * @throws Exception
     */
    public static List<Series> getSeriesByBrand(List<Brand> brands) throws Exception{
        //https://car.autohome.com.cn/price/brand-33.html#pvareaid=2042362
        List<Series> list = new ArrayList<>();
          //String url = "https://car.autohome.com.cn/price/brand-33.html#pvareaid=2042362";
        for(int i=0;i<brands.size();i++) {
            String url = brands.get(i).getUrl();
            Integer brandId = brands.get(i).getId();
            String brandName = brands.get(i).getName();
            list = listSeries(url, list, brandName, brandId);
            String[] split = url.split("brand-");
            
            //System.out.println(split[0]);
            String[] split2 = split[1].split("\\.");
            String str = split[0]+"brand-";
            String id = split2[0];
            for(int j=2;j<5;j++) {
                //在售的url
                String url1 = str + id + "-0-0-" + j + ".html#pvareaid=2042362";
                list = listSeries(url1, list, brandName, brandId);
            }
            
            for(int j=2;j<4;j++) {
                //的url
                for(int k=1;k<5;k++) {
                    String url1 = str + id + "-0-" + j + "-" + k + ".html#pvareaid=2042362";
                    list = listSeries(url1, list, brandName, brandId);
                }
            }
        }
        
        return list;
    }
    
    /**
     * 根据系列链接查询所有系列集合
     * @param url
     * @param list
     * @param brandName
     * @return
     * @throws Exception
     */
    public static List<Series> listSeries(String url, List<Series> list, String brandName,Integer brandId) throws Exception{
        Document document = Jsoup.connect(url).timeout(500000).get();
        Elements elements = document.select("div .list-cont-bg .list-cont-main");
        
        for (Element element : elements) {
            String seriesName = element.select("div .font-bold").text();
            
            Element last = element.select(".main-lever .main-lever-right .main-lever-link a").last();
            
            String paramUrl = last.attr("abs:href");
            Series series = new Series();
            
            series.setSeriesName(seriesName);
            series.setSeriesUrl(paramUrl);
            series.setBrandName(brandName);
            series.setBrandId(brandId);
            list.add(series);
        }
       
        return list; 
    }
    
    /**
     * 查询品牌集合
     * @param rootUrl
     * @return
     */
    public static List<Brand> getBrandsHref(String rootUrl){
        List<Brand> result = new ArrayList<>();
        
        try {
            Letter[] values = Letter.values();
            String targetUrl = "";
            for (Letter letter : values) {
                if("A".equals(letter.name())) {
                    targetUrl = rootUrl;
                }else {
                    targetUrl = "https://www.autohome.com.cn/grade/carhtml/"+letter.name()+".html";
                }
                Document document = Jsoup.connect(targetUrl).timeout(500000).get();
                
                Elements elementA = document.select("dl dt div a");
                for (Element element1 : elementA) {
                    String attr = element1.attr("abs:href");
                    if(attr.indexOf("pic")<0) {//链接中带pic的品牌下面的车型没有参数
                        Brand brand = new Brand();
                        brand.setUrl(attr);
                        
                        String name = element1.text();
                        brand.setName(name);
                        
                       Integer generateId = JDBCTemplate.getGenerateId(Params.AUTOHOME_BRAND_ID_SEQ);
                        brand.setId(generateId);
                        brand.setNum(0);
                        
                        
                        
                        String imgUrl = element1.parent().parent().child(0).child(0).attr("abs:src");
                        
                        URL url = new URL(imgUrl);
                        InputStream inputStream = url.openStream();
                        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
                        byte[] buff = new byte[100];  
                        int rc = 0;  
                        while ((rc = inputStream.read(buff, 0, 100)) > 0) {  
                            swapStream.write(buff, 0, rc);  
                        }  
                        byte[] byteData = swapStream.toByteArray();
                        
                        brand.setImage(byteData);
                        //JDBCTemplate.saveBrand(brand, Params.AUTOHOME_BRAND);
                        Integer id = SqlLiteJDBCUtils.saveBrand(brand);
                        brand.setId(id);
                        result.add(brand);
                    }
                }
            }
            result = result.stream().distinct().collect(Collectors.toList());
            
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e, "getBrandsHref");
        }
        return result;
    }
   
}
