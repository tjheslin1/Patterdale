package staticanalysis;

import io.github.tjheslin1.westie.Violation;
import io.github.tjheslin1.westie.WestieAnalyser;
import io.github.tjheslin1.westie.WestieRegexes;
import io.github.tjheslin1.westie.gitissue.GitIssueAnalyser;
import io.github.tjheslin1.westie.infrastructure.GitIssues;
import io.github.tjheslin1.westie.todostructure.TodosStructureAnalyser;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;
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
