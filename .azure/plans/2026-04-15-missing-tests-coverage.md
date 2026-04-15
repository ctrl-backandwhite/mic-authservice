# mic-authservice Missing Tests Coverage Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task.

**Goal:** Create unit tests for 17 uncovered classes with testable logic in mic-authservice (9 RepositoryImpl, 4 Kafka adapters, 4 Domain models).

**Architecture:** Tests follow Mockito + JUnit 5 pattern with existing Provider test data factories. RepositoryImpl tests mock JPA adapters and entity mappers. Kafka adapter tests mock KafkaTemplate. Domain model tests validate behavioral methods directly.

**Tech Stack:** JUnit 5, Mockito, AssertJ, existing Provider fixtures

---

## Task Summary

| # | Task | Classes | Tests |
|---|---|---|---|
| 1 | Domain Models with behavior | User, OauthClient, Group, Role | ~20 |
| 2 | Standard CRUD RepositoryImpl (5 identical pattern) | Group, Role, GrantType, Scope, RedirectUri | ~25 |
| 3 | PermissionRepositoryImpl (has bug) | PermissionRepositoryImpl | ~5 |
| 4 | UserRepositoryImpl (extra query methods) | UserRepositoryImpl | ~13 |
| 5 | OauthClientRepositoryImpl (findByClientId) | OauthClientRepositoryImpl | ~6 |
| 6 | UserSessionRepositoryImpl (@Transactional) | UserSessionRepositoryImpl | ~5 |
| 7 | Kafka Adapters (2 real + 2 NoOp) | KafkaAuth/NotificationEventAdapter, NoOp variants | ~8 |
| 8 | Run full test suite | - | - |

---

## Patterns

### RepositoryImpl Test Pattern
All RepositoryImpl tests follow this structure:
- `@ExtendWith(MockitoExtension.class)`
- `@Mock` for mapper and JPA adapter
- `@InjectMocks` for the impl
- Tests: save(), findAll(), update() (delegates to save), delete(), getById()
- Use Provider factory methods for test data (e.g., `RoleProvider.role()`, `RoleProvider.roleEntity()`)

### Domain Model Test Pattern
- No mocks needed, pure unit tests
- Test behavioral methods: getAuthorities(), getAuthority(), add/remove list methods
- Use Provider factory methods

### Kafka Adapter Test Pattern
- `@ExtendWith(MockitoExtension.class)`
- `@Mock` KafkaTemplate, mapper (if applicable)
- Verify `kafkaTemplate.send()` called with correct topic, key, event
- NoOp: verify method is callable without exceptions

---

## Task 1: Domain Models (User, OauthClient, Group, Role)

**Files to create:**
- `src/test/java/com/backandwhite/domain/model/UserTest.java`
- `src/test/java/com/backandwhite/domain/model/OauthClientTest.java`
- `src/test/java/com/backandwhite/domain/model/GroupTest.java`
- `src/test/java/com/backandwhite/domain/model/RoleTest.java`

**Key behaviors to test:**
- User: getAuthorities() with roles → returns roles; getAuthorities() with null/empty roles → returns ROLE_USER; getUsername() → returns email; isEnabled/isAccountNonExpired/etc delegate to fields
- OauthClient: addScope/removeScope, addRedirectUri/removeRedirectUri, addGrantType/removeGrantType
- Group: addRole/removeRole, addPermission/removePermission
- Role: getAuthority() → returns uniqueName

---

## Task 2: Standard CRUD RepositoryImpl (Group, Role, GrantType, Scope, RedirectUri)

**Files to create:**
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/GroupRepositoryImplTest.java`
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/RoleRepositoryImplTest.java`
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/GrantTypeRepositoryImplTest.java`
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/ScopeRepositoryImplTest.java`
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/RedirectUriRepositoryImplTest.java`

**Methods to test per class:**
- save(): mock mapper.toEntity() + jpaAdapter.save() + mapper.toDomain() → verify chain
- findAll(): mock jpaAdapter.findAll() + mapper.toDomainList() → verify
- update(): verify delegates to save()
- delete(): verify jpaAdapter.deleteById() called
- getById(): found → return domain; not found → return null

---

## Task 3: PermissionRepositoryImpl

**File to create:**
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/PermissionRepositoryImplTest.java`

**Note:** getById() has a potential bug with unconditional ENTITY_NOT_FOUND call. Test both found and not-found paths.

---

## Task 4: UserRepositoryImpl

**File to create:**
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/UserRepositoryImplTest.java`

**Extra methods to test:** findUserByEmail, findUserByNickName, findByActivationToken, findByPasswordResetToken, findByPasswordChangeCode, findByRoleId, findByGroupId, deleteAll (batch)
**getById:** throws EntityNotFoundException (unlike others)

---

## Task 5: OauthClientRepositoryImpl

**File to create:**
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/OauthClientRepositoryImplTest.java`

**Extra:** findByClientId() with null check, getById() returns null (no exception)

---

## Task 6: UserSessionRepositoryImpl

**File to create:**
- `src/test/java/com/backandwhite/infrastructure/db/postgres/repository/impl/UserSessionRepositoryImplTest.java`

**Methods:** save(), findActiveByUserId(), findBySessionId() (null check), revokeSession() (@Transactional), updateLastActiveAt() (@Transactional)

---

## Task 7: Kafka Adapters

**Files to create:**
- `src/test/java/com/backandwhite/infrastructure/message/kafka/producer/KafkaAuthEventAdapterTest.java`
- `src/test/java/com/backandwhite/infrastructure/message/kafka/producer/KafkaNotificationEventAdapterTest.java`
- `src/test/java/com/backandwhite/infrastructure/message/kafka/producer/NoOpAuthEventAdapterTest.java`
- `src/test/java/com/backandwhite/infrastructure/message/kafka/producer/NoOpNotificationEventAdapterTest.java`

---

## Task 8: Run full test suite and verify all pass
