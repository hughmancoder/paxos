# Makefile for building and testing with Gradle

GRADLEW := ./gradlew

.PHONY: build clean test run

default:
	@echo "Setting ./gradlew executable permissions..."
	chmod +x ./gradlew
	@echo "Compiling the project..."
	./gradlew compileJava

build:
	@echo "Building the project..."
	@$(GRADLEW) build

compile:
	./gradlew compileJava

clean:
	@echo "Cleaning the build..."
	@$(GRADLEW) clean

run:
	@echo "Running the application..."
	@$(GRADLEW) run

test:
	@echo "Running tests..."
	@$(GRADLEW) test

test-simultaneous-member-vote:
	./gradlew test --tests *SimultaneousMemberVoteTest
	
test-immediate-response:
	./gradlew test --tests *ImmediateResponseTest


test-varied-response:
	./gradlew test --tests *VariedResponseTest

test-network:
	./gradlew test --tests *NetworkHandlerTest

kill-ports:
	@command -v lsof >/dev/null 2>&1 || { echo >&2 "lsof command not found. Aborting."; exit 1; }
	@echo "Killing processes on ports 4570-4579..."
	@-lsof -ti:4570-4579 | xargs -r kill -9
	@echo "Ports freed."

