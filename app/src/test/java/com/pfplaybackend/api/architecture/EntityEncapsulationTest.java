package com.pfplaybackend.api.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@DisplayName("엔티티 캡슐화 검증")
class EntityEncapsulationTest {

    static JavaClasses partyClasses;

    @BeforeAll
    static void setUp() {
        partyClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(
                        "com.pfplaybackend.api.party.domain.entity.data"
                );
    }

    @Test
    @DisplayName("Party 도메인 엔티티에 Lombok 생성 setter 메서드가 없다")
    void entityClassesShouldNotHaveLombokSetters() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.entity.data..")
                .and().areAnnotatedWith(jakarta.persistence.Entity.class)
                .should(notHaveLombokGeneratedSetters());

        rule.check(partyClasses);
    }

    @Test
    @DisplayName("Party 도메인 엔티티의 no-arg 생성자는 protected이다")
    void entityNoArgConstructorsShouldBeProtected() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.entity.data..")
                .and().areAnnotatedWith(jakarta.persistence.Entity.class)
                .should(haveProtectedNoArgConstructor());

        rule.check(partyClasses);
    }

    private static ArchCondition<JavaClass> notHaveLombokGeneratedSetters() {
        return new ArchCondition<>("not have Lombok-generated setter methods (public set* with single parameter)") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                javaClass.getMethods().stream()
                        .filter(m -> m.getName().startsWith("set"))
                        .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
                        .filter(m -> m.getRawParameterTypes().size() == 1)
                        .forEach(m -> events.add(SimpleConditionEvent.violated(m,
                                javaClass.getSimpleName() + "." + m.getName()
                                        + "() — Lombok setter가 존재합니다. 도메인 메서드로 교체하세요.")));
            }
        };
    }

    private static ArchCondition<JavaClass> haveProtectedNoArgConstructor() {
        return new ArchCondition<>("have a protected no-arg constructor") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean hasNoArgConstructor = javaClass.getConstructors().stream()
                        .anyMatch(c -> c.getRawParameterTypes().isEmpty());

                if (!hasNoArgConstructor) {
                    return;
                }

                boolean hasProtectedNoArg = javaClass.getConstructors().stream()
                        .filter(c -> c.getRawParameterTypes().isEmpty())
                        .anyMatch(c -> c.getModifiers().contains(JavaModifier.PROTECTED));

                if (!hasProtectedNoArg) {
                    events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + " — no-arg 생성자가 protected가 아닙니다."));
                }
            }
        };
    }
}
