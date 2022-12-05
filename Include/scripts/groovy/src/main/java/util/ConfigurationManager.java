package src.main.java.util;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.hamcrest.Matchers;

import java.io.File;
import java.util.*;

import static org.apache.commons.configuration.AbstractConfiguration.getDefaultListDelimiter;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Configuration manager class. Singleton with early initialization.
 * <p>
 * This class loads file provided by system property
 * <code>application.properties.file</code> (Default value is
 * "resources/application.properties"). Also loads all property files form
 * <code>test.props.dir</code>(default value is "resources") if
 * <code>resources.load.subdirs</code> flag is 1.
 * <p>

 */
public class ConfigurationManager {
	// early initialization
	static final Log log = getLog(ConfigurationManager.class);
	private static final ConfigurationManager INSTANCE = new ConfigurationManager();
	private static final String ENV_RESOURCES = "env.resources";
	private static final String DEFAULT_RESOURCES = "resources";

	/**
	 * Private constructor, prevents instantiation from other classes
	 */
	private ConfigurationManager() {
		AbstractConfiguration.setDefaultListDelimiter(';');
	}

	public static ConfigurationManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Method to add properties from system.getproperties, application.properties.file and environment.resources file
	 */
	private static InheritableThreadLocal<PropertyUtil> localProps =
			new InheritableThreadLocal<PropertyUtil>() {
				@Override
				protected PropertyUtil initialValue() {
					PropertyUtil p = new PropertyUtil(
							System.getProperty("application.properties.file",
									"resources/application.properties"));

					String[] resources = p.getStringArray(ENV_RESOURCES, DEFAULT_RESOURCES);
					for (String resource : resources) {
						addBundle(p, resource);
					}
					ConfigurationListener cl = new PropertyConfigurationListener();
					p.addConfigurationListener(cl);
					return p;
				}

				@Override
				protected PropertyUtil childValue(PropertyUtil parentValue) {
					PropertyUtil cp = new PropertyUtil(parentValue);
					ConfigurationListener cl = new PropertyConfigurationListener();
					cp.addConfigurationListener(cl);
					return cp;
				}

			};

	/**
	 * @param defaultVal optional
	 * @return string value of the key
	 */
	public static String getKeyStringVal(String key, String defaultVal) {

		String value = System.getProperty(key);
		if(value != null && !value.isEmpty())
		{
			return value;
		}

		value = System.getenv(key);
		if(value != null && !value.isEmpty())
		{
			return value;
		}

		value = ConfigurationManager.getBundle().getString(key);

		if(value != null && !value.isEmpty())
		{
			return value;
		}

		if(defaultVal != null )
		{
			return defaultVal;
		}

		return "";
	}

	/**
	 * @param key the key to retrieve
	 * @param defaultVal optional
	 * @return integer value of the key or 0 if key is not an integer
	 */
	public static int getKeyIntVal(String key, int defaultVal) {
		int rc = 0;
		try {
			rc = Integer.parseInt(getKeyStringVal(key, ""));
		} catch (NumberFormatException e) {
			rc = defaultVal;
		}

		return rc;
	}
	/**
	 * @param key the key to retrieve
	 * @param defaultVal optional
	 * @return boolean value of the key
	 */
	public static boolean getKeyBooleanVal(String key, boolean defaultVal) {
		try {
			String sVal = ConfigurationManager.getKeyStringVal(key, "").trim();
			return StringUtils.isNumeric(sVal)
					? (Integer.parseInt(sVal) != 0)
					: Boolean.parseBoolean(sVal);
		} catch (NumberFormatException e) {
			// just ignore
		}

		return defaultVal;
	}

	/**
	 * To add local resources.
	 *
	 * @param fileOrDir
	 */
	public static void addBundle(String fileOrDir) {
		ConfigurationManager.addBundle(getBundle(), fileOrDir);
	}

	public static void addAll(Map<String, ?> props) {
		ConfigurationManager.getBundle().addAll(props);
	}

	public static PropertyUtil getBundle() {
		return ConfigurationManager.localProps.get();
	}

	/**
	 * @param p
	 * @param fileOrDir
	 */
	private static void addBundle(PropertyUtil p, String fileOrDir) {
		String localResources = p.getString("local.resources", p.getString("env.local.resources", DEFAULT_RESOURCES));
		fileOrDir = p.getSubstitutor().replace(fileOrDir);
		File resourceFile = new File(fileOrDir);
		/**
		 * will reload existing properties value(if any) if the last loaded
		 * dir/file is not the current one. case: suit-1 default, suit-2 :
		 * s2-local, suit-3: default Here after suit-2 you need to reload
		 * default.
		 */
		if (!localResources.equalsIgnoreCase(resourceFile.getAbsolutePath())) {
			p.addProperty("local.reasources", resourceFile.getAbsolutePath());
			if (resourceFile.exists()) {
				if (resourceFile.isDirectory()) {
					boolean loadSubDirs = p.getBoolean("resources.load.subdirs", true);
					File[] propFiles = FileUtil.listFilesAsArray(resourceFile,
							".properties", StringComparator.Suffix, loadSubDirs);
					log.info("Resource dir: " + resourceFile.getAbsolutePath()
							+ ". Found property files to load: " + propFiles.length);
					PropertyUtil p1 = new PropertyUtil();
					p1.load(propFiles);
					p.copy(p1);

					propFiles = FileUtil.listFilesAsArray(resourceFile, ".xml",
							StringComparator.Suffix, loadSubDirs);
					log.info("Resource dir: " + resourceFile.getAbsolutePath()
							+ ". Found property files to load: " + propFiles.length);

					p1 = new PropertyUtil();
					p1.load(propFiles);
					p.copy(p1);

				} else {
					tryLoadPropertyFile(p, fileOrDir, resourceFile);
				}
			} else {
				log.error(resourceFile.getAbsolutePath() + " not exist!");
			}
		}
	}

