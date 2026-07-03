package org.example;


import java.util.List;

/**
 * Synchronous request interface, invoking Binance RestAPI via synchronous
 * method.<br>
 * All methods in this interface will be blocked until the RestAPI response.
 * <p>
 * If the invoking failed or timeout, the
 *  will be thrown.
 */
public interface SyncRequestClient {


    AccountInformation getAccountInformation();


    Order postOrder(String symbol, OrderSide side, PositionSide positionSide, OrderType orderType,
                    TimeInForce timeInForce, String quantity, String price, String reduceOnly,
                    String newClientOrderId, String stopPrice, WorkingType workingType, NewOrderRespType newOrderRespType);

    static SyncRequestClient create(String apiKey, String secretKey, RequestOptions options) {
        return BinanceApiInternalFactory.getInstance().createSyncRequestClient(apiKey, secretKey, options);
    }


}