package rainbow.db.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDb2Blob {

    private static DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
//        dataSource = new SimpleDriverDataSource(new DB2Driver(),
//                "jdbc:db2://10.14.3.9:50000/v3db2:user=v3db2;password=password;");
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.getTransactionManager().beginTransaction();
    }

    @AfterEach
    public void tearDown() throws Exception {
        jdbcTemplate.getTransactionManager().rollback();
    }

    @Test
    public void testByteArray() {
        String content = "就是测测没啥关系";
        byte[] buff = content.getBytes();
        int result = jdbcTemplate.update("insert into TEST_BLOB values(?, ?)", 1, buff);
        assertEquals(1, result);

        List<String> data = jdbcTemplate.query("select NR from TEST_BLOB where id=1", new RowMapper<String>() {
            @Override
            public String mapRow(final ResultSet rs, int rowNum) throws SQLException {
                try {
                    final InputStream in = rs.getBinaryStream(1);
                    byte[] b = new byte[in.available()];
                    in.read(b);
                    return new String(b);
                } catch (IOException e) {
                    return null;
                }
            }
        });
        assertEquals(content, data.get(0));
    }
    
    @Test
    public void testInputStream() {
        String content = "就是测测没啥关系";
        byte[] buff = content.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(buff);
        
        int result = jdbcTemplate.update("insert into TEST_BLOB values(?, ?)", 1, bais);
        assertEquals(1, result);
        
        List<String> data = jdbcTemplate.query("select NR from TEST_BLOB where id=1", new RowMapper<String>() {
            @Override
            public String mapRow(final ResultSet rs, int rowNum) throws SQLException {
                try {
                    final InputStream in = rs.getBinaryStream(1);
                    byte[] b = new byte[in.available()];
                    in.read(b);
                    return new String(b);
                } catch (IOException e) {
                    return null;
                }
            }
        });
        assertEquals(content, data.get(0));
    }
}
