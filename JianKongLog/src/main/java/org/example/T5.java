package org.example;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class T5 {

    /*
    * 问题1：
    *   打开微信窗口的快捷键，在微信已经处于输入状态时，会关闭微信的窗口
    * */


    static Long ShangCiFaSongShiJian = 0L;
    public static void main(String[] args) throws InterruptedException, IOException {


    }


    public static void searchAll(String txt) throws GeneralSecurityException {
//        return;
        Long currentTime = System.currentTimeMillis();
        if ((currentTime - ShangCiFaSongShiJian) > 1000 * 20) {
            ShangCiFaSongShiJian = currentTime;
        } else {
            return;
        }
        SendEmail.method(PrivateConfig.computer, txt);
    }


}

