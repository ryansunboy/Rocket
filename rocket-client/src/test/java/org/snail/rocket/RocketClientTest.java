package org.snail.rocket;

import org.snail.rocket.client.RocketClient;
import org.snail.rocket.client.exception.RocketTimeOutException;
import org.snail.rocket.client.netty.ConnectionFactory;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.utils.JSONUtils;

/**
 * Unit test for simple App.
 */
public class RocketClientTest {
    public static void main(String[] args) throws InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory(28800,"localhost","rocket","rocket");
        RocketClient rocketClient = new RocketClient();
        rocketClient.setConnectionFactory(connectionFactory);
        Message message;
        try {
            message = rocketClient.recieveMessage("delay");
            System.out.println(JSONUtils.toJSONString(message));
        } catch (RocketTimeOutException e) {
            e.printStackTrace();
        } finally {
            rocketClient.close();
        }
    }

}
