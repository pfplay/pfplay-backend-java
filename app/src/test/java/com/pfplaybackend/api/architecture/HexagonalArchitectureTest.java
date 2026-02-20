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
    }

    @Nested
    @DisplayName("Party 도메인 레이어 규칙")
    class PartyDomainLayerRules {

        @Test
        @DisplayName("domain 패키지는 adapter 패키지에 의존하지 않는다 (PartyroomAggregateService 예외)")
        void domainShouldNotDependOnAdapter() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.domain..")
                    // Phase 2에서 해소 예정: PartyroomAggregateService → DjRepository, PartyroomPlaybackRepository
                    .and().doNotHaveSimpleName("PartyroomAggregateService")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.adapter..");

            rule.check(partyClasses);
        }

        @Test
        @DisplayName("domain 패키지는 application 패키지에 의존하지 않는다 (이벤트/서비스 DTO 참조 예외)")
        void domainShouldNotDependOnApplication() {
            // TODO: PlaybackStartedEvent가 PlaybackDto를, PlaybackReactionDomainService가 ReactionPostProcessDto를 참조 중
            //       향후 도메인 전용 타입으로 교체하여 해소 예정
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.domain..")
                    .and().doNotHaveSimpleName("PlaybackStartedEvent")
                    .and().doNotHaveSimpleName("PlaybackReactionDomainService")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.application..");

            rule.check(partyClasses);
        }

        @Test
        @DisplayName("application 패키지는 adapter.in 패키지에 의존하지 않는다 (Request/Message DTO 참조 예외)")
        void applicationShouldNotDependOnInboundAdapter() {
            // TODO: 다수의 Application Service가 adapter.in의 Request/Message DTO를 직접 참조 중
            //       향후 application/dto/command로 이동하여 해소 예정
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..party.application..")
                    .and().doNotHaveSimpleName("CrewGradeService")
                    .and().doNotHaveSimpleName("CrewBlockService")
                    .and().doNotHaveSimpleName("CrewPenaltyService")
                    .and().doNotHaveSimpleName("CrewProfileChangeHandler")
                    .and().doNotHaveSimpleName("PartyroomInfoService")
                    .and().doNotHaveSimpleName("PartyroomManagementService")
                    .and().doNotHaveSimpleName("PartyroomChatService")
                    .and().doNotHaveSimpleName("PlaybackManagementService")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..party.adapter.in..");

            rule.check(partyClasses);
        }
    }

    @Nested
    @DisplayName("Auth 도메인 레이어 규칙")
    class AuthDomainLayerRules {

        @Test
        @DisplayName("auth domain 패키지는 adapter 패키지에 의존하지 않는다 (OAuth2Redirection 예외)")
        void domainShouldNotDependOnAdapter() {
            // TODO: OAuth2Redirection이 OAuth2ProviderConfig$Environment를 참조 중
            //       향후 도메인 전용 타입으로 교체하여 해소 예정
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..auth.domain..")
                    .and().doNotHaveSimpleName("OAuth2Redirection")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..auth.adapter..");

            rule.check(authClasses);
        }

        @Test
        @DisplayName("auth application 패키지는 adapter.in 패키지에 의존하지 않는다 (Request/Response DTO 참조 예외)")
        void applicationShouldNotDependOnInboundAdapter() {
            // TODO: AuthService, OAuthUrlService가 adapter.in의 Request/Response DTO를 참조 중
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..auth.application..")
                    .and().doNotHaveSimpleName("AuthService")
                    .and().doNotHaveSimpleName("OAuthUrlService")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..auth.adapter.in..");

            rule.check(authClasses);
        }
    }

    @Nested
    @DisplayName("Admin 도메인 레이어 규칙")
    class AdminDomainLayerRules {

        @Test
        @DisplayName("admin application 패키지는 adapter.in 패키지에 의존하지 않는다 (Request/Response DTO 참조 예외)")
        void applicationShouldNotDependOnInboundAdapter() {
            // TODO: AdminPartyroomService, AdminDemoService, ReactionSimulationService가 adapter.in의 DTO를 참조 중
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..admin.application..")
                    .and().doNotHaveSimpleName("AdminPartyroomService")
                    .and().doNotHaveSimpleName("AdminDemoService")
                    .and().doNotHaveSimpleName("ReactionSimulationService")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..admin.adapter.in..");

            rule.check(adminClasses);
        }
    }
}
