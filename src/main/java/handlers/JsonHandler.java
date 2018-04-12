package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Config;

import java.io.File;
import java.io.IOException;

public class JsonHandler {

    public Config getConfig(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Config config = mapper.readValue(file, Config.class);
            return config;
        } catch (IOException e) {
            return null;
        }
    }

    public void setConfig(Config config) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("config.json"), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
