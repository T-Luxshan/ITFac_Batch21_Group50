package com.OnTerminal.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Suite Runner
 * This is the main entry point for running Cucumber tests with Serenity BDD.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
                // Feature file location
                features = "src/test/resources/features",

                // Step definition packages
                glue = {
                                "com.OnTerminal.steps",
                                "com.OnTerminal.steps.ui",
                                "com.OnTerminal.steps.api"
                },

                // Default tags - exclude @skip and @wip (work in progress)
                tags = "@plants_ui",

                // Plugin configuration for better reporting

                plugin = {
                                "pretty",
                                "html:target/cucumber-reports/cucumber.html",
                                "json:target/cucumber-reports/cucumber.json",
                                "junit:target/cucumber-reports/cucumber.xml"
                },

                // Fail if step definitions are not found
                snippets = CucumberOptions.SnippetType.CAMELCASE,

                // Rerun failed scenarios
                monochrome = true,

                // Show summary at the end
                dryRun = false)
public class CucumberTestSuite {
        /*
         * This class is intentionally empty.
         * It serves as an entry point for JUnit to discover and run Cucumber tests.
         *
         */
}
