package org.snail.rocket;

import org.snail.rocket.common.utils.DBAnalysisUtils;

import java.sql.SQLException;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-28 16:41
 */

public class DBAnalysisUtilsTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Map<String,String> map = DBAnalysisUtils.getColumnNameNoMap("rm-bp1xde4nrn9z3x4v1.mysql.rds.aliyuncs.com:3306","","test2","J6stiVZs","parameter");
        System.out.println(map.size());
    }
}
