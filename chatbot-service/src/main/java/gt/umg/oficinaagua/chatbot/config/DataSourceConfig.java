package gt.umg.oficinaagua.chatbot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public HikariDataSource pgvectorDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String usuario,
            @Value("${spring.datasource.password}") String contrasena) {
        return crear("pgvector-pool", url, usuario, contrasena, 10);
    }

    @Bean
    public HikariDataSource replicaDataSource(
            @Value("${chatbot.replica.url}") String url,
            @Value("${chatbot.replica.username}") String usuario,
            @Value("${chatbot.replica.password}") String contrasena) {
        HikariDataSource dataSource = crear("replica-pool", url, usuario, contrasena, 3);
        dataSource.setReadOnly(true);
        dataSource.setInitializationFailTimeout(-1);
        dataSource.setConnectionTimeout(5000);
        return dataSource;
    }

    @Bean
    public JdbcClient replicaJdbcClient(@Qualifier("replicaDataSource") HikariDataSource replicaDataSource) {
        return JdbcClient.create(replicaDataSource);
    }

    private static HikariDataSource crear(String nombrePool, String url, String usuario,
                                          String contrasena, int maximoConexiones) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName(nombrePool);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(usuario);
        dataSource.setPassword(contrasena);
        dataSource.setMaximumPoolSize(maximoConexiones);
        return dataSource;
    }
}
