package org.snail.rocket.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.rocket.message.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-07 11:09
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    public static Map<String,SyncFuture<Message>> futureMap = new ConcurrentHashMap<>();
    private String clientId;
    private String auth;
    //利用写空闲发送心跳检测消息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    if (StringUtils.isNotBlank(clientId)) {
                        PingMsg pingMsg = new PingMsg(clientId);
                        ctx.writeAndFlush(pingMsg);
                        logger.debug("send ping to server.");
                    }
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGIN:{
                //向服务器发起登录
                LoginMsg loginMsg=new LoginMsg();
                loginMsg.setPassword("yao");
                loginMsg.setUserName("robin");
                channelHandlerContext.writeAndFlush(loginMsg);
                logger.info("connecting to server...");
            }break;
            case LOGINREPLY:{
                LoginReplyMsg loginReplyMsg = (LoginReplyMsg) baseMsg;
                clientId = loginReplyMsg.getClientId();
                auth = loginReplyMsg.getAuth();
                logger.info("connect server successful!");
            }break;
            case PING:{
                logger.debug("receive ping from server.");
            }break;
            case ASK:{
                ReplyClientBody replyClientBody=new ReplyClientBody("client info **** !!!");
                String requestId = UUID.randomUUID().toString();
                ReplyMsg replyMsg=new ReplyMsg(clientId,requestId);
                replyMsg.setBody(replyClientBody);
                channelHandlerContext.writeAndFlush(replyMsg);
            }break;
            case REPLY:{
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyServerBody replyServerBody=(ReplyServerBody)replyMsg.getBody();
                String info = replyServerBody.getServerInfo();
                logger.debug("receive client msg: "+info);
                Message message = JSONUtils.parse(info,Message.class);
                SyncFuture<Message> future = futureMap.get(replyMsg.getRequestId());
                future.setResponse(message);
            }break;
            case LOGOUTREPLY:{
                channelHandlerContext.close();
            }break;
            default:break;
        }
        ReferenceCountUtil.release(msgType);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}