package com.tech.integrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest // Loads the full Spring context for testing
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test") // Uses application-test.properties
public class BaseDatabaseTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setupDatabase() throws SQLException {
        // Load schema and initial data
//        loadSQLFile("sql/clearDb.sql"); // clear db

    }

    private void loadSQLFile(String filePath) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(filePath));
        }
    }
}
