package com.pfplaybackend.api.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Playlist 모듈 아키텍처 규칙 검증")
class PlaylistArchitectureTest {

    static JavaClasses playlistClasses;

    @BeforeAll
    static void setUp() {
        playlistClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.playlist");
    }

    @Test
    @DisplayName("domain 패키지는 adapter 패키지에 의존하지 않는다")
    void domainShouldNotDependOnAdapter() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..playlist.domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..playlist.adapter..");

        rule.check(playlistClasses);
    }

    @Test
    @DisplayName("domain 패키지는 application 패키지에 의존하지 않는다")
    void domainShouldNotDependOnApplication() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..playlist.domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..playlist.application..");

        rule.check(playlistClasses);
    }

    @Test
    @DisplayName("application 패키지는 adapter.in 패키지에 의존하지 않는다")
    void applicationShouldNotDependOnInboundAdapter() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..playlist.application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..playlist.adapter.in..");

        rule.check(playlistClasses);
    }
}
