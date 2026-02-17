package org.phoenicis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableList;
import org.phoenicis.repository.location.ClasspathRepositoryLocation;
import org.phoenicis.repository.location.GitRepositoryLocation;
import org.phoenicis.repository.location.RepositoryLocation;
import org.phoenicis.repository.types.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilesystemJsonRepositoryLocationLoader implements RepositoryLocationLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemJsonRepositoryLocationLoader.class);
    private static final String LEGACY_SCRIPTS_REPOSITORY_URL = "https://github.com/ahmedmoselhi/scripts";

    private final String repositoryListPath;
    private final String defaultGitUrl;
    private final String defaultGitBranch;
    private final String defaultClasspath;
    private final ObjectMapper objectMapper;

    public FilesystemJsonRepositoryLocationLoader(String repositoryListPath, String defaultGitUrl,
            String defaultGitBranch, String defaultClasspath, ObjectMapper objectMapper) {
        this.repositoryListPath = repositoryListPath;
        this.defaultGitUrl = defaultGitUrl;
        this.defaultGitBranch = defaultGitBranch;
        this.defaultClasspath = defaultClasspath;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<RepositoryLocation<? extends Repository>> getDefaultRepositoryLocations() {
        try {
            return ImmutableList.<RepositoryLocation<? extends Repository>> builder()
                    .add(new GitRepositoryLocation.Builder()
                            .withGitRepositoryUri(new URL(defaultGitUrl).toURI())
                            .withBranch(defaultGitBranch).build())
                    .add(new ClasspathRepositoryLocation(defaultClasspath))
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            LOGGER.error("Couldn't create default repository location list", e);

            return Collections.emptyList();
        }
    }

    @Override
    public List<RepositoryLocation<? extends Repository>> loadRepositoryLocations() {
        List<RepositoryLocation<? extends Repository>> result = new ArrayList<>();

        File repositoryListFile = new File(repositoryListPath);

        if (repositoryListFile.exists()) {
            try {
                result = this.objectMapper.readValue(new File(repositoryListPath),
                        TypeFactory.defaultInstance().constructParametricType(List.class, RepositoryLocation.class));
                result = migrateLegacyRepositoryLocations(result);
            } catch (IOException e) {
                LOGGER.error("Couldn't load repository location list", e);
            }
        } else {
            result.addAll(getDefaultRepositoryLocations());
        }

        return result;
    }

    @Override
    public void saveRepositories(List<RepositoryLocation<? extends Repository>> repositoryLocations) {
        try {
            objectMapper.writeValue(new File(repositoryListPath), repositoryLocations);
        } catch (IOException e) {
            LOGGER.error("Couldn't save repository location list", e);
        }
    }

    private List<RepositoryLocation<? extends Repository>> migrateLegacyRepositoryLocations(
            List<RepositoryLocation<? extends Repository>> repositoryLocations) {
        final List<RepositoryLocation<? extends Repository>> migratedRepositoryLocations = new ArrayList<>();
        boolean changed = false;

        for (RepositoryLocation<? extends Repository> location : repositoryLocations) {
            if (location instanceof GitRepositoryLocation
                    && ((GitRepositoryLocation) location).getGitRepositoryUri().toString()
                            .equals(LEGACY_SCRIPTS_REPOSITORY_URL)) {
                try {
                    migratedRepositoryLocations.add(new GitRepositoryLocation.Builder((GitRepositoryLocation) location)
                            .withGitRepositoryUri(new URI(defaultGitUrl))
                            .build());
                    changed = true;
                } catch (URISyntaxException e) {
                    LOGGER.error("Couldn't migrate legacy scripts repository URL", e);
                    migratedRepositoryLocations.add(location);
                }
            } else {
                migratedRepositoryLocations.add(location);
            }
        }

        if (changed) {
            saveRepositories(migratedRepositoryLocations);
        }

        return migratedRepositoryLocations;
    }
}
