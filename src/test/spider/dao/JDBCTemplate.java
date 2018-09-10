package test.spider.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.util.Base64;

import test.spider.common.CommonUtils;
import test.spider.common.ExceptionLog;
import test.spider.pojo.Brand;
import test.spider.pojo.Model;
import test.spider.pojo.Series;

public class JDBCTemplate {
    static String url = "jdbc:postgresql://127.0.0.1:5432/testdb";

    static String usr = "postgres";

    static String psd = "rtzt12345678";
    
    
    
    /**
     * 获取连接方法 （连接池使用）
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, usr, psd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return conn;
    }
    
    /**
     * 查询品牌集合
     * @param tableName
     * @param brandId
     * @return
     */
    public static List<Brand> brandList(String tableName){
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        List<Brand> list = new ArrayList<>();
        try {
            String sql = "select * from "+ tableName;
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                Brand brand = new Brand();
                brand.setId(rs.getInt("id"));
                brand.setName(rs.getString("name"));
                list.add(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
        
        return list;
    }
    
    /**
     * 查询指定品牌下系列的集合
     * @param tableName
     * @param brandId
     * @return
     */
    public static List<Series> listByBrandId(String tableName, Integer brandId){
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        List<Series> list = new ArrayList<>();
        try {
            String sql = "select * from "+ tableName +" where brand_id=" + brandId;
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                Series series = new Series();
                series.setId(rs.getInt("id"));
                series.setSeriesName(rs.getString("name"));
                series.setBrandId(rs.getInt("brand_id"));
                list.add(series);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
        
        return list;
    }
    
    /**
     * 查询指定系列下的所有型号集合
     * @param tableName
     * @param seriesId
     * @return
     */
    public static List<Model> listBySeriesId(String tableName, Integer seriesId){
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        List<Model> list = new ArrayList<>();
        try {
            String sql = "select * from "+ tableName +" where series_id=" + seriesId;
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                Model model = new Model();
                model.setId(rs.getInt("id"));
                model.setModelName(rs.getString("name"));
                model.setVehicleParams(rs.getString("param"));
                model.setSeriesId(rs.getInt("series_id"));
                list.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
        
        return list;
    }
    
    public static List<Model> listAll(String tableName){
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        List<Model> list = new ArrayList<>();
        try {
            String sql = "select * from "+ tableName;
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                Model model = new Model();
                model.setId(rs.getInt("id"));
                model.setModelName(rs.getString("name"));
                model.setVehicleParams(rs.getString("param"));
                model.setSeriesId(rs.getInt("series_id"));
                list.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
        
        return list;
    }

    /**
     * 公共的保存车辆型号、参数方法
     * @param model 车辆型号对象
     * @param tableName 要存入的表名
     * @return
     */
    public static void saveModel(Model model, String tableName, String seqName) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        try {
            if(null == model.getSeriesId()) {
                model.setSeriesId(0);
            }
            if("".equals(model.getModelName())) {
                model.setModelName("unknown");
            }
            
            if("".equals(model.getVehicleParams())) {
                model.setVehicleParams("-");
            }
            String sql = "insert into " + tableName + " (id, name, series_id, param) values ( nextval('" + seqName + "' ),'" 
                 + model.getModelName() + "','" + model.getSeriesId() + "','" + model.getVehicleParams() + "')";
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            int row=st.executeUpdate(sql);
            if(row==1) {
                System.out.println("model插入结果：true");
            }else {
                System.out.println("model插入结果：false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
    }
    
    /**
     * 公共的保存车辆系列方法
     * @param series
     * @param tableName
     */
    public static void saveSeries(Series series,String tableName) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        try {
            String sql = "insert into " + tableName + " (id, name, brand_id) values ('" + series.getId() + "','" 
                    + series.getSeriesName() + "','"+ series.getBrandId() + "')";
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            int row=st.executeUpdate(sql);
            if(row==1) {
                System.out.println("Series插入结果：true");
            }else {
                System.out.println("Series插入结果：false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
    }
    
    /**
     * 公共的保存品牌信息的方法
     * @param brand
     * @param tableName
     */
    public static void saveBrand(Brand brand,String tableName) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        try {
            String sql = "insert into " + tableName + " (id, name, image) values (?, ?, ?)";
            conn = connectionPool.getConnection();
            byte[] b = brand.getImage();//得到数组byte[]
            System.out.println("Length = " + b.length);
            String s = Base64.encodeBytes(b, 0, b.length);
            System.out.println("s = " + s.length());
            String c = "decode(\'" + s + "\',\'base64\')";
            sql = sql.replace("?, ?, ?", "?, ?, " + c);
            
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, brand.getId());
            ps.setString(2, brand.getName());
            int row = ps.executeUpdate();
            if(row==1) {
                System.out.println("brand插入结果：true");
            }else {
                System.out.println("brand插入结果：false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        
    }
    
    public static void getPic(Integer id, String tableName) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            
            
            String sql = "select image from " + tableName + "  where id = "+id;
            Statement ps = conn.createStatement();
           
            ResultSet rs = ps.executeQuery(sql);
            if(rs.next()) {
                InputStream binaryStream = rs.getBinaryStream(1);
                
                BufferedInputStream in=null;
                BufferedOutputStream out=null;
                in=new BufferedInputStream(binaryStream);
                out=new BufferedOutputStream(new FileOutputStream("D:\\var\\logo\\"+System.currentTimeMillis()+".png"));
                int len=-1;
                byte[] b=new byte[1024];
                while((len=in.read(b))!=-1){
                    out.write(b,0,len);
                }
                in.close();
                out.close();
                //Blob image = rs.
                
              /*  outSTr = new FileOutputStream(new File("D:\\var\\logo\\"+System.currentTimeMillis()+".png")); 
                Buff=new BufferedOutputStream(outSTr); 
                Buff.write(image.getBytes(1, (int)image.length())); 
                Buff.flush(); 
                Buff.close(); */
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取序列id
     * @param seqName
     * @return
     */
    public static Integer getGenerateId(String seqName) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        Integer id = 0;
        try {
            String sql = "select nextval('" + seqName +  "')";
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            id = rs.getInt(1);
            //System.out.println("nextval is :"+id);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        return id;
    }
    
    @Deprecated
    public static void AutoHome_Insert(Model model) {
        ConnectionPool connectionPool = new ConnectionPool();
        Connection conn = null;
        try {
            String brandName = model.getBrandName();
            String seriesName = model.getSeriesName();
            String modelName = model.getModelName();
            String vehicleParams = model.getVehicleParams();
            if(CommonUtils.isNull(brandName)) {
                brandName = "-";
            }
            if(CommonUtils.isNull(seriesName)) {
                seriesName = "-";
            }
            if(CommonUtils.isNull(modelName)) {
                modelName = "-";
            }
            if(CommonUtils.isNull(vehicleParams)) {
                vehicleParams = "-";
            }
            String sql = "insert into autohome (brand, series, model, param) values('" + brandName + "','" +
                    seriesName + "','" + modelName + "','" + vehicleParams + "')";
            conn = connectionPool.getConnection();
            Statement st = conn.createStatement();
            int row=st.executeUpdate(sql);
            if(row==1) {
                System.out.println("autohome插入结果：true");
            }else {
                System.out.println("autohome插入结果：false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionLog.recordException(e, "AutoHome_Insert");
        }finally {
            connectionPool.backConnection(conn);
        }
    }
 

    /**
     * 汽车之家的SQL
     * @param model
     * @return
     */
    @Deprecated
    public static boolean JDBC_insert(Model model) {

       Connection conn = null;

       try {

           Class.forName("org.postgresql.Driver");

           conn = DriverManager.getConnection(url, usr, psd);

           Statement st = conn.createStatement();

           int rs = st.executeUpdate("insert into autohome (brand, series, model, param) values('" + model.getBrandName() + "','" +
           model.getSeriesName() + "','" + model.getModelName() + "','" + model.getVehicleParams() + "')");

     
           st.close();
           conn.close();
           if(rs==1) {
               return true;
           }
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
    }
}
