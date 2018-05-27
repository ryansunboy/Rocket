package org.snail.rocket.common.utils;

import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.constants.RowsEventConstant;
import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.utils.support.Column;
import org.snail.rocket.common.utils.support.Table;
import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取数据库表结构工具类
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-28 16:13
 */

public class DBAnalysisUtils {
    private Connection connection;

    static{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DBAnalysisUtils(String connStr, String db, String username, String password) throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://"+ connStr +"/"+ db, username, password);
    }

    private static DBAnalysisUtils instance = null;

    private static DBAnalysisUtils getInstance(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DBAnalysisUtils(connStr, db, username, password);
        }
        return instance;
    }

    private static Connection getConnection(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        return getInstance(connStr, db, username, password).connection;
    }

    /**
     * 获取表的主键
     * @param conn 数据库连接
     * @param tableName  表名
     * @return 表中的主键
     * @throws SQLException
     */
    private static List<String> getPks(Connection conn, String tableName) throws SQLException {
        List<String> pks = new ArrayList<>();
        ResultSet rsPks = conn.getMetaData().getPrimaryKeys(null, null, tableName);

        while (rsPks.next()) {
            pks.add(rsPks.getString("COLUMN_NAME"));
        }
        rsPks.close(); //关闭
        return pks;
    }

    /**
     *  获取所有的列信息
     * @param conn 数据库连接
     * @param tableName 表名
     * @return 列的详细信息
     * @throws SQLException
     */
    public static List<Column> getColumns(Connection conn, String tableName) throws SQLException {
        List<Column> cols = new ArrayList<Column>();
        //获取这个表的主键 ，并存储在list中
        List pks = getPks(conn,tableName);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName + " limit 1;");                                   //此处需要优化 limit 1 top 1 rownum <= 1  根据不同数据库
        ResultSetMetaData rsCols = rs.getMetaData();
        int columnCount = rsCols.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            Column col = new Column();
            col.setTableName(rsCols.getTableName(i));
            col.setName(rsCols.getColumnName(i));
            col.setType(rsCols.getColumnTypeName(i));
            col.setPk(pks.contains(rsCols.getColumnName(i)) ? 1 : 0);
            col.setLength(rsCols.getColumnDisplaySize(i));
            col.setNotNull(rsCols.isNullable(i) == 0 ? 1 : 0);
            cols.add(col);
        }
        rs.close();
        stmt.close();
        return cols;
    }

    /**
     *  获取所有的列名称与编号的关系
     * @param connStr  数据库连接字符串
     * @param db 连接的库
     * @param username  数据库用户名
     * @param password   数据库密码
     * @param tableName 表名
     * @return 列的详细信息
     * @throws SQLException
     */
    public static Map<String,String> getColumnNameNoMap(String connStr, String db, String username, String password, String tableName) throws SQLException, ClassNotFoundException {
        Map<String,String> columnMap = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        if(StringUtils.isEmpty(db)){
            try
            {
                conn= DriverManager.getConnection("jdbc:mysql://"+ connStr+"/"+RowsEventConstant.DATABASE_DEFAULT_SCHEMA, username, password);
                String sql = "select TABLE_SCHEMA from TABLES t where t.table_name='"+tableName+"' limit 1";
                stmt= conn.createStatement();
                 rs= stmt.executeQuery(sql);
               while(rs.next()){
                   db = rs.getString(1);
                   break;
               }
            }
            finally
            {
                rs.close();
                stmt.close();
                conn.close();
            }
            

        }
            try
            {
                conn = DriverManager.getConnection("jdbc:mysql://"+ connStr +"/"+ db, username, password);
                //获取这个表的主键 ，并存储在list中
                List<String> pks = getPks(conn,tableName);
                stmt = conn.createStatement();
                String sql = "select * from " + tableName + " limit 1";
                rs = stmt.executeQuery(sql);                                   //此处需要优化 limit 1 top 1 rownum <= 1  根据不同数据库
                ResultSetMetaData rsCols = rs.getMetaData();
                int columnCount = rsCols.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columnMap.put(rsCols.getColumnName(i),String.valueOf(i-1));
                }
              
            } catch (Exception e){
             throw new RocketException(e);
            }
            finally{
                if(rs!=null) {
                    rs.close();
                }
                if(stmt!=null) {
                    stmt.close();
                }
                if(conn != null) {
                    conn.close();
                }
            }

        
        
        return columnMap;
    }

    /**
     * 获取所有表信息
     * @param connStr  数据库连接字符串
     * @param db 连接的库
     * @param username  数据库用户名
     * @param password   数据库密码
     * @return  库中表信息
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static List<Table> collectAllTables(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(connStr, db, username, password);
        return collectAllTables(conn,db);
    }

    /**
     *  获取所有表信息
     * @param conn 数据库连接 s
     * @param db 数据库
     * @return  库中表信息
     * @throws SQLException
     */
    public static List<Table> collectAllTables(Connection conn, String db) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();

        //获取库中的所有表
        ResultSet rsTables = dmd.getTables(null, null, null, new String[]{"TABLE"});
        List<Table> tables = new ArrayList<Table>();
        //将表存到list中
        while (rsTables.next()) {
            Table tb = new Table();
            tb.setSpace(db);
            //获取表名称
            String tbName = rsTables.getString("TABLE_NAME");
            tb.setName(tbName);

            //获取表中的字段及其类型
            List<Column> cols = getColumns(conn,tbName);
            tb.setColumns(cols);
            tables.add(tb);
        }
        rsTables.close();

        return tables;           //connection未关闭
    }
    public static List<String> generateTableFullName(String dataBase,String tableName){
        List<String> res = new ArrayList<>();
        if(StringUtils.isEmpty(dataBase)){
            res.add(tableName);
            return res;
        }
        String[] dataBases = dataBase.split(CommonConstant.SEPARATOR_DOT);
        for(String str : dataBases){
            res.add(str+CommonConstant.KEY_SEPARATOR_DOT+tableName);
        }
        return res;
    }

    public static Long getServerId(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException{
        Connection connection = getConnection(connStr,db,username,password);
        String sql = "SELECT @@server_id;";
        Statement  statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        String serverId=null;
        while(resultSet.next()){
            serverId = resultSet.getString("@@server_id");
        }
        return Long.valueOf(serverId);
    }
}
