package org.snail.rocket.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.snail.rocket.common.container.DefaultQueuePool;
import org.snail.rocket.common.container.DelayQueuePool;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.rocket.message.*;

import java.util.UUID;


/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 17:00
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private DefaultQueuePool queuePool = new DefaultQueuePool();
    private DelayQueuePool delayPool = new DelayQueuePool();

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel失效，从Map中移除
        NettyChannelMap.remove((SocketChannel)ctx.channel());
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {

        if(MsgType.LOGIN.equals(baseMsg.getType())){
            LoginMsg loginMsg=(LoginMsg)baseMsg;
            if("rocket".equals(loginMsg.getUserName())&&"rocket".equals(loginMsg.getPassword())){
                //登录成功,把channel存到服务端的map中
                String clientId = UUID.randomUUID().toString();
                String auth = UUID.randomUUID().toString();
                loginMsg.setClientId(clientId);
                NettyChannelMap.add(loginMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
                NettyChannelMap.addAuth(loginMsg.getClientId(),auth);
                logger.info("client"+loginMsg.getClientId()+" 登录成功");
                LoginReplyMsg loginReplyMsg = new LoginReplyMsg();
                loginReplyMsg.setClientId(clientId);
                loginReplyMsg.setAuth(auth);
                NettyChannelMap.get(loginMsg.getClientId()).writeAndFlush(loginReplyMsg);
            }
        }else{
            if(baseMsg.getClientId() == null || NettyChannelMap.get(baseMsg.getClientId())==null){
                //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                LoginMsg loginMsg=new LoginMsg();
                channelHandlerContext.channel().writeAndFlush(loginMsg);
            }
        }
        switch (baseMsg.getType()){
            case PING:{
                PingMsg pingMsg=(PingMsg)baseMsg;
                PingMsg replyPing=new PingMsg(baseMsg.getClientId());
                NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
            }break;
            case ASK:{
                //收到客户端的请求
                AskMsg askMsg=(AskMsg)baseMsg;
                if(NettyChannelMap.getAuth(askMsg.getClientId()).equals((askMsg.getParams().getAuth()))){
                    Message message;
                    if("delay".equals(askMsg.getGroupType())){
                        message = delayPool.take();
                    } else if("realTime".equals(askMsg.getGroupType())) {
                        message = queuePool.take();
                    } else {
                        logger.warn("未配置分组字段！");
                        message = queuePool.take();
                    }
                    ReplyServerBody replyBody=new ReplyServerBody(JSONUtils.toJSONString(message));
                    ReplyMsg replyMsg=new ReplyMsg(baseMsg.getClientId(),askMsg.getRequestId());
                    replyMsg.setBody(replyBody);
                    NettyChannelMap.get(askMsg.getClientId()).writeAndFlush(replyMsg);
                }
            }break;
            case REPLY:{
                //收到客户端
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyClientBody clientBody=(ReplyClientBody)replyMsg.getBody();
                logger.info("receive client msg: "+clientBody.getClientInfo());
            }break;
            case LOGOUT:{
                //收到客户端
                LogOutMsg logOutMsg=(LogOutMsg) baseMsg;
                String clientId = logOutMsg.getClientId();
                LogOutReplyMsg logOutReplyMsg = new LogOutReplyMsg();
                NettyChannelMap.get(clientId).writeAndFlush(logOutReplyMsg);
                NettyChannelMap.removeChannel(clientId);
                NettyChannelMap.removeAuth(clientId);
                logger.info("client"+clientId+" 登出成功.");
            }break;
            default:break;
        }
        ReferenceCountUtil.release(baseMsg);
    }
}
