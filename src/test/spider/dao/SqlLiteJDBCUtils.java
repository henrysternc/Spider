package test.spider.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import test.spider.pojo.Brand;
import test.spider.pojo.Model;
import test.spider.pojo.Series;

public class SqlLiteJDBCUtils {
    
    private static final String Class_Name = "org.sqlite.JDBC";
    
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\shizh\\Desktop\\车型sql\\vehicle.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(Class_Name);
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    
    @SuppressWarnings("resource")
    public static Integer saveBrand(Brand brand) throws Exception{
        String sql = "insert into a_brand (id, name, image) values(null, ?, ?)";
       // SqlLitePool connectionPool = new SqlLitePool();
        Connection conn = null;
        Integer id = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            //ps.setInt(1, brand.getId());
            ps.setString(1, brand.getName());
            //ps.setBlob(3, brand.getImage1());
            ps.setBytes(2, brand.getImage());
            int row = ps.executeUpdate();
            
           
            if(row==1) {
                System.out.println("Brand插入结果：true");
            }else {
                System.out.println("Brand插入结果：false");
            }
            
            String sql2 = "SELECT max(Id) FROM a_brand;";
            ps = conn.prepareStatement(sql2);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ps.close();
            conn.close();
        }
        return id;
    }
    
    @Deprecated
    public static Integer saveSeries(Series series) {
        Integer id = null;
        String sql = "insert into a_series (id, name, brand_id) values(null, ?, ?)";
        SqlLitePool connectionPool = new SqlLitePool();
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, series.getSeriesName());
            ps.setInt(2, series.getBrandId());
            int row = ps.executeUpdate();
            if(row==1) {
                System.out.println("Series插入结果：true");
            }else {
                System.out.println("Series插入结果：false");
            }
            String sql2 = "SELECT max(Id) FROM a_series;";
            ps = conn.prepareStatement(sql2);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
        return id;
    }
    
    @Deprecated
   public static void saveModel(Model model) {
        
        SqlLitePool connectionPool = new SqlLitePool();
        Connection conn = null;
        try {
            String sql = "insert into a_model (id, name, series_id,param) values(null, ?, ?, ?)";
            conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            //ps.setInt(1, series.getId());
            ps.setString(1, model.getModelName());
            ps.setInt(2, model.getSeriesId());
            ps.setString(3, model.getVehicleParams());
            int row = ps.executeUpdate();
            if(row==1) {
                System.out.println("Model插入结果：true");
            }else {
                System.out.println("Model插入结果：false");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connectionPool.backConnection(conn);
        }
    }
}
