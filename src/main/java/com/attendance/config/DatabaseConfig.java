package com.attendance.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

@Configuration
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000; // 5 seconds
    private static boolean useInMemoryDatabase = false;
    
    @Autowired
    private Environment env;
    
    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Primary
    @Bean
    public DataSource dataSource() {
        // Try the MySQL connection first
        DataSource dataSource = tryMySQLConnection();
        
        // If MySQL connection failed, use H2 in-memory database
        if (dataSource == null) {
            dataSource = createInMemoryDatabase();
        }
        
        // Wrap with lazy connection proxy
        return new LazyConnectionDataSourceProxy(dataSource);
    }
    
    private DataSource tryMySQLConnection() {
        String url = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        
        logger.info("Configuring MySQL database connection with url: {}", url);
        
        // Kiểm tra thông tin cơ sở dữ liệu đã được cài đặt
        if (url == null || username == null) {
            logger.error("Database connection information is missing. Please check your .env file.");
            return null;
        }
        
        // Configure HikariCP with optimized settings for "Too many connections" scenario
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        
        // Very conservative connection pool settings
        config.setMaximumPoolSize(2); // Even more reduced
        config.setMinimumIdle(0);     // No idle connections when not in use
        config.setIdleTimeout(10000); // How long a connection can remain idle - reduced to 10 seconds
        config.setConnectionTimeout(5000); // 5 seconds wait for connection
        config.setMaxLifetime(600000); // Max lifetime of a connection - 10 minutes
        
        // Add specific MySQL settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        // Create data source with retry mechanism
        HikariDataSource dataSource = null;
        SQLException lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                logger.info("Attempt {} to connect to MySQL database", attempt);
                dataSource = new HikariDataSource(config);
                
                // Test connection
                try (Connection connection = dataSource.getConnection()) {
                    if (connection.isValid(5)) {
                        logger.info("Successfully connected to MySQL database on attempt {}", attempt);
                        return dataSource;
                    }
                }
            } catch (SQLException e) {
                lastException = e;
                if (dataSource != null) {
                    dataSource.close();
                    dataSource = null;
                }
                
                if (e.getMessage().contains("Too many connections")) {
                    logger.warn("Too many connections error on attempt {}, waiting before retry...", attempt);
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } else {
                    // For other errors, log and continue to next attempt
                    logger.error("Failed to connect to MySQL database: {}", e.getMessage());
                }
            }
        }
        
        // If all attempts failed
        if (dataSource == null) {
            logger.error("Failed to connect to MySQL database after {} attempts", MAX_RETRY_ATTEMPTS);
            if (lastException != null) {
                logger.error("Last error was: {}", lastException.getMessage());
            }
            return null; // Signal for fallback to H2
        }
        
        return dataSource;
    }
    
    private DataSource createInMemoryDatabase() {
        logger.warn("Failed to connect to MySQL database. Falling back to in-memory H2 database for development purposes.");
        useInMemoryDatabase = true;
        
        try {
            // Create an in-memory H2 database
            DataSource h2DataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("attendance_db_inmemory")
                .build();
            
            logger.info("Successfully created H2 in-memory database as fallback");
            
            // We need to display a clear warning about using in-memory database
            logger.warn("***************************************************************");
            logger.warn("* ATTENTION: Using in-memory H2 database instead of MySQL!    *");
            logger.warn("* This is a temporary solution for development/testing only.  *");
            logger.warn("* All data will be lost when the application restarts.        *");
            logger.warn("* Please resolve your MySQL connection issues for production. *");
            logger.warn("***************************************************************");
            
            return h2DataSource;
            
        } catch (Exception e) {
            logger.error("Failed to create in-memory H2 database: {}", e.getMessage());
            throw new RuntimeException("Could not create in-memory database fallback", e);
        }
    }
    
    /**
     * Indicates if the application is currently using the in-memory database fallback
     * @return true if using H2 in-memory database, false if using MySQL
     */
    public static boolean isUsingInMemoryDatabase() {
        return useInMemoryDatabase;
    }
}