package name.abhijitsarkar.javaee.userpref;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TestUserPreferenceApp extends UserPreferenceApp {
    public static void main(String[] args) throws Exception {
	SpringApplication.run(UserPreferenceApp.class, args);
    }

    @Bean
    @ConfigurationProperties("spring.oracle")
    public DataSource oracleDataSource() {
	return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate oracleJdbcTemplate() {
	return new JdbcTemplate(oracleDataSource());
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.h2")
    public DataSource h2DataSource() {
	return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public JdbcTemplate h2JdbcTemplate() {
	return new JdbcTemplate(h2DataSource());
    }
}
