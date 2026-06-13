Upgrade Java runtime to JDK 21 (macOS)

This project targets Java 21 in `backend/pom.xml`.

Local steps (macOS - Homebrew / Adoptium):

1) Install Temurin 21 via Homebrew (recommended):

```bash
brew install --cask temurin@21
```

2) Point `JAVA_HOME` to the JDK 21 installation. If you installed via Homebrew:

```bash
export JAVA_HOME="/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
# or if using temurin path (check /Library/Java/JavaVirtualMachines/)
# export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
```

Add the export to your shell profile (`~/.zshrc` or `~/.bash_profile`) to make it persistent.

3) Verify:

```bash
java -version
mvn -v
```

Set Maven to use that `JAVA_HOME` if necessary by configuring your IDE or the terminal environment.

Docker (for integration tests using Testcontainers)

Install Docker Desktop for macOS and start it (required by Testcontainers integration tests):

```bash
# Download and install from Docker:
# https://www.docker.com/get-started
# Then start Docker Desktop from Applications.
```

Local build/test commands

- Build without running integration tests (fast):

```bash
cd backend
mvn -DskipITs=true test
```

- Run full build including integration tests (requires Docker running):

```bash
cd backend
mvn -DskipITs=false verify
```

CI

CI should run with Java 21 and `-DskipITs=false` so integration tests execute in the runner (runners have Docker available). See `.github/workflows/ci.yml` in this repo for an example.
