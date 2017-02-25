package cn.sz1727.core.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import cn.sz1727.core.config.ConfigurationManager;
import cn.sz1727.core.config.PropertyLoader;
import cn.sz1727.core.exception.LoadingSystemPropertyException;
import cn.sz1727.core.exception.ProjectIdNotConfiguredException;
import cn.sz1727.core.i18n.CoreErrorMessages;
import cn.sz1727l.core.util.XStringUtil;

/**
 * cn.sz1727.core.context.MallSystemPropertiesLoader.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class MallSystemPropertiesLoader implements SystemPropertiesLoader {
	public static final String PROJECT_ID = "project.id";
	public static final String PROJECT_CONFIGURATION_FILE = "project.properties";
	public static final String SYSTEM_PROPERTY_FILE = "config_sys.properties";

	private static final Logger LOG = LoggerFactory.getLogger(MallSystemPropertiesLoader.class);

	@Override
	@PostConstruct
	public void loadSystemProperties() {
		LOG.info("<<< Start loading system properties ...");
		String projectId = getProjectID();
		loadSystemProperties(projectId);
		LOG.info(">>> Finish loading system properties ...");
	}

	private String getProjectID() {
		Properties properties = loadPropertyFile(PROJECT_CONFIGURATION_FILE);
		String projectId = properties.getProperty(PROJECT_ID);
		if (XStringUtil.isBlank(projectId)) {
			throw new ProjectIdNotConfiguredException(CoreErrorMessages.create().projectIdNotConfigured());
		}
		return projectId;
	}

	private void loadSystemProperties(String projectId) {
		File cfgFile;
		String defaultFilePath = "";
		try {
			cfgFile = ResourceUtils.getFile("classpath:" + SYSTEM_PROPERTY_FILE);
			if (cfgFile.exists()) {
				defaultFilePath = cfgFile.getAbsolutePath();
			}
		} catch (FileNotFoundException e) {
			LOG.error(" <<< not found {} file when loading.", SYSTEM_PROPERTY_FILE, e);
			// ignore exception here
		}

		String propertyFileLocation = System.getProperty(projectId, defaultFilePath);

		if (XStringUtil.isBlank(propertyFileLocation)) {
			throw new LoadingSystemPropertyException(CoreErrorMessages.create().systemPropertyEnvVarNotConfigured(projectId));
		}

		String propertyFile = XStringUtil.endsWithIgnoreCase(propertyFileLocation, SYSTEM_PROPERTY_FILE) ? propertyFileLocation : propertyFileLocation + File.separator + SYSTEM_PROPERTY_FILE;

		if (!new File(propertyFile).exists()) {
			throw new LoadingSystemPropertyException(CoreErrorMessages.create().systemPropertyFileNotFound(propertyFile));
		}

		Properties properties = loadPropertyFile(propertyFile);

		Set<Entry<Object, Object>> entries = properties.entrySet();
		for (Entry<Object, Object> entry : entries) {
			// 设置配置信息到系统全局配置
			System.setProperty(entry.getKey().toString(), entry.getValue().toString());
			LOG.info("Setup system properties, key: " + entry.getKey().toString() + ", value: " + entry.getValue().toString());
		}
		ConfigurationManager.getRef().reload();
	}

	private Properties loadPropertyFile(String file) {
		
		PropertyLoader loader = new PropertyLoader(false);
		loader.load(file);
		Properties properties = loader.getProperties();
		return properties;
	}

}
