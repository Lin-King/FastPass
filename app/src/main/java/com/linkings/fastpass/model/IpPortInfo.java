package com.linkings.fastpass.model;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by Lin on 2017/9/5.
 * Time: 16:49
 * Description: TOO
 */

public class IpPortInfo implements Serializable {
    private InetAddress inetAddress;
    private int port;

    public IpPortInfo(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
