package org.snail.rocket.common.utils;

import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.exception.RocketException;
import org.apache.commons.lang.StringUtils;


import java.math.BigDecimal;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-03-01 16:43
 */

public class CompareUtils {
    public static boolean EQ(String filterValue,String columnValue){
        if(filterValue.contains(CommonConstant.SEPARATOR_DOT)){
            String[] filterValueArray = filterValue.split(CommonConstant.SEPARATOR_DOT);
            for (String filter : filterValueArray)
            {
                if(filter.equals(columnValue)){
                    return true;
                }
            }
        }else if(columnValue.equals(filterValue)){
            return true;
        }
        return false;
    }

    public static boolean NEQ(String filterValue,String columnValue){
        return !EQ(filterValue,columnValue);
    }

    public static boolean GT(String filterValue,String columnValue){
        BigDecimal colum;
        BigDecimal filterBig;
        if(StringUtils.isNumeric(columnValue)){
            colum  = new BigDecimal(columnValue);
        } else {
            throw new RocketException("非法数值！");
        }
        if(filterValue.contains(CommonConstant.SEPARATOR_DOT)){
            String[] filterValueArray = filterValue.split(CommonConstant.SEPARATOR_DOT);
            for (String filter : filterValueArray)
            {
                if(StringUtils.isNumeric(filter)){
                    filterBig = new BigDecimal(filter);
                } else {
                    throw new RocketException("非法数值！");
                }
                if(colum.compareTo(filterBig) > 0){
                    return true;
                }
            }
        }else {
            if(StringUtils.isNumeric(filterValue)){
                filterBig  = new BigDecimal(filterValue);
            } else {
                throw new RocketException("非法数值！");
            }
            if(colum.compareTo(filterBig) > 0){
                return true;
            }
        }
        return false;
    }

    public static boolean GTE(String filterValue,String columnValue){
        BigDecimal colum;
        BigDecimal filterBig;
        if(StringUtils.isNumeric(columnValue)){
            colum  = new BigDecimal(columnValue);
        } else {
            throw new RocketException("非法数值！");
        }
        if(filterValue.contains(CommonConstant.SEPARATOR_DOT)){
            String[] filterValueArray = filterValue.split(CommonConstant.SEPARATOR_DOT);
            for (String filter : filterValueArray)
            {
                if(StringUtils.isNumeric(filter)){
                    filterBig = new BigDecimal(filter);
                } else {
                    throw new RocketException("非法数值！");
                }
                if(colum.compareTo(filterBig) >= 0){
                    return true;
                }
            }
        }else {
            if(StringUtils.isNumeric(filterValue)){
                filterBig  = new BigDecimal(filterValue);
            } else {
                throw new RocketException("非法数值！");
            }
            if(colum.compareTo(filterBig) >= 0){
                return true;
            }
        }
        return false;
    }

    public static boolean LT(String filterValue,String columnValue){
        BigDecimal colum;
        BigDecimal filterBig;
        if(StringUtils.isNumeric(columnValue)){
            colum  = new BigDecimal(columnValue);
        } else {
            throw new RocketException("非法数值！");
        }
        if(filterValue.contains(CommonConstant.SEPARATOR_DOT)){
            String[] filterValueArray = filterValue.split(CommonConstant.SEPARATOR_DOT);
            for (String filter : filterValueArray)
            {
                if(StringUtils.isNumeric(filter)){
                    filterBig = new BigDecimal(filter);
                } else {
                    throw new RocketException("非法数值！");
                }
                if(colum.compareTo(filterBig) < 0){
                    return true;
                }
            }
        }else {
            if(StringUtils.isNumeric(filterValue)){
                filterBig  = new BigDecimal(filterValue);
            } else {
                throw new RocketException("非法数值！");
            }
            if(colum.compareTo(filterBig) < 0){
                return true;
            }
        }
        return false;
    }

    public static boolean LTE(String filterValue,String columnValue){
        BigDecimal colum;
        BigDecimal filterBig;
        if(StringUtils.isNumeric(columnValue)){
            colum  = new BigDecimal(columnValue);
        } else {
            throw new RocketException("非法数值！");
        }
        if(filterValue.contains(CommonConstant.SEPARATOR_DOT)){
            String[] filterValueArray = filterValue.split(CommonConstant.SEPARATOR_DOT);
            for (String filter : filterValueArray)
            {
                if(StringUtils.isNumeric(filter)){
                    filterBig = new BigDecimal(filter);
                } else {
                    throw new RocketException("非法数值！");
                }
                if(colum.compareTo(filterBig) <= 0){
                    return true;
                }
            }
        }else {
            if(StringUtils.isNumeric(filterValue)){
                filterBig  = new BigDecimal(filterValue);
            } else {
                throw new RocketException("非法数值！");
            }
            if(colum.compareTo(filterBig) <= 0){
                return true;
            }
        }
        return false;
    }

}
