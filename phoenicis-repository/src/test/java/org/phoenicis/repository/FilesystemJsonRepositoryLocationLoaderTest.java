package org.phoenicis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.phoenicis.configuration.ObjectMapperFactory;
import org.phoenicis.repository.location.GitRepositoryLocation;
import org.phoenicis.repository.location.RepositoryLocation;
import org.phoenicis.repository.types.Repository;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilesystemJsonRepositoryLocationLoaderTest {
    @Test
    public void testMigratesLegacyScriptsRepositoryUrl() throws Exception {
        final TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        try {
            final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
            final String repositoryListPath = temporaryFolder.newFile("repositories.json").getAbsolutePath();
            final GitRepositoryLocation legacyRepository = new GitRepositoryLocation.Builder()
                    .withGitRepositoryUri(new URI("https://github.com/ahmedmoselhi/scripts"))
                    .withBranch("master")
                    .build();

            objectMapper.writeValue(temporaryFolder.getRoot().toPath().resolve("repositories.json").toFile(),
                    Collections.singletonList(legacyRepository));

            final FilesystemJsonRepositoryLocationLoader loader = new FilesystemJsonRepositoryLocationLoader(
                    repositoryListPath,
                    "https://github.com/PhoenicisOrg/scripts",
                    "master",
                    "/org/phoenicis/repository",
                    objectMapper);

            final List<RepositoryLocation<? extends Repository>> loadedLocations = loader.loadRepositoryLocations();
            final GitRepositoryLocation migratedRepository = (GitRepositoryLocation) loadedLocations.get(0);

            assertEquals("https://github.com/PhoenicisOrg/scripts", migratedRepository.getGitRepositoryUri().toString());

            final List<?> persistedLocations = objectMapper.readValue(temporaryFolder.getRoot().toPath()
                    .resolve("repositories.json").toFile(), List.class);
            assertTrue(persistedLocations.toString().contains("https://github.com/PhoenicisOrg/scripts"));
        } finally {
            temporaryFolder.delete();
        }
    }
}
