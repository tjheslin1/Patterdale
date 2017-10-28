package staticanalysis;

import io.github.tjheslin1.westie.Violation;
import io.github.tjheslin1.westie.WestieAnalyser;
import io.github.tjheslin1.westie.WestieRegexes;
import io.github.tjheslin1.westie.gitissue.GitIssueAnalyser;
import io.github.tjheslin1.westie.infrastructure.GitIssues;
import io.github.tjheslin1.westie.todostructure.TodosStructureAnalyser;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

public class ConventionAnalysisTest implements WithAssertions {

    private static final Path WORKING_DIR = Paths.get(System.getProperty("user.dir"), "src");

    @Test
    public void todosMustReferenceAGitIssue() throws Exception {
        List<Violation> violations = new TodosStructureAnalyser(
                WestieRegexes.TODO_REGEX + "ISSUE-" + WestieRegexes.EXTRACT_NUMBER_REGEX)
                .checkAllTodosFollowExpectedStructure(WORKING_DIR);

        assertThat(violations).isEmpty();
    }

    @Test
    public void todosMustReferenceAGitIssueInOpenStateOnly() throws Exception {
        GitIssues gitIssues = new GitIssues("tjheslin1", "Westie");
        List<Violation> violations = new GitIssueAnalyser(gitIssues, "ISSUE-" + WestieRegexes.EXTRACT_NUMBER_REGEX)
                .todosAreInOpenState(WORKING_DIR, singletonList("ojdbc7.jar"));

        assertThat(violations).isEmpty();
    }

    @Test
    public void srcJavaFilesShouldStartWithOpenSourceLicenseDoc() throws Exception {
        List<Violation> violations = new WestieAnalyser()
                .analyseDirectory(WORKING_DIR.resolve("main").resolve("java"))
                .forJavaFiles().analyseFileContent(this::javaFileStartsWithLicense,
                        "All src java files must start with open source license header.");

        assertThat(violations).isEmpty();
    }

    @Test
    public void configurationConfigDocumentationIsUpToDate() throws Exception {
        String yamlContent = FileUtils.readFileToString(new File("src/test/resources/patterdale.yml"), UTF_8);

        List<Violation> violations = new WestieAnalyser().analyseFile(Paths.get("docs/configuration.md"))
                .analyseFileContent(documentation -> !documentation.contains(yamlContent),
                        "Expected configuration.md to contain the contents of patterdale.yml");

        assertThat(violations).isEmpty();
    }

    @Test
    public void configurationPasswordDocumentationIsUpToDate() throws Exception {
        String yamlContent = FileUtils.readFileToString(new File("src/test/resources/passwords.yml"), UTF_8);

        List<Violation> violations = new WestieAnalyser().analyseFile(Paths.get("docs/configuration.md"))
                .analyseFileContent(documentation -> !documentation.contains(yamlContent),
                        "Expected configuration.md to contain the contents of passwords.yml");

        assertThat(violations).isEmpty();
    }

    @Test
    public void correctDockerImageVersionIsReferenced() throws Exception {
        String versionLine = FileUtils.readLines(new File("build.gradle"), UTF_8)
                .stream().filter(line -> line.startsWith("version '"))
                .findFirst().get();
        String buildVersion = versionLine.substring("version '".length(), versionLine.length() - 1);

        String readmeDockerRunCommand = FileUtils.readLines(new File("README.md"), UTF_8)
                .stream().filter(line -> line.contains("docker run -d -p 7000:7000"))
                .findFirst().get();

        String configurationDockerRunCommand = FileUtils.readLines(new File("docs/configuration.md"), UTF_8)
                .stream().filter(line -> line.contains("docker run -d -p 7000:7000"))
                .findFirst().get();

        assertThat(readmeDockerRunCommand)
                .describedAs("README.md's docker run command should reference build.gradle's app version")
                .contains(buildVersion);

        assertThat(configurationDockerRunCommand)
                .describedAs("docs/configuration.md's docker run command should reference build.gradle's app version")
                .contains(buildVersion);
    }

    private boolean javaFileStartsWithLicense(String fileContent) {
        return !fileContent.startsWith(LICENSE);
    }

    private static final String LICENSE = format("/*\n" +
            " * Copyright %d Thomas Heslin <tjheslin1@gmail.com>.\n" +
            " *\n" +
            " * This file is part of Patterdale-jvm.\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *  http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */\n", ZonedDateTime.now().getYear());
}
