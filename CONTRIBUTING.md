# Contributing to WhisperChain

Thank you for your interest in contributing to WhisperChain! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## How to Contribute

There are many ways to contribute to WhisperChain:

1. **Report bugs**: Submit bug reports on the [issue tracker](https://github.com/Amineos/WhisperChain/issues).
2. **Suggest features**: Submit feature requests on the [issue tracker](https://github.com/Amineos/WhisperChain/issues).
3. **Submit pull requests**: If you'd like to add a new feature or fix a bug, submit a pull request.

## Development Process

### Setting Up Development Environment

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR-USERNAME/WhisperChain.git`
3. Create a new branch: `git checkout -b feature-or-fix-name`
4. Set up the development environment:
   - Make sure you have JDK 21+ installed
   - Use an IDE that supports Gradle (IntelliJ IDEA recommended)
   - Import the project as a Gradle project

### Building the Project

Build the project with:

```bash
./gradlew build
```

### Testing

Test your changes thoroughly:

1. Run automated tests: `./gradlew test`
2. Test the plugin on a local Paper server

### Submitting a Pull Request

1. Push your changes to your fork
2. Submit a pull request from your branch to the `main` branch
3. In the PR description, explain the changes and link to any relevant issues
4. Wait for a review and address any requested changes

## Code Style

- Follow existing code style and conventions
- Include JavaDoc comments for new classes and methods
- Keep code clean and maintainable
- Write descriptive commit messages

## Pull Request Checklist

Before submitting a pull request, make sure:

- [ ] Your code builds without errors
- [ ] You've added/updated tests for your changes
- [ ] You've tested your changes with a Paper server
- [ ] Your code follows the project's style guidelines
- [ ] You've updated documentation if necessary

## License

By contributing to WhisperChain, you agree that your contributions will be licensed under the project's [MIT License](LICENSE). 