package com.pfplaybackend.api.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("헥사고널 아키텍처 규칙 검증")
class HexagonalArchitectureTest {

    static JavaClasses partyClasses;
    static JavaClasses authClasses;
    static JavaClasses adminClasses;
    static JavaClasses partyviewClasses;

    @BeforeAll
    static void setUp() {
        partyClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.party");
        authClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.auth");
        adminClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.admin");
        partyviewClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.partyview");
    }

    @Nested
    @DisplayName("Party 도메인 레이어 규칙")
    class PartyDomainLayerRules {

        @Test
        @DisplayName("domain 패키지는 adapter 패키지에 의존하지 않는다")
        void domainShouldNotDependOnAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.adapter..");

            rule.check(partyClasses);
        }

        @Test
        @DisplayName("domain 패키지는 application 패키지에 의존하지 않는다")
        void domainShouldNotDependOnApplication() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.application..");

            rule.check(partyClasses);
        }

        @Test
        @DisplayName("application service는 Partyroom Aggregate 내부 Repository를 직접 import하지 않는다")
        void applicationServiceShouldNotImportAggregateRepositories() {
            // Partyroom aggregate 엔티티(Partyroom, Crew, DJ, PartyroomPlayback, DjQueue)에 대한
            // Repository는 PartyroomAggregatePort를 통해서만 접근해야 한다.
            // 별도 Aggregate인 Playback*, CrewPenaltyHistory, CrewBlockHistory Repository는 허용.
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.application.service..")
                    .should().dependOnClassesThat()
                    .haveNameMatching(".*(PartyroomRepository|CrewRepository|DjRepository|DjQueueRepository|PartyroomPlaybackRepository)");

            rule.check(partyClasses);
        }

        @Test
        @DisplayName("application 패키지는 adapter.in 패키지에 의존하지 않는다")
        void applicationShouldNotDependOnInboundAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.adapter.in..");

            rule.check(partyClasses);
        }
    }

    @Nested
    @DisplayName("Auth 도메인 레이어 규칙")
    class AuthDomainLayerRules {

        @Test
        @DisplayName("auth domain 패키지는 adapter 패키지에 의존하지 않는다")
        void domainShouldNotDependOnAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..auth.domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..auth.adapter..");

            rule.check(authClasses);
        }

        @Test
        @DisplayName("auth application 패키지는 adapter.in 패키지에 의존하지 않는다")
        void applicationShouldNotDependOnInboundAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..auth.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..auth.adapter.in..");

            rule.check(authClasses);
        }
    }

    @Nested
    @DisplayName("Admin 도메인 레이어 규칙")
    class AdminDomainLayerRules {

        @Test
        @DisplayName("admin application 패키지는 adapter.in 패키지에 의존하지 않는다")
        void applicationShouldNotDependOnInboundAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..admin.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..admin.adapter.in..");

            rule.check(adminClasses);
        }
    }

    @Nested
    @DisplayName("Partyview 도메인 레이어 규칙")
    class PartyviewDomainLayerRules {

        @Test
        @DisplayName("partyview application 패키지는 partyview adapter.in 패키지에 의존하지 않는다")
        void applicationShouldNotDependOnInboundAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..partyview.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..partyview.adapter.in..");

            rule.check(partyviewClasses);
        }

        @Test
        @DisplayName("partyview application 패키지는 party adapter.out.persistence 패키지에 의존하지 않는다")
        void applicationShouldNotDependOnPartyPersistence() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..partyview.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.adapter.out.persistence..");

            rule.check(partyviewClasses);
        }
    }
}
