package pl.gdansk.cinema.cinema_booking;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "pl.gdansk.cinema.cinema_booking", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule services_should_be_in_service_package =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule controllers_should_be_in_controller_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..");

    @ArchTest
    public static final ArchRule repositories_should_be_in_repository_package =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..repository..");

    @ArchTest
    public static final ArchRule controllers_should_not_depend_on_entities =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..entity..");

    @ArchTest
    public static final ArchRule dtos_should_not_depend_on_entities =
            noClasses().that().resideInAPackage("..dto..")
                    .should().dependOnClassesThat().resideInAPackage("..entity..");

    @ArchTest
    public static final ArchRule layered_architecture_rule =
            layeredArchitecture().consideringAllDependencies()
                    .layer("Controller").definedBy("..controller..")
                    .layer("Service").definedBy("..service..")
                    .layer("Repository").definedBy("..repository..")
                    .layer("Config").definedBy("..config..")
                    .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                    .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Config")
                    .whereLayer("Config").mayNotBeAccessedByAnyLayer();

    @ArchTest
    public static final ArchRule repositories_should_be_interfaces =
            classes().that().resideInAPackage("..repository..")
                    .should().beInterfaces();
}
