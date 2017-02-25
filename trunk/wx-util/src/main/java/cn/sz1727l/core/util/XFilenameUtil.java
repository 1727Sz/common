package cn.sz1727l.core.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * cn.sz1727l.core.util.XFilenameUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XFilenameUtil extends FilenameUtils {

	private XFilenameUtil() {
	}

	/**
	 * Create file using sub paths i.e. pathComponents = {"d:", "ocp",
	 * "test.xml"} file path = d:\ocp\test.xml
	 * 
	 * @param pathComponents
	 * @return
	 */
	public static File fileWithSubpaths(String[] subpaths) {
		if (subpaths == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder(64);
		for (int i = 0; i < subpaths.length; i++) {
			String component = subpaths[i];
			if (component == null) {
				continue;
			}

			buf.append(component);
			if (i < subpaths.length - 1) {
				buf.append(File.separator);
			}
		}
		return XFileUtil.newFile(buf.toString());
	}
}
