package cn.sz1727.core.dao.api;

import java.net.URISyntaxException;
import java.net.URL;

import javax.persistence.spi.PersistenceUnitInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * cn.sz1727.mao.dao.api.MergingPersistenceUnitManager.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class MergingPersistenceUnitManager extends DefaultPersistenceUnitManager {
	private static final Logger LOG = LoggerFactory.getLogger(MergingPersistenceUnitManager.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager
	 * #
	 * postProcessPersistenceUnitInfo(org.springframework.orm.jpa.persistenceunit
	 * .MutablePersistenceUnitInfo)
	 */
	@Override
	protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {

		// Invoke normal post processing
		super.postProcessPersistenceUnitInfo(pui);

		PersistenceUnitInfo oldPui = getPersistenceUnitInfo(pui.getPersistenceUnitName());

		if (oldPui != null) {
			postProcessPersistenceUnitInfo(pui, oldPui);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager
	 * #isPersistenceUnitOverrideAllowed()
	 */
	@Override
	protected boolean isPersistenceUnitOverrideAllowed() {
		return true;
	}

	protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui, PersistenceUnitInfo oldPui) {

		String persistenceUnitName = pui.getPersistenceUnitName();

		for (URL url : oldPui.getJarFileUrls()) {
			if (!pui.getJarFileUrls().contains(url)) {
				LOG.debug("Adding JAR file URL {} to persistence unit {}.", url, persistenceUnitName);
				pui.addJarFileUrl(url);
			}
		}

		for (String className : oldPui.getManagedClassNames()) {
			if (!pui.getManagedClassNames().contains(className)) {
				LOG.debug("Adding class {} to PersistenceUnit {}", className, persistenceUnitName);
				pui.addManagedClassName(className);
			}
		}

		for (String mappingFileName : oldPui.getMappingFileNames()) {
			if (!pui.getMappingFileNames().contains(mappingFileName)) {
				LOG.debug("Adding mapping file to persistence unit {}.", mappingFileName, persistenceUnitName);
				pui.addMappingFileName(mappingFileName);
			}
		}

		URL newUrl = pui.getPersistenceUnitRootUrl();
		URL oldUrl = oldPui.getPersistenceUnitRootUrl();

		if (oldUrl == null || newUrl == null) {
			return;
		}

		try {
			boolean rootUrlsDiffer = !newUrl.toURI().equals(oldUrl.toURI());
			boolean urlNotInJarUrls = !pui.getJarFileUrls().contains(oldUrl);

			if (rootUrlsDiffer && urlNotInJarUrls) {
				pui.addJarFileUrl(oldUrl);
			}
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}
}