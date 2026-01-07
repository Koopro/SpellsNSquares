# Contributing to Spells N Squares

Thank you for your interest in contributing to Spells N Squares! This document provides guidelines for contributing.

## Code of Conduct

- Be respectful and constructive
- Follow existing code patterns and style
- Write clear, documented code
- Test your changes before submitting

## Development Workflow

### 1. Fork and Clone

Fork the repository and clone your fork locally.

### 2. Create a Branch

Create a feature branch for your changes:

```bash
git checkout -b feature/my-feature
```

### 3. Make Changes

- Follow the existing code style
- Add JavaDoc comments for public APIs
- Write tests for new functionality
- Update documentation as needed

### 4. Test Your Changes

- Run `./gradlew test` to ensure tests pass
- Test in-game functionality
- Check for linter errors

### 5. Commit Your Changes

Write clear, descriptive commit messages:

```
feat: Add new spell system
fix: Resolve particle performance issue
docs: Update API documentation
```

### 6. Submit a Pull Request

- Provide a clear description of changes
- Reference any related issues
- Ensure all checks pass

## Code Style Guidelines

### Naming Conventions

- Classes: `PascalCase`
- Methods: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: `lowercase`

### Code Organization

- Keep files under 300 lines (refactor if needed)
- Use feature-based organization
- Separate client/server code clearly
- Group related functionality together

### Error Handling

- Use `SafeEventHandler` for event handlers
- Provide meaningful error messages
- Log errors appropriately
- Never silently swallow exceptions

### Documentation

- Add JavaDoc for all public APIs
- Document complex algorithms
- Include usage examples
- Update relevant documentation files

## Testing Guidelines

### Unit Tests

- Test core functionality
- Test edge cases
- Test error conditions
- Aim for good coverage of critical paths

### Integration Tests

- Test feature interactions
- Test data persistence
- Test network synchronization

## Pull Request Guidelines

### Before Submitting

- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Documentation updated
- [ ] No linter errors
- [ ] Changes tested in-game

### PR Description

Include:
- What changes were made
- Why the changes were needed
- How to test the changes
- Any breaking changes

## Feature Requests

For feature requests:
1. Check if it's already planned
2. Open an issue describing the feature
3. Discuss implementation approach
4. Wait for approval before implementing

## Bug Reports

When reporting bugs, include:
- Minecraft version
- Mod version
- Steps to reproduce
- Expected vs actual behavior
- Logs (if applicable)
- Screenshots (if applicable)

## Questions?

Feel free to ask questions by opening an issue or contacting maintainers.

Thank you for contributing!





