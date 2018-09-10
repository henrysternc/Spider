package test.spider.bitauto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import test.spider.common.CommonUtils;
import test.spider.common.ExceptionLog;
import test.spider.common.Letter;
import test.spider.common.Params;
import test.spider.dao.JDBCTemplate;
import test.spider.pojo.Brand;
import test.spider.pojo.Model;
import test.spider.pojo.Series;

public class BitAutoSpider {
    
    public static void main(String[] args) {
        try {
            String rootUrl = "https://apicar.bitauto.com/CarInfo/getlefttreejson.ashx?tagtype=chexing";
            List<Brand> brandList = getBrandList(rootUrl);
            List<Series> newSeries = getNewSeriesByBrands(brandList);
            List<Series> result = getAllSeries(newSeries);
           
            multiThreading(result,Params.MAX_THREADS );
          
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e,"main");
        }
    }
    
    public static String multiThreading(List<Series> list, final int nThreads) throws Exception {  
        if (list == null || list.isEmpty()) {  
            return null;  
        }  
          
        //StringBuffer ret = new StringBuffer();  
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
                        Integer seriesId = series.getId();
                        if(!"".equals(seriesUrl)) {
                            getModels(seriesUrl, seriesId);
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
    
    public static void getModels(String paramUrl,Integer seriesId) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            List<String> nameList = new ArrayList<>();
            
            List<String> paramsList = new ArrayList<>();
            
            //打开的话，就是执行javaScript/Css
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            //获取页面
            HtmlPage page = webClient.getPage(paramUrl);
            
            //获取车型名称
            DomNodeList<DomNode> modelNames = page.querySelectorAll(".sel-car-move dl");
            for (DomNode domNode : modelNames) {
                //String modelName = domNode.getTextContent();
               // System.err.println("型号名称：" + modelName);
                DomNode firstChild = domNode.getFirstChild();
                String modelName = firstChild.getTextContent();
                nameList.add(modelName);
            }
            String pageXml = page.asXml();
            Jsoup.clean(pageXml, Whitelist.basic());
            /** jsoup解析文档 */
            // 把String转化成document格式
            Document doc = Jsoup.parse(pageXml);
            
            
            Elements elements = doc.getElementById("tr2,2,2_0,1,2").select("td");
            
            for (Element element : elements) {
                String param = element.html();
               // System.out.println("型号参数：" + param);
                paramsList.add(param);
            }
            
            for(int i=0;i<modelNames.size();i++) {
                Model model = new Model();
                //Integer modelId = JDBCTemplate.getGenerateId(Params.BITAUTO_MODEL_ID_SEQ);
                //model.setId(modelId);
                model.setModelName(nameList.get(i));
                model.setVehicleParams(paramsList.get(i));
                model.setSeriesId(seriesId);
                JDBCTemplate.saveModel(model, Params.BITAUTO_MODEL, Params.BITAUTO_MODEL_ID_SEQ);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e, "getModels");
        }finally {
            webClient.close();
        }
    }
    
    public static List<Series> getAllSeries(List<Series> list) {
        List<Series> result = new ArrayList<>();
        for(int i=0;i<list.size();i++) {
            try {
                result.add(list.get(i));
                String brandName = list.get(i).getBrandName();
                String seriesName = list.get(i).getSeriesName();
                String seriesUrl = list.get(i).getSeriesUrl();
                Integer id = list.get(i).getId();
                
                if(!CommonUtils.isNull(seriesUrl)) {
                    Document document = Jsoup.connect(seriesUrl).timeout(500000).get();
                    Elements elements = document.select(".middle-nav-box div .brand-info ul .offsale-years.drop-layer-box div a");
                    for (Element element : elements) {
                        String tmpurl = element.attr("abs:href");
                        String oldUrl = getOldUrl(tmpurl);
                        //System.out.println(oldUrl);
                        Series series = new Series();
                        series.setBrandName(brandName);
                        series.setSeriesName(seriesName);
                        series.setSeriesUrl(oldUrl);
                        series.setId(id);
                        result.add(series);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                ExceptionLog.recordException(e, "getAllSeries");
            }
        }
        
        return result;
    }
    
    
    
    /**
     * 根据品牌获取车辆系列（主要是获取配置参数url集合）
     * @param list
     * @return
     */
    public static List<Series> getNewSeriesByBrands(List<Brand> list) {
        List<Series> result = new ArrayList<>();
        for(int i=0;i<list.size();i++) {
            try {
                String url = list.get(i).getUrl();
                String brandName = list.get(i).getName();
                Integer brandId = list.get(i).getId();
                if(!CommonUtils.isNull(url)) {
                    url = Params.SERIES_ROOT_PATH + url;
                    Document document = Jsoup.connect(url).timeout(500000).get();
                    Elements elements = document.select("#divCsLevel_0 .row.block-4col-180 div div div a");
                    
                    
                    for (Element element : elements) {
                        String seriesUrl = element.attr("abs:href") + Params.PEI_ZHI;
                        String seriesName = element.attr("title");
                        
                        Series series = new Series();
                        Integer seriesId = JDBCTemplate.getGenerateId(Params.BITAUTO_SERIES_ID_SEQ);
                        series.setId(seriesId);
                        series.setBrandId(brandId);
                        series.setBrandName(brandName);
                        series.setSeriesName(seriesName);
                        series.setSeriesUrl(seriesUrl);
                        JDBCTemplate.saveSeries(series, Params.BITAUTO_SERIES);
                        result.add(series);
                    }
                   
                }
            } catch (IOException e) {
                e.printStackTrace();
                ExceptionLog.recordException(e, "getNewSeriesByBrands");
            }
            
        }
        
        //循环result 把ModelURL处理好
        return result;
    }
    
    /**
     * 获取易车网所有车辆品牌列表
     * @param rootUrl
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static List<Brand> getBrandList(String rootUrl) throws Exception {
        List<Brand> list = new ArrayList<>();
        // 创建一个httpclient对象

        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建一个GET对象

        HttpGet get = new HttpGet(rootUrl);

        // 执行请求
        CloseableHttpResponse response = httpClient.execute(get);

        // 取响应的结果
        int statusCode = response.getStatusLine().getStatusCode();

        System.out.println(statusCode);

        HttpEntity entity = response.getEntity();

        String string = EntityUtils.toString(entity, "utf-8");
        String substring = string.substring(14, string.length() - 1);
        Object parse = JSONObject.parse(substring);
        Map<String, Object> map = (Map<String, Object>) parse;
        // System.out.println(substring);
        Map<String, Object> obj = (Map<String, Object>) map.get("brand");
        Letter[] values = Letter.values();
        for (Letter letter : values) {
            Object objstr = obj.get(letter.name());
            if (!CommonUtils.isNull(objstr)) {
                List<Brand> tmplist = JSONObject.parseArray(objstr.toString(), Brand.class);
                for (Brand brand : tmplist) {
                    Integer brandId = JDBCTemplate.getGenerateId(Params.BITAUTO_BRAND_ID_SEQ);
                    brand.setId(brandId);
                    JDBCTemplate.saveBrand(brand, Params.BITAUTO_BRAND);
                    list.add(brand);
                }
            }
        }
       // System.out.println(list.size());
        // 关闭httpclient
        response.close();

        httpClient.close();
        return list;
    }

    
    public static String getOldUrl(String tmpUrl) {
        
        int lastIndexOf = tmpUrl.lastIndexOf("/");
        if(lastIndexOf<0) {
            return null;
        }
        String year = tmpUrl.substring(lastIndexOf+1, tmpUrl.length());
        String baseurl = tmpUrl.substring(0, lastIndexOf+1);
        String oldUrl = baseurl+"peizhi/"+year;
        return oldUrl;
    }
}
