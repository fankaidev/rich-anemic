package net.fklj.richanemic.adm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class TestDbConfiguration {

    @Bean
    @Primary
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        builder.addScript("schema.sql");
        return builder.setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public NamedParameterJdbcOperations db(
            DataSource dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
