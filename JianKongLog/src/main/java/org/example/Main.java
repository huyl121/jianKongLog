package org.example;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class Main {

    public static File fileLogJianKong;
    public static BigDecimal ling = new BigDecimal(0);



    public static void main(String[] args) {
        System.out.println("Hello world!");


        SpringApplication.run(Main.class, args);
        if(args.length==0){
            args = new String[1];
            args[0] = "E:/code/biance";
        }

        PrivateConfig.init((args[0]));
        if(PrivateConfig.daiLi.equals("1")) {
            System.out.println("开代理");
            System.setProperty("https.proxySet", "true");
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", "10809");
        }
        PrivateConfig.init1();

        String logPathJianKong = args[0] + "//" + "JianKong.txt";
        fileLogJianKong = new File(logPathJianKong);


        method();

    }

    public static void method() {
        try {
            //休息一下，等待主线程完成后，再监控
            Thread.sleep(10 * 1000);
            int a = 0;
            while (true) {
                try {
                    System.out.println(getCurrentTime() + "\n" + "jian Kong Log" + "\n");
                    Thread.sleep(10 * 60 * 1000);
//                    Thread.sleep(10 * 1000);

                    if (System.currentTimeMillis() - fileLogJianKong.lastModified() > 20 * 60 * 1000L) {
//                    if (System.currentTimeMillis() - fileLogJianKong.lastModified() > 20  * 1000L) {
                        T5.searchAll("log文件没有更新，抓紧电话胡亚龙");
                        Thread.sleep(1000 * 60);
                        T5.searchAll("log文件没有更新，抓紧电话胡亚龙");
                        Thread.sleep(1000 * 60);
                        T5.searchAll("log文件没有更新，抓紧电话胡亚龙");
                        Thread.sleep(1000 * 60);
                        a++;
                        if(a > 3){
                            qingCang(PrivateConfig.biCoin_personInfoList);
                            break;
                        }
                        continue;
                    }
                    a = 0;

                } catch (Exception e) {
                    Thread.sleep(1000 * 60);
                    e.printStackTrace();
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }

            while (true){
                try{
                    T5.searchAll("log文件没有更新，抓紧电话胡亚龙");
                }catch (Exception e){
                    e.printStackTrace();
                }
                Thread.sleep(60*60 * 1000);
            }
        } catch (Exception e) {
            System.out.println("监控log程序启动出错了");
            e.printStackTrace();
        }

    }

    public static void qingCang(List<JSONObject> listPersonInfo) {
        for (JSONObject personInfo : listPersonInfo) {
            try{
                SyncRequestClient syncRequestClient = (SyncRequestClient) personInfo.get("syncRequestClient");
                AccountInformation accountInformation = syncRequestClient.getAccountInformation();
                List<Position> positionList = accountInformation.getPositions();
                for (Position position : positionList) {
                    BigDecimal geShu = position.getPositionAmt().abs();
                    if (geShu.compareTo(ling) > 0) {
                        jianCang(syncRequestClient, position.getSymbol(), position.getPositionSide(), geShu.toString(), position.getPositionAmt());
                    }
                }
            }catch (Exception e){

            }

            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("清仓结束。");
    }

    public static void jianCang(SyncRequestClient syncRequestClient, String symbol, String positionSide, String getOrigQty, BigDecimal positionAmt) {

        if(positionSide.equals(PositionSide.BOTH.toString())){
            if(positionAmt.compareTo(ling) > 0){
                postOrder(syncRequestClient, symbol, OrderSide.SELL.toString(), PositionSide.BOTH.toString(), "TRUE", getOrigQty);
            }else {
                postOrder(syncRequestClient, symbol, OrderSide.BUY.toString(), PositionSide.BOTH.toString(), "TRUE", getOrigQty);
            }
        }else {
//            System.out.println("双向");
            if(positionAmt.compareTo(ling) > 0){
                postOrder(syncRequestClient, symbol, OrderSide.SELL.toString(), PositionSide.LONG.toString(), null, getOrigQty);
            }else {
                postOrder(syncRequestClient, symbol, OrderSide.BUY.toString(), PositionSide.SHORT.toString(), null, getOrigQty);
            }
        }
    }

    public static void postOrder(SyncRequestClient syncRequestClient, String symbol, String side, String positionSide, String reduceOnly, String getOrigQty) {
        for (int i = 0; i < 3; i++) {
            try {
                syncRequestClient.postOrder(
                        symbol,
                        OrderSide.valueOf(side),//买还是卖，做多时，buy sell；做空时，sell buy
                        PositionSide.valueOf(positionSide),//双向：long short 单向：both
                        OrderType.valueOf("MARKET"),// 订单类型，limit：限价单；MARKET：市价单（想要成功买卖，使用这个）
                        null,//TimeInForce.valueOf("GTC"),//成交为止，一直有效，不用管
                        getOrigQty,//跟单数量（下单时，跟单个数一定是大于0的，通过AccountInformation查询到的，做空的个数小于0，做多的大于0）
                        null,//跟单单价，总价需要大于5（市价时，可以不填）
                        reduceOnly,//order.getReduceOnly().toString(), //双向持仓时，只能传null
                        null,//order.getClientOrderId(),
                        null,//order.getStopPrice().toString(),
                        null,//WorkingType.valueOf(order.getWorkingType()),
                        NewOrderRespType.RESULT);
                return;
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

    }


    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(System.currentTimeMillis())); // 时间戳转换日期
    }

}