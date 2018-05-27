package org.snail.rocket.common.tools;

import org.snail.rocket.common.utils.DESUtils;

import java.util.Scanner;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-18 17:28
 */

public class EncryptTool {
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        System.out.print("请输入需要加密的内容：");
        String text = sc.nextLine();
        System.out.print("是否使用默认加密key?Y/N：");
        String flag = sc.nextLine();
        if("y".equalsIgnoreCase(flag)){
            String encryptText = DESUtils.encrypt(text,DESUtils.DEFAULT_DES_KEY);
            System.out.println("加密后的密文为："+encryptText);
        } else {
            System.out.print("请输入加密的key(大于等于8个字符)：");
            String key = sc.next();
            while(key.getBytes().length < 8){
                System.out.print("输入的key小于8个字符，请重新输入：");
                key = sc.next();
            }
            String encryptText = DESUtils.encrypt(text,key);
            String encryptKey = DESUtils.encrypt(key,DESUtils.DEFAULT_DES_KEY);
            System.out.println("加密使用的key为："+ key);
            System.out.println("key的密文为："+ encryptKey+"  请将key密文加入到rocket配置文件rocket.encrypt.key中。");
            System.out.println("加密后的密文为："+ encryptText);
        }
    }
}
