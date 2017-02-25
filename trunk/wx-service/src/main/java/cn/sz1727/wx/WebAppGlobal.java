package cn.sz1727.wx;

/**
 * cn.sz1727.wx.WebAppGlobal.java
 *
 * @author Dongd_Zhou
 * @creation Sep 4, 2014
 * 
 */
public final class WebAppGlobal {
	
	private WebAppGlobal() {}
	
	public static final String PROJECT_ID = System.getProperty("project.id", "");

	public static final String ENCODING_DEFAULT = System.getProperty("encoding.default", "utf-8");
}
