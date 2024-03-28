package com.proxyip.select.utils;

/**
 * <p>
 * IdGen
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/28 19:17
 */
public class IdGen {
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long twepoch = 1288834974657L;
    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;
    private long maxWorkerId;
    private long maxDatacenterId;
    private long sequenceBits;
    private long workerIdShift;
    private long datacenterIdShift;
    private long timestampLeftShift;
    private long sequenceMask;
    private long lastTimestamp;

    public IdGen(long workerId, long datacenterId) {
        this.maxWorkerId = ~(-1L << (int)this.workerIdBits);
        this.maxDatacenterId = ~(-1L << (int)this.datacenterIdBits);
        this.sequenceBits = 12L;
        this.workerIdShift = this.sequenceBits;
        this.datacenterIdShift = this.sequenceBits + this.workerIdBits;
        this.timestampLeftShift = this.sequenceBits + this.workerIdBits + this.datacenterIdBits;
        this.sequenceMask = ~(-1L << (int)this.sequenceBits);
        this.lastTimestamp = -1L;
        if (workerId <= this.maxWorkerId && workerId >= 0L) {
            if (datacenterId <= this.maxDatacenterId && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", this.maxDatacenterId));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & this.sequenceMask;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - this.twepoch << (int)this.timestampLeftShift | this.datacenterId << (int)this.datacenterIdShift | this.workerId << (int)this.workerIdShift | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}