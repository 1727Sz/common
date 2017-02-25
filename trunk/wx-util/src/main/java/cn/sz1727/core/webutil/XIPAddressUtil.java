package cn.sz1727.core.webutil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.util.XStringUtil;

/**
 * cn.sz1727.core.webutil.XIPAddressUtil.java
 * 
 * @author Dongd_ZHOU
 * @creation 2014年8月19日
 * 
 */
public final class XIPAddressUtil {
	private static final Logger LOG = LoggerFactory.getLogger(XIPAddressUtil.class);

	private XIPAddressUtil() {
	}

	public static String getIPAddress(HttpServletRequest request) {
		String realIP = request.getHeader("x-forwarded-for");
		if (XStringUtil.isNotBlank(realIP)) {
			while ((XStringUtil.isNotBlank(realIP) && realIP.equalsIgnoreCase("unknow"))) {
				realIP = request.getHeader("x-forwarded-for");
			}
		}
		if (XStringUtil.isBlank(realIP)) {
			realIP = request.getHeader("Proxy-Clint-IP");
		}
		if (XStringUtil.isBlank(realIP)) {
			realIP = request.getHeader("WL-Proxy-Clint-IP");
		}
		if (XStringUtil.isBlank(realIP)) {
			realIP = request.getRemoteAddr();
		}
		return realIP;
	}

	public static boolean isContainIPInLocalHost(String ipStr) {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				// System.out.println("DisplayName:" + ni.getDisplayName());
				// System.out.println("Name:" + ni.getName());
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					String ip = ips.nextElement().getHostAddress();
					// LOG.info("获取本地IP:"+ip);
					if (XStringUtil.contains(ipStr, ip)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("获取本地IP错误", e);
		}
		return false;
	}

	public static String getHostIP() {
		String localIP = null;
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						localIP = ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			LOG.error("获取本地IP异常", e);
		}
		return localIP;
	}
}