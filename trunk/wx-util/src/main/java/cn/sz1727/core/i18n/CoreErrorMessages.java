package cn.sz1727.core.i18n;

import java.net.URL;

import cn.sz1727.core.config.Focus;

/**
 * cn.sz1727.core.i18n.CoreErrorMessages.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class CoreErrorMessages extends AbstractErrorMessageFactory {
	public static final String CORE_ERROR_BUNDLE_PREFIX = "core";

	private static class CoreErrorMessagesHolder {
		public final static CoreErrorMessages INSTANCE = new CoreErrorMessages();
	}

	private CoreErrorMessages() {
	}

	public static CoreErrorMessages create() {
		return CoreErrorMessagesHolder.INSTANCE;
	}

	// ~ Runtime
	// ----------------------------------------------------------------------------

	public ErrorMessage nullArguments(String arg) {
		return createErrorMessage(101000, arg);
	}

	public ErrorMessage systemPropertyEnvVarNotConfigured(String key) {
		return createErrorMessage(101001, key);
	}

	public ErrorMessage systemPropertyFileNotFound(String location) {
		return createErrorMessage(101002, location);
	}

	public ErrorMessage memcachedServerAddressNotFound(String key) {
		return createErrorMessage(101003, key);
	}

	public ErrorMessage zookeeperServerAddressNotFound(String key) {
		return createErrorMessage(101004, key);
	}

	public ErrorMessage projectIdNotConfigured() {
		return createErrorMessage(101005);
	}

	// ~ Configuration
	// ----------------------------------------------------------------------

	public ErrorMessage loadResourceFromUrlFailed(URL url, String exDetails) {
		return createErrorMessage(102000, exDetails, url);
	}

	public ErrorMessage resolveResourceNameFailed(String resourceName, String exDetails) {
		return createErrorMessage(102001, exDetails, resourceName);
	}

	public ErrorMessage loadOCPResourceFailed() {
		return createErrorMessage(102002);
	}

	public ErrorMessage mandatoryPropsOfResource() {
		return createErrorMessage(102003);
	}

	public ErrorMessage propNotFoundOfResource(String propName, Focus focus) {
		return createErrorMessage(102004, propName, focus);
	}

	public ErrorMessage nonReadablePropErrorOfResource(String propName) {
		return createErrorMessage(102005, propName);
	}

	public ErrorMessage unknowFailureSettingsForProps(String propKeyPath) {
		return createErrorMessage(102006, propKeyPath);
	}

	// ~ Service Registry
	// ---------------------------------------------------------------------

	public ErrorMessage lookupServiceFailed(String serviceName) {
		return createErrorMessage(103000, serviceName);
	}

	public ErrorMessage serviceNotFound(String serviceName) {
		return createErrorMessage(103001, serviceName);
	}

	public ErrorMessage unregisteredService(String serviceName) {
		return createErrorMessage(103002, serviceName);
	}

	// ~ Transformation
	// ------------------------------------------------------------------------

	public ErrorMessage transformFailed(String srcType, String toType, String details) {
		return createErrorMessage(104000, srcType, toType, details);
	}

	// ~ JPA DAO
	// -------------------------------------------------------------------------------

	public ErrorMessage argsNullJpa(String callMethod) {
		return createErrorMessage(300000, callMethod);
	}

	public ErrorMessage persistenceJpa(String callMethod, String details) {
		return createErrorMessage(300001, callMethod, details);
	}

	public ErrorMessage illegalStateJpa(String callMethod, String details) {
		return createErrorMessage(300002, callMethod, details);
	}

	public ErrorMessage illegalargsJpa(String callMethod, String details) {
		return createErrorMessage(300003, callMethod, details);
	}

	public ErrorMessage sqlNullJpa(String callMethod) {
		return createErrorMessage(300004, callMethod);
	}

	// ~ Core Repository
	// -------------------------------------------------------------------------

	public ErrorMessage argsNullRepo(String callMethod, String arg) {
		return createErrorMessage(350000, callMethod);
	}

	public ErrorMessage existingDomainAddRepo(Object domain) {
		return createErrorMessage(350001, domain);
	}

	public ErrorMessage nonExistingDomainUpdateRepo() {
		return createErrorMessage(350002);
	}

	// ~ Protocol
	// --------------------------------------------------------------------------------

	public ErrorMessage failedEncodedWithBase64() {
		return createErrorMessage(400000);
	}

	public ErrorMessage invalidCRC() {
		return createErrorMessage(400001);
	}

	public ErrorMessage unsupportedEncryptionTypeTcp(int encryptionType) {
		return createErrorMessage(400002, encryptionType);
	}

	public ErrorMessage unsupportedCompressionTypeTcp(int compressionType) {
		return createErrorMessage(400003, compressionType);
	}

	public ErrorMessage invalidCmdCode(String cmdCode) {
		return createErrorMessage(400004, cmdCode);
	}

	// ~ Validation
	// --------------------------------------------------------------------------------

	public ErrorMessage invalidSessionID(String sid, String appId) {
		return createErrorMessage(500000, sid, appId);
	}

	public ErrorMessage invalidCSRFToken(String csrfToken, String appId) {
		return createErrorMessage(500001, csrfToken, appId);
	}

	public ErrorMessage invalidAuthToken(String authToken, String appId) {
		return createErrorMessage(500002, authToken, appId);
	}

	public ErrorMessage mandatoryApplicationID() {
		return createErrorMessage(500003);
	}

	/**
	 *
	 * 1002 ： [{0}]格式有误
	 * 
	 * @return
	 */
	public ErrorMessage badFormatter(String string) {
		return createErrorMessage(1002, string);
	}

	// ~ ACL
	// --------------------------------------------------------------------------------

	public ErrorMessage nullSecurityContext() {
		return createErrorMessage(600000);
	}

	public ErrorMessage authenticateFailed(String details) {
		return createErrorMessage(600001, details);
	}

	public ErrorMessage securityProviderNotFound() {
		return createErrorMessage(600002);
	}

	public ErrorMessage unKnowAuthentication(String authenticationName) {
		return createErrorMessage(600003, authenticationName);
	}

	public ErrorMessage accessDenied(String authorization) {
		return createErrorMessage(600004, authorization);
	}

	// ~ Cryption
	// ---------------------------------------------------------------------------

	public ErrorMessage failedToReadKeyFile(String keyFile) {
		return createErrorMessage(700000, keyFile);
	}

	public ErrorMessage failedToEncryptData(String algorithm) {
		return createErrorMessage(700001, algorithm);
	}

	public ErrorMessage failedToDecryptData(String algorithm) {
		return createErrorMessage(700002, algorithm);
	}

	// ~ Compression
	// -------------------------------------------------------------------------

	public ErrorMessage failedToCompressData(String algorithm) {
		return createErrorMessage(750000, algorithm);
	}

	public ErrorMessage failedToUncompressData(String algorithm) {
		return createErrorMessage(750001, algorithm);
	}

	@Override
	protected String bundlePrefix() {
		return CORE_ERROR_BUNDLE_PREFIX;
	}

}