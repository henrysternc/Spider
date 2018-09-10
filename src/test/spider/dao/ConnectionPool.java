package test.spider.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.sql.DataSource;

public  class ConnectionPool implements DataSource {
    private Connection conn;

    // 1.����һ������������Ӷ���Connection
    private static LinkedList<Connection> pool = new LinkedList<Connection>();

    // ѭ��Ϊpool���5�����Ӷ���
    static {
        for (int i = 0; i < 5; i++) {
            Connection conn = JDBCTemplate.getConnection();
            pool.add(conn);
        }

    }

    public ConnectionPool() {

    }

    public ConnectionPool(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                conn = JDBCTemplate.getConnection();
                pool.add(conn);
            }
        }
        conn = pool.removeFirst();
        return conn;
    }

    public void backConnection(Connection conn) {
        pool.add(conn);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLoginTimeout(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getConnection(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
