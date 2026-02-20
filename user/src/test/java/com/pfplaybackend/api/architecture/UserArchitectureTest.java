package com.pfplaybackend.api.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("User 모듈 아키텍처 규칙 검증")
class UserArchitectureTest {

    static JavaClasses userClasses;

    @BeforeAll
    static void setUp() {
        userClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.pfplaybackend.api.user");
    }

    @Test
    @DisplayName("domain 패키지는 adapter 패키지에 의존하지 않는다")
    void domainShouldNotDependOnAdapter() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..user.domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..user.adapter..");

        rule.check(userClasses);
    }

    @Test
    @DisplayName("domain 패키지는 application 패키지에 의존하지 않는다 (MemberData, UserAccountData, UserAvatarDomainService 예외)")
    void domainShouldNotDependOnApplication() {
        // TODO: MemberData, UserAccountData가 application DTO를, UserAvatarDomainService가 AvatarResourceService를 참조 중
        //       향후 도메인 전용 타입/포트로 교체하여 해소 예정
        ArchRule rule = noClasses()
                .that().resideInAPackage("..user.domain..")
                .and().doNotHaveSimpleName("MemberData")
                .and().doNotHaveSimpleName("UserAccountData")
                .and().doNotHaveSimpleName("UserAvatarDomainService")
                .should().dependOnClassesThat()
                .resideInAPackage("..user.application..");

        rule.check(userClasses);
    }

    @Test
    @DisplayName("application 패키지는 adapter.in 패키지에 의존하지 않는다 (Request/Response DTO 참조 예외)")
    void applicationShouldNotDependOnInboundAdapter() {
        // TODO: MemberSignService, UserInfoService, AvatarRequestValidator, UserAvatarService가 adapter.in의 DTO를 참조 중
        ArchRule rule = noClasses()
                .that().resideInAPackage("..user.application..")
                .and().doNotHaveSimpleName("MemberSignService")
                .and().doNotHaveSimpleName("UserInfoService")
                .and().doNotHaveSimpleName("AvatarRequestValidator")
                .and().doNotHaveSimpleName("UserAvatarService")
                .should().dependOnClassesThat()
                .resideInAPackage("..user.adapter.in..");

        rule.check(userClasses);
    }
}
