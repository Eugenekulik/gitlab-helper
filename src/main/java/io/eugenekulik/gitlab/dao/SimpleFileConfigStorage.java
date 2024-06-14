package io.eugenekulik.gitlab.dao;

import io.eugenekulik.gitlab.exception.LoadingConfigurationError;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimpleFileConfigStorage implements ConfigStorage {

  @Value("${config.file.path}")
  private String storageFilePath;

  private Map<String, Object> cache;


  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    File file = new File(storageFilePath);
    if (!file.exists()) {
      try {
        if(file.createNewFile()) {
          cache = new HashMap<>();
          saveConfig();
        }
      } catch (IOException e) {
        throw new LoadingConfigurationError(
            "Could not create file with configuration", e);
      }
    } else {
      try(FileInputStream in = new FileInputStream(file)) {
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        cache = (Map<String, Object>) objectInputStream.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new LoadingConfigurationError("could not load configuration", e);
      }
    }
  }


  @Override
  public Optional<Object> getConfig(String key) {
    return Optional.ofNullable(cache.get(key));
  }

  @Override
  public void setConfig(String key, Object value) {
    cache.put(key, value);
    saveConfig();
  }

  private void saveConfig() {
    try (FileOutputStream fileOut = new FileOutputStream(storageFilePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(cache);
      log.info("configuration save to file successfully");
    } catch (IOException e) {
      log.error("Error occurred while saving configuration", e);
    }
  }
}
