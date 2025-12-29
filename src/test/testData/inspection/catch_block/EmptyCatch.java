import java.io.IOException;
import java.sql.SQLException;

public class EmptyCatch {
    public void emptyBlock() {
        try {
            riskyOperation();
        } <weak_warning descr="LogSensei: Catch block missing ERROR log">catch</weak_warning> (Exception e)  {
            // Empty block - action expected
        }
    }

    public void withException() {
        try {
            riskyOperation();
        } <weak_warning descr="LogSensei: Catch block missing ERROR log">catch</weak_warning> (Exception e){
            throw new RuntimeException("IO failed", e); // No log - action expected
        }
    }

    public void withMultiException() {
        try {
            riskyIOOperation();
            riskySQLOperation();
        } <weak_warning descr="LogSensei: Catch block missing ERROR log">catch</weak_warning> (IOException | SQLException e){
            // Empty block - action expected
        }
    }

    // Helper
    private void riskyOperation() throws Exception {
        throw new Exception("test");
    }

    private void riskySQLOperation() throws SQLException {
        throw new SQLException("test");
    }

    private void riskyIOOperation() throws IOException {
        throw new IOException("test");
    }
}