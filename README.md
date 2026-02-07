# OnTerminal Test Automation

This repository contains the test automation framework for the OnTerminal application. It supports both UI and API testing using Serenity BDD, and Cucumber.

## Tech Stack
- **Language:** Java 21
- **Framework:** Serenity BDD 5.x (UI & API Automation)
- **BDD:** Cucumber 7.x
- **Build Tool:** Maven

## Prerequisites
- Java JDK 21+ installed and configured.
- Maven installed.

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/T-Luxshan/ITFac_Batch21_Group50.git
   cd ITFac_Batch21_Group50
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install -DskipTests
   ```

## Running Tests

Run test cases individually:
```bash
mvn clean test "-Dtest=CucumberTestSuite" "-Dcucumber.filter.tags=@<Test_ID>"
```

Run all tests:
```bash
mvn clean verify
```

## Detailed Reports

After running tests, generate the Serenity report:
```bash
mvn serenity:aggregate
```
Open `target/site/serenity/index.html` in your browser to view the detailed test report.
