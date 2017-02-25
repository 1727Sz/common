package cn.sz1727.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * cn.sz1727.core.i18n.ReloadControl.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ReloadControl {

	/**
	 * JDK 1.6 or later
	 */
	static class Always extends ResourceBundle.Control {
		private boolean needsReload = true;

		@Override
		public long getTimeToLive(String baseName, Locale locale) {
			if (needsReload) {
				return 0;
			}
			return ResourceBundle.Control.TTL_NO_EXPIRATION_CONTROL;
		}

		@Override
		public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
			// Do not cache, always reload
			return true;
		}

	}
}
