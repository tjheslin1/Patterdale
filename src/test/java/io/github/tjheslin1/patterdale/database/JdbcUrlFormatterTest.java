package io.github.tjheslin1.patterdale.database;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.tjheslin1.patterdale.database.JdbcUrlFormatter.databaseUrlWithCredentials;

public class JdbcUrlFormatterTest implements WithAssertions {

    private static final String ORIGINAL_URL_WITHOUT_CREDENTIALS = "jdbc:oracle:thin:@localhost:1522:xe";
    private static final String URL_WITH_CREDENTIALS = "jdbc:oracle:thin:system/oracle@localhost:1522:xe";

    @Test
    public void shouldFormatUrlWithoutCredentialsToIncludeThem() throws Exception {
        String formattedUrl = databaseUrlWithCredentials(ORIGINAL_URL_WITHOUT_CREDENTIALS, "system", "oracle");

        assertThat(formattedUrl).isEqualTo(URL_WITH_CREDENTIALS);
    }

    @Test
    public void shouldNotChangeUrlWithCredentials() throws Exception {
        String formattedUrl = databaseUrlWithCredentials(URL_WITH_CREDENTIALS, "system", "oracle");

        assertThat(formattedUrl).isEqualTo(URL_WITH_CREDENTIALS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBlowUpIfInvalidDatabaseUrlFormat() throws Exception {
        databaseUrlWithCredentials("invalidDatabaseUrl", "system", "oracle");
    }
}