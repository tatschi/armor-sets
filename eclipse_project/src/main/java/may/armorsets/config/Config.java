package may.armorsets.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import may.armorsets.ArmorSets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {

	private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec config;
	
	
	static {
		
		ConfigOptions.init(builder);
		config = builder.build();
	}
	
	
	public static void loadConfig(ForgeConfigSpec config, String configPath) {
		ArmorSets.LOGGER.info("Loading config from " + configPath);
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(configPath)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		ArmorSets.LOGGER.info("Completed loading config from " + configPath);
		config.setConfig(file);
	}
}
