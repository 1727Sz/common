package cn.sz1727.core.db;

import org.apache.commons.dbcp.BasicDataSource;

import cn.sz1727.core.util.XDesEncrypt;

/**
 * cn.sz1727.core.db.MallBasicDataSource.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月20日
 * 
 */

public class MallBasicDataSource extends BasicDataSource {

	@Override
	public void setPassword(String password) {
		super.setPassword(decode(password));
	}

	@Override
	public void setUsername(String username) {
		super.setUsername(decode(username));
	}

	protected String decode(String encStr) {
		return XDesEncrypt.getInstace().getDesStr(encStr);
	}

}
