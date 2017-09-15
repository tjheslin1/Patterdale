package staticanalysis;

import io.github.tjheslin1.westie.Violation;
import io.github.tjheslin1.westie.WestieRegexes;
import io.github.tjheslin1.westie.gitissue.GitIssueAnalyser;
import io.github.tjheslin1.westie.infrastructure.GitIssues;
import io.github.tjheslin1.westie.todostructure.TodosStructureAnalyser;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
}
