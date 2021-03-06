package com.gabrielittner.threetenbp;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjectSubject.assertThat;

import com.google.common.base.Charsets;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JavaWriterTest {

    private static final String SOURCE_NAME = "com/gabrielittner/threetenbp/LazyZoneRules";

    private Path outputDir;
    private JavaWriter javaWriter;

    @Before
    public void setup() throws IOException {
        outputDir = Files.createTempDirectory(null);
        javaWriter = new JavaWriter(outputDir);
    }

    private JavaFileObject generatedSource(String version, String... zoneIds) throws Exception {
        javaWriter.writeZoneIds(version, new LinkedHashSet<>(Arrays.asList(zoneIds)));
        Path output = outputDir.resolve(SOURCE_NAME + ".java");
        String sourceString =  new String(Files.readAllBytes(output), Charsets.UTF_8);
        return JavaFileObjects.forSourceString(SOURCE_NAME, sourceString);
    }

    @Test
    public void writeZoneIds() throws Exception {
        JavaFileObject source = generatedSource("2010a", "Europe/Berlin", "UTC", "US/Pacific");
        JavaFileObject expected = JavaFileObjects.forSourceString(SOURCE_NAME, ""
                + "package com.gabrielittner.threetenbp;"
                + "\n"
                + "import java.lang.String;"
                + "import java.util.Arrays;"
                + "import java.util.List;"
                + "\n"
                + "class LazyZoneRules {\n"
                + "    static final String VERSION = \"2010a\";\n"
                + "\n"
                + "    static final List<String> REGION_IDS = Arrays.asList(\n"
                + "            \"Europe/Berlin\",\n"
                + "            \"UTC\",\n"
                + "            \"US/Pacific\");\n"
                + "        }");

        Compilation compilation = javac().compile(source);
        assertThat(compilation).succeeded();
        assertThat(source).hasSourceEquivalentTo(expected);
    }
}
