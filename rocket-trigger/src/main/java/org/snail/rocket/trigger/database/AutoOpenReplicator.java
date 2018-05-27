package org.snail.rocket.trigger.database;

import com.google.code.or.common.glossary.column.StringColumn;
import com.google.code.or.net.Packet;
import com.google.code.or.net.Transport;
import com.google.code.or.net.TransportException;
import com.google.code.or.net.impl.packet.EOFPacket;
import com.google.code.or.net.impl.packet.ErrorPacket;
import com.google.code.or.net.impl.packet.ResultSetRowPacket;
import com.google.code.or.net.impl.packet.command.ComBinlogDumpPacket;
import com.google.code.or.net.impl.packet.command.ComQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 经过封装的支持重连的binlog连接器
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 15:46
 */

public class AutoOpenReplicator extends DefaultOpenReplicator {

    private static Logger logger = LoggerFactory.getLogger(AutoOpenReplicator.class);

    private boolean autoReconnect = true;

    private int delayReconnect = 30;

    private int defaultTimeout = 120 * 1000;

    private Transport comQueryTransport;

    private long lastAlive;


    @Override
    protected void dumpBinlog() throws Exception {
        logger.info(String.format("starting replication at %s:%d", this.binlogFileName, this.binlogPosition));
        final ComBinlogDumpPacket command = new ComBinlogDumpPacket();
        command.setBinlogFlag(0);
        command.setServerId(stopOnEOF ? 0 : this.serverId);
        command.setBinlogPosition(this.binlogPosition);
        command.setBinlogFileName(StringColumn.valueOf(this.binlogFileName.getBytes(this.encoding)));
        this.transport.getOutputStream().writePacket(command);
        this.transport.getOutputStream().flush();

        //
        final Packet packet = this.transport.getInputStream().readPacket();
        if(packet.getPacketBody()[0] == ErrorPacket.PACKET_MARKER) {
            final ErrorPacket error = ErrorPacket.valueOf(packet);
            throw new TransportException(error);
        }
    }

    /**
     * 是否自动重连
     *
     * @return 自动重连
     */
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    /**
     * 设置自动重连
     *
     * @param autoReconnect 自动重连
     */
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    /**
     * 断开多少秒后进行自动重连
     *
     * @param delayReconnect 断开后多少秒
     */
    public void setDelayReconnect(int delayReconnect) {
        this.delayReconnect = delayReconnect;
    }

    /**
     * 断开多少秒后进行自动重连
     *
     * @return 断开后多少秒
     */
    public int getDelayReconnect() {
        return delayReconnect;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public long getLastAlive() {
        return lastAlive;
    }

    public void setLastAlive(long lastAlive) {
        this.lastAlive = lastAlive;
    }

    @Override
    public void start() {
        do {
            try {
                long current = System.currentTimeMillis();
                if (!this.isRunning()) {
                    updatePosition();
                    logger.info("Try to startup dump binlog from mysql master[{}, {}] ...", this.binlogFileName, this.binlogPosition);
                    this.reset();
                    super.start();
                    this.lastAlive = current;
                    logger.info("Startup successfully! the status check is {} seconds", defaultTimeout / 1000);
                } else {
                    /*if ((current - this.lastAlive >= this.defaultTimeout)) {
                        stopQuietly(0, TimeUnit.SECONDS);
                    }*/
                }
                TimeUnit.SECONDS.sleep(this.getDelayReconnect());
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("connect mysql failure!", e);
                }
                // reconnect failure, reget last binlog & position from master node and update cache!
                //LoadCenter.loadAll(); // just update all cache, not flush!
                updatePosition();
                try {
                    TimeUnit.SECONDS.sleep(this.getDelayReconnect());
                } catch (InterruptedException ignore) {
                    // NOP
                }
            }
        } while (this.autoReconnect);
    }

    @Override
    public void stopQuietly(long timeout, TimeUnit unit) {
        super.stopQuietly(timeout, unit);
        if (this.getBinlogParser() != null) {
            // 重置, 当MySQL服务器进行restart/stop操作时进入该流程
            this.binlogParser.setParserListeners(null); // 这句比较关键，不然会死循环
        }
    }

    /**
     * 自动配置binlog位置
     */
    private void updatePosition() {
        // 配置binlog位置
        try {
            ResultSetRowPacket binlogPacket = query("show master status");
            if (binlogPacket != null) {
                List<StringColumn> values = binlogPacket.getColumns();
                this.setBinlogFileName(values.get(0).toString());
                this.setBinlogPosition(Long.valueOf(values.get(1).toString()));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("update binlog position failure!", e);
            }
        }
    }

    /**
     * ComQuery 查询
     *
     * @param sql 查询语句
     * @return
     */
    private ResultSetRowPacket query(String sql) throws Exception {
        ResultSetRowPacket row = null;
        final ComQuery command = new ComQuery();
        command.setSql(StringColumn.valueOf(sql.getBytes()));
        if (this.comQueryTransport == null) {
            this.comQueryTransport = getDefaultTransport();
        }
        this.comQueryTransport.connect(this.host, this.port);
        this.comQueryTransport.getOutputStream().writePacket(command);
        this.comQueryTransport.getOutputStream().flush();
        // step 1
        this.comQueryTransport.getInputStream().readPacket();
        //
        Packet packet;
        // step 2
        while (true) {
            packet = comQueryTransport.getInputStream().readPacket();
            if (packet.getPacketBody()[0] == EOFPacket.PACKET_MARKER) {
                break;
            }
        }
        // step 3
        while (true) {
            packet = comQueryTransport.getInputStream().readPacket();
            if (packet.getPacketBody()[0] == EOFPacket.PACKET_MARKER) {
                break;
            } else {
                row = ResultSetRowPacket.valueOf(packet);
            }
        }
        this.comQueryTransport.disconnect();
        return row;
    }

    private void reset() {
        this.transport = null;
        this.binlogParser = null;
    }

    public void updateLastAliveTime(){
        this.lastAlive = System.currentTimeMillis();
    }
}
