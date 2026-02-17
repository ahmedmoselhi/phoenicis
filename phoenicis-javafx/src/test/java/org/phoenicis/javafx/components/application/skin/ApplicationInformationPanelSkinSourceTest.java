package org.phoenicis.javafx.components.application.skin;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;

public class ApplicationInformationPanelSkinSourceTest {
    @Test
    public void testWineHqSourceUrlIsNotReferenced() throws IOException {
        final Path sourcePath = Path.of("src/main/java/org/phoenicis/javafx/components/application/skin/ApplicationInformationPanelSkin.java");
        final String content = Files.readString(sourcePath, StandardCharsets.UTF_8);

        assertFalse(content.contains("https://dl.winehq.org/wine/source/"));
        assertFalse(content.contains("Could not read any Wine branch"));
    }
}
