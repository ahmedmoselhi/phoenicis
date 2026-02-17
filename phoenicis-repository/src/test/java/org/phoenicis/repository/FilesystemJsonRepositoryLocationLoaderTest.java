package org.phoenicis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.phoenicis.configuration.ObjectMapperFactory;
import org.phoenicis.repository.location.GitRepositoryLocation;
import org.phoenicis.repository.location.RepositoryLocation;
import org.phoenicis.repository.types.Repository;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FilesystemJsonRepositoryLocationLoaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testMigratesLegacyScriptsRepositoryUrl() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
        final File repositoryListFile = temporaryFolder.newFile("repositories.json");
        final GitRepositoryLocation legacyRepository = new GitRepositoryLocation.Builder()
                .withGitRepositoryUri(new URI("https://github.com/ahmedmoselhi/scripts"))
                .withBranch("master")
                .build();

        objectMapper.writeValue(repositoryListFile, Collections.singletonList(legacyRepository));

        final FilesystemJsonRepositoryLocationLoader loader = new FilesystemJsonRepositoryLocationLoader(
                repositoryListFile.getAbsolutePath(),
                "https://github.com/PhoenicisOrg/scripts",
                "master",
                "/org/phoenicis/repository",
                objectMapper);

        final List<RepositoryLocation<? extends Repository>> loadedLocations = loader.loadRepositoryLocations();
        final GitRepositoryLocation migratedRepository = (GitRepositoryLocation) loadedLocations.get(0);

        assertEquals("https://github.com/PhoenicisOrg/scripts", migratedRepository.getGitRepositoryUri().toString());

        final List<RepositoryLocation<? extends Repository>> persistedLocations = objectMapper.readValue(
                repositoryListFile,
                objectMapper.getTypeFactory().constructParametricType(List.class, RepositoryLocation.class));
        final GitRepositoryLocation persistedRepository = (GitRepositoryLocation) persistedLocations.get(0);

        assertEquals("https://github.com/PhoenicisOrg/scripts", persistedRepository.getGitRepositoryUri().toString());
    }

    @Test
    public void testDoesNotChangeNonLegacyRepositoryUrl() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
        final File repositoryListFile = temporaryFolder.newFile("repositories-non-legacy.json");
        final GitRepositoryLocation currentRepository = new GitRepositoryLocation.Builder()
                .withGitRepositoryUri(new URI("https://github.com/PhoenicisOrg/scripts"))
                .withBranch("master")
                .build();

        objectMapper.writeValue(repositoryListFile, Collections.singletonList(currentRepository));

        final FilesystemJsonRepositoryLocationLoader loader = new FilesystemJsonRepositoryLocationLoader(
                repositoryListFile.getAbsolutePath(),
                "https://github.com/PhoenicisOrg/scripts",
                "master",
                "/org/phoenicis/repository",
                objectMapper);

        final List<RepositoryLocation<? extends Repository>> loadedLocations = loader.loadRepositoryLocations();
        final GitRepositoryLocation loadedRepository = (GitRepositoryLocation) loadedLocations.get(0);

        assertEquals("https://github.com/PhoenicisOrg/scripts", loadedRepository.getGitRepositoryUri().toString());
    }
}
