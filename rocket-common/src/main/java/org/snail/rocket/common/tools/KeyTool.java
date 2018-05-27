package org.snail.rocket.common.tools;

import org.snail.rocket.common.utils.DESUtils;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-18 17:28
 */

public class KeyTool {
    public static void main(String[] args) {
        String key = "";
        String password = "";
        for(int i=0;i<args.length;i++){
            if("-k".equalsIgnoreCase(args[i])){
                if(i+1>=args.length){
                    System.out.println("缺少-k参数值！");
                    return;
                }
                key = args[i+1];
            }
            if("-p".equalsIgnoreCase(args[i])){
                if(i+1>=args.length){
                    System.out.println("缺少-p参数值！");
                    return;
                }
                password = args[i+1];
            }
        }
        if("".equals(key)){
            String encryptText = DESUtils.encrypt(password,DESUtils.DEFAULT_DES_KEY);
            System.out.println("password:"+encryptText);
        } else {
            if(key.length()<8){
                System.out.print("输入的key小于8个字符!");
                return;
            }
            String encryptText = DESUtils.encrypt(password,key);
            String encryptKey = DESUtils.encrypt(key,DESUtils.DEFAULT_DES_KEY);
            System.out.println("privateKey:"+ encryptKey);
            System.out.println("password:"+ encryptText);
        }

    }
}
