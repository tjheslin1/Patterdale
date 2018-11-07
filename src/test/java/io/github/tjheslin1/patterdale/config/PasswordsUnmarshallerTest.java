package io.github.tjheslin1.patterdale.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import static java.lang.String.format;

public class PasswordsUnmarshallerTest implements WithAssertions, WithMockito {

    private final Logger logger = mock(Logger.class);

    private final PasswordsUnmarshaller passwordsUnmarshaller = new PasswordsUnmarshaller(logger);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void unmarshallsPasswordsFileIntoPasswords() {
        File passwordsFile = loadTestPasswordsFile();

        Passwords passwords = passwordsUnmarshaller.parsePasswords(passwordsFile);

        assertThat(passwords).isEqualTo(expectedPasswords());
    }

    @Test
    public void blowsUpIfPassWordsFileIsMalformed() throws Exception {
        File tempFile = temporaryFolder.newFile("passwords.yml");
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write("invalid content");
        fileWriter.flush();


        assertThatThrownBy(() -> passwordsUnmarshaller.parsePasswords(tempFile))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(format("Error occurred reading passwords file '%s'", tempFile.getName()))
                .hasCauseExactlyInstanceOf(JsonMappingException.class);
    }

    @SuppressWarnings("ConstantConditions")
    private File loadTestPasswordsFile() {
        URL url = this.getClass().getClassLoader().getResource("passwords.yml");
        return new File(url.getPath());
    }

    private Passwords expectedPasswords() {
        Passwords passwords = new Passwords();
        passwords.passwords = ImmutableMap.of(
                DATABASE_NAME_1, PASSWORD_VALUE_1,
                DATABASE_NAME_2, PASSWORD_VALUE_2);

        return passwords;
    }


    private static final String DATABASE_NAME_1 = "bobsDatabase";
    private static final String DATABASE_NAME_2 = "alicesDatabase";
    private static final String PASSWORD_VALUE_1 = "oracle";
    private static final String PASSWORD_VALUE_2 = "oracle";
}