package by.eugenekulik.config;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitlabConfig {

  @Value("${gitlab.url}")
  private String gitlabUrl;

  @Value("${gitlab.token}")
  private String gitlabToken;

  @Bean
  public GitLabApi gitLabApi(){
    return new GitLabApi(gitlabUrl, gitlabToken);
  }
}
