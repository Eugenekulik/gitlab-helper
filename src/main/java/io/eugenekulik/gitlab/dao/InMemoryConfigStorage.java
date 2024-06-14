package io.eugenekulik.gitlab.dao;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryConfigStorage implements ConfigStorage {

  Map<String, Object> configurations = new ConcurrentHashMap<>();

  @Override
  public Optional<Object> getConfig(String key) {
    return Optional.ofNullable(configurations.get(key));
  }

  @Override
  public void setConfig(String key, Object value) {
    configurations.put(key, value);
  }
}
