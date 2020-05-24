package red.steady.richWidgets.application;

import java.io.File;
import java.io.IOException;

import red.steady.richWidgets.ConfigurationProperties;
import red.steady.richWidgets.utils.PropertiesFile;
import red.steady.richWidgets.utils.RichUtils;

public class RichApplication {

	private static final String UI_PROPERITES_FILE_NAME = "ui.properites";

	private ConfigurationProperties configuration_map;

	private final String applicationName;
	private final File persistenceDirectory;

	public RichApplication(String applicationName) {
		this(applicationName, new File(new File(System.getProperty("user.home")), applicationName));
	}

	public RichApplication(String applicationName, File persistenceDirectory) {
		super();

		RichUtils.checkNotEmptyParameter(applicationName, "applicationName");
		RichUtils.checkNotNullParameter(persistenceDirectory, "persistenceDirectory");

		this.applicationName = applicationName;
		this.persistenceDirectory = persistenceDirectory;
	}

	public File getFileInPersistenceDirectory(String file_path) {
		File file = new File(persistenceDirectory, file_path);

		file.getParentFile().mkdirs();

		return file;
	}

	public void loadUserConfiguration() {
		File config_file = getFileInPersistenceDirectory(UI_PROPERITES_FILE_NAME);

		PropertiesFile configuration_file = new PropertiesFile();
		if (config_file.exists() == false) {
			try {
				config_file.getParentFile().mkdirs();
				config_file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(
						"Failed to create initial user configuration file, \"" + config_file.getAbsolutePath() + "\".",
						e);
			}
		}
		configuration_file.loadFromFile(config_file);

		setConfigurationValues(configuration_file);
	}

	public void saveUserConfiguration() {
		((PropertiesFile) getConfigurationValues()).saveToFile();
	}

	public ConfigurationProperties getConfigurationValues() {
		RichUtils.checkNotNull(configuration_map);

		return configuration_map;
	}

	public void setConfigurationValues(ConfigurationProperties configuration_map) {
		this.configuration_map = configuration_map;
	}

}
