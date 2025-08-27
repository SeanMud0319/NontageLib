package top.nontage.nontagelib.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public abstract class BaseConfig {
    private static final DumperOptions options = new DumperOptions();
    private static final Yaml yaml;

    static {
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    private File file;

    void initFile(File file) {
        this.file = file;
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(file)) {
                Map<String, Object> map = ConfigUtils.toMap(this);
                yaml.dump(map, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        if (!file.exists()) {
            save();
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Map<String, Object> map = yaml.load(reader);
            if (map != null) {
                ConfigUtils.applyValues(this, map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
