package testutil;

import io.github.tjheslin1.patterdale.Patterdale;
import io.github.tjheslin1.patterdale.config.*;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Future;

import static io.github.tjheslin1.patterdale.Patterdale.initialDatabaseConnections;
import static io.github.tjheslin1.patterdale.config.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static java.util.stream.Collectors.toMap;

public class TestUtil {

    public static Patterdale startPatterdale(String configFile, String passwordsFile) {
        Logger logger = LoggerFactory.getLogger("application");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(configFile));

        Passwords passwords = new PasswordsUnmarshaller(logger)
                .parsePasswords(new File(passwordsFile));

        PatterdaleRuntimeParameters runtimeParameters = patterdaleRuntimeParameters(patterdaleConfig);
        Map<String, Future<DBConnectionPool>> futureConnections = initialDatabaseConnections(logger, passwords, runtimeParameters);

        Map<String, Probe> probesByName = runtimeParameters.probes().stream().collect(toMap(probe -> probe.name, probe -> probe));
        TypeToProbeMapper typeToProbeMapper = new TypeToProbeMapper(logger);

        Patterdale patterdale = new Patterdale(runtimeParameters, futureConnections, typeToProbeMapper, probesByName, logger);
        patterdale.start();
        return patterdale;
    }

    public static String responseBody(HttpResponse response) throws IOException {
        try (Scanner scanner = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.toString()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
