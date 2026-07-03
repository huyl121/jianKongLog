package org.example;
public class SyncRequestImpl implements SyncRequestClient {

    private final RestApiRequestImpl requestImpl;

    SyncRequestImpl(RestApiRequestImpl requestImpl) {
        this.requestImpl = requestImpl;
    }

    @Override
    public AccountInformation getAccountInformation() {
        return RestApiInvoker.callSync(requestImpl.getAccountInformation());
    }

    @Override
    public Order postOrder(String symbol, OrderSide side, PositionSide positionSide, OrderType orderType,
                           TimeInForce timeInForce, String quantity, String price, String reduceOnly,
                           String newClientOrderId, String stopPrice, WorkingType workingType, NewOrderRespType newOrderRespType) {
        return RestApiInvoker.callSync(requestImpl.postOrder(symbol, side, positionSide, orderType,
                timeInForce, quantity, price, reduceOnly,
                newClientOrderId, stopPrice, workingType,newOrderRespType));
    }

}