	private static void tryLoadPropertyFile(PropertyUtil p, String fileOrDir, File resourceFile)
	{
		try {
			if (fileOrDir.endsWith(".properties") || fileOrDir.endsWith(".xml") ){
				p.load(new File[]{resourceFile});
			}
		} catch (Exception e) {
			log.error("Unable to load " + resourceFile.getAbsolutePath() + "!", e);
		}
	}

    private static class PropertyConfigurationListener implements ConfigurationListener {
		String oldValue;

		private static void loadFileOrDir(PropertyUtil p1, String local, String fileOrDir, File resourceFile, boolean loadSubDirs) {
			if (resourceFile.isDirectory()) {
				File[] propFiles = FileUtil.listFilesAsArray(resourceFile, "." + local, StringComparator.Suffix, loadSubDirs);
				p1.load(propFiles);

			} else {
				try {
					if (fileOrDir.endsWith(local)) {
						p1.load(fileOrDir);
					}
				} catch (Exception e) {
					log.error("Unable to load " + resourceFile.getAbsolutePath() + "!", e);
				}
			}
		}

		private static void addLocal(PropertyUtil p, String local, String fileOrDir) {
			String defaultLocal = p.getString(ApplicationProperties.DEFAULT_LOCALE.getKey(), "");
			File resourceFile = new File(fileOrDir);
			/**
			 * will reload existing properties value(if any) if the last loaded
			 * dir/file is not the current one.
			 */
			boolean loadSubDirs = p.getBoolean("resources.load.subdirs", true);

			if (resourceFile.exists()) {
				PropertyUtil p1 = new PropertyUtil();
				p1.setEncoding(p.getString(ApplicationProperties.LOCALE_CHAR_ENCODING.getKey(), "UTF-8"));
				loadFileOrDir(p1, local, fileOrDir, resourceFile, loadSubDirs);

				if (local.equalsIgnoreCase(defaultLocal)) {
					p.copy(p1);
				} else {
					Iterator<?> keyIter = p1.getKeys();
					Configuration localSet = p.subset(local);
					while (keyIter.hasNext()) {
						String key = (String) keyIter.next();
						localSet.addProperty(key, p1.getObject(key));
					}
				}

			} else {
				log.error(resourceFile.getAbsolutePath() + " not exist!");
			}
		}

		private void setClearEvent(ConfigurationEvent event)
		{
			if ((event.getType() == AbstractConfiguration.EVENT_CLEAR_PROPERTY
					|| event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY)
					&& event.isBeforeUpdate()) {
				oldValue = String.format("%s", getBundle().getObject(event.getPropertyName()));
			}
		}

		private void addLocalConfig(ConfigurationEvent event, String key)
		{
			if (key.equalsIgnoreCase(ApplicationProperties.DEFAULT_LOCALE.getKey())) {
				String[] resources = getBundle().getStringArray(ENV_RESOURCES, DEFAULT_RESOURCES);
				for (String resource : resources) {
					String fileOrDir = getBundle().getSubstitutor().replace(resource);
					addLocal(getBundle(), (String) event.getPropertyValue(),
							fileOrDir);
				}
			}
		}

		private void addEnvironmentResources(ConfigurationEvent event, String key, Object value)
		{
			String[] bundles = null;
			if (key.equalsIgnoreCase(ENV_RESOURCES)) {

				if (event.getPropertyValue() instanceof ArrayList<?>) {
					ArrayList<String> bundlesArray = ((ArrayList<String>) event.getPropertyValue());
					bundles = bundlesArray.toArray(new String[bundlesArray.size()]);
				} else {
					String resourcesBundle = (String) value;
					if (isNotBlank(resourcesBundle)) {
						bundles = resourcesBundle.split(String
								.valueOf(getDefaultListDelimiter()));
					}
				}
				if (bundles != null  && bundles.length > 0) {
					for (String res : bundles) {
						log.info("Adding resources from: " + res);
						ConfigurationManager.addBundle(res);
					}
				}
			}
		}

		@Override
		public void configurationChanged(ConfigurationEvent event) {

			setClearEvent(event);

			if ((event.getType() == AbstractConfiguration.EVENT_ADD_PROPERTY
					|| event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY)
					&& !event.isBeforeUpdate()) {
				String key = event.getPropertyName();
				Object value = event.getPropertyValue();
				if (oldValue != null && Matchers.equalTo(oldValue).matches(value)) {
					// do nothing
					return;
				}

				// Resource loading
				addEnvironmentResources(event, key, value);

				// Locale loading
				addLocalConfig(event, key);
			}
		}
	}

}
