package io.roach.spring.pooling;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private HikariDataSource dataSource;

    @GetMapping
    public ResponseEntity<RepresentationModel> index() {
        RepresentationModel index = new RepresentationModel();

        index.add(linkTo(methodOn(getClass())
                .databaseMetadata()).withRel("database-info")
                .withTitle("Database and JDBC driver metadata"));

        index.add(linkTo(methodOn(getClass())
                .getConnectionPoolSize())
                .withRel("pool-size")
                .withTitle("Connection pool size"));

        index.add(linkTo(methodOn(getClass())
                .getConnectionPoolConfig())
                .withRel("pool-config")
                .withTitle("Connection pool config"));

        index.add(Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .pathSegment("actuator")
                        .buildAndExpand()
                        .toUriString())
                .withRel("actuators")
                .withTitle("Spring boot actuators"));

        Arrays.asList("hikaricp.connections", "hikaricp.connections.acquire", "hikaricp.connections.active",
                "hikaricp.connections.idle", "hikaricp.connections.max", "hikaricp.connections.usage",
                "http.server.requests", "jdbc.connections.active", "jdbc.connections.idle", "jdbc.connections.max",
                "jdbc.connections.min", "jvm.threads.live", "jvm.threads.peak", "process.cpu.usage", "system.cpu.count",
                "system.cpu.usage", "system.load.average.1m").forEach(key -> {
            index.add(
                    Link.of(ServletUriComponentsBuilder.fromCurrentContextPath().pathSegment(
                                            "actuator", "metrics", key)
                                    .buildAndExpand().toUriString()).withRel("actuators")
                            .withTitle("Metrics endpoint"));
        });

        return ResponseEntity.ok(index);
    }

    @GetMapping(value = "/database-info")
    public ResponseEntity<Map<String, Object>> databaseMetadata() {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("databaseVersion", databaseVersion());

        Connection connection = null;
        try {
            connection = DataSourceUtils.doGetConnection(dataSource);
            DatabaseMetaData metaData = connection.getMetaData();

            properties.put("databaseProductName", metaData.getDatabaseProductName());
            properties.put("databaseMajorVersion", metaData.getDatabaseMajorVersion());
            properties.put("databaseMinorVersion", metaData.getDatabaseMinorVersion());
            properties.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            properties.put("driverMajorVersion", metaData.getDriverMajorVersion());
            properties.put("driverMinorVersion", metaData.getDriverMinorVersion());
            properties.put("driverName", metaData.getDriverName());
            properties.put("driverVersion", metaData.getDriverVersion());
            properties.put("maxConnections", metaData.getMaxConnections());
            properties.put("defaultTransactionIsolation", metaData.getDefaultTransactionIsolation());
            properties.put("transactionIsolation", connection.getTransactionIsolation());
            properties.put("transactionIsolationName",
                    ConnectionProviderInitiator.toIsolationNiceName(connection.getTransactionIsolation()));
        } catch (SQLException ex) {
            // Ignore
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return ResponseEntity.ok(properties);
    }

    private String databaseVersion() {
        try {
            return new JdbcTemplate(dataSource).queryForObject("select version()", String.class);
        } catch (DataAccessException e) {
            return "unknown";
        }
    }

    @GetMapping(value = "/pool-size")
    public ResponseEntity<ConnectionPoolSize> getConnectionPoolSize() {
        HikariPoolMXBean mxBean = dataSource.getHikariPoolMXBean();
        return ResponseEntity.ok(ConnectionPoolSize.from(mxBean)
                .add(linkTo(methodOn(getClass())
                        .getConnectionPoolConfig())
                        .withRel("pool-config"))
                .add(linkTo(methodOn(getClass())
                        .getConnectionPoolSize())
                        .withSelfRel()));
    }

    @GetMapping(value = "/pool-config")
    public ResponseEntity<ConnectionPoolConfig> getConnectionPoolConfig() {
        HikariConfigMXBean mxConfigBean = dataSource.getHikariConfigMXBean();
        return ResponseEntity.ok(ConnectionPoolConfig.from(mxConfigBean)
                .add(linkTo(methodOn(getClass())
                        .getConnectionPoolSize())
                        .withRel("pool-size"))
                .add(linkTo(methodOn(getClass())
                        .getConnectionPoolConfig())
                        .withSelfRel()));
    }

    public static class ConnectionPoolSize extends RepresentationModel<ConnectionPoolSize> {
        public static ConnectionPoolSize from(HikariPoolMXBean bean) {
            ConnectionPoolSize instance = new ConnectionPoolSize();
            instance.activeConnections = bean.getActiveConnections();
            instance.idleConnections = bean.getIdleConnections();
            instance.threadsAwaitingConnection = bean.getThreadsAwaitingConnection();
            instance.totalConnections = bean.getTotalConnections();
            return instance;
        }

        public int activeConnections;

        public int idleConnections;

        public int threadsAwaitingConnection;

        public int totalConnections;

        public int getActiveConnections() {
            return activeConnections;
        }

        public int getIdleConnections() {
            return idleConnections;
        }

        public int getThreadsAwaitingConnection() {
            return threadsAwaitingConnection;
        }

        public int getTotalConnections() {
            return totalConnections;
        }
    }

    public static class ConnectionPoolConfig extends RepresentationModel<ConnectionPoolConfig> {
        public static ConnectionPoolConfig from(HikariConfigMXBean bean) {
            ConnectionPoolConfig instance = new ConnectionPoolConfig();
            instance.connectionTimeout = bean.getConnectionTimeout();
            instance.poolName = bean.getPoolName();
            instance.idleTimeout = bean.getIdleTimeout();
            instance.leakDetectionThreshold = bean.getLeakDetectionThreshold();
            instance.maximumPoolSize = bean.getMaximumPoolSize();
            instance.maxLifetime = bean.getMaxLifetime();
            instance.minimumIdle = bean.getMinimumIdle();
            instance.validationTimeout = bean.getValidationTimeout();
            return instance;
        }

        public long connectionTimeout;

        public String poolName;

        public long idleTimeout;

        public long leakDetectionThreshold;

        public int maximumPoolSize;

        public long maxLifetime;

        public int minimumIdle;

        public long validationTimeout;

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public String getPoolName() {
            return poolName;
        }

        public long getIdleTimeout() {
            return idleTimeout;
        }

        public long getLeakDetectionThreshold() {
            return leakDetectionThreshold;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public long getMaxLifetime() {
            return maxLifetime;
        }

        public int getMinimumIdle() {
            return minimumIdle;
        }

        public long getValidationTimeout() {
            return validationTimeout;
        }
    }

}
