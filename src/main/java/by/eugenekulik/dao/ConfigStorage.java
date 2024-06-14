package by.eugenekulik.dao;

import java.util.Optional;

public interface ConfigStorage {


  Optional<Object> getConfig(String key);

  void setConfig(String key, Object value);

}
