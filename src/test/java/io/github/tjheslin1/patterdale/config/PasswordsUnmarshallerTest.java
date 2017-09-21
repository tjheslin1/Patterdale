package io.github.tjheslin1.patterdale.config;

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.File;
import java.net.URL;

public class PasswordsUnmarshallerTest implements WithAssertions, WithMockito {

    private final Logger logger = mock(Logger.class);

    private final PasswordsUnmarshaller passwordsUnmarshaller = new PasswordsUnmarshaller(logger);

    @Ignore
    @Test
    public void unmarshallsPasswordsFileIntoPasswords() throws Exception {
        File passwordsFile = loadTestPasswordsFile();

    }

    private File loadTestPasswordsFile() {
        URL url = this.getClass().getClassLoader().getResource("passwords.yml");
        return new File(url.getPath());
    }
}