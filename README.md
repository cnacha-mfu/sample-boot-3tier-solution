# sample-boot-3tier — **SOLUTION**

The finished version of
[`sample-boot-3tier`](https://github.com/cnacha-mfu/sample-boot-3tier) — the
library app split into three tiers, with a **DTO layer** and **MapStruct**.

Every step in that repo's [TODO.md](https://github.com/cnacha-mfu/sample-boot-3tier/blob/main/TODO.md)
links to the file here that answers it.

The long write-up of the whole session is **[SAMPLE-3TIER.md](SAMPLE-3TIER.md)**.
[SAMPLE-JPA.md](SAMPLE-JPA.md) is the previous day, for reference.

---

## Which file answers which step

| Step | File |
| --- | --- |
| 1 — `@EntityScan` | `library-web-service/.../service/App.java` |
| 2 — the DTO | `library-web-service/.../service/dto/BookDTO.java` |
| 3 — the mapper | `library-web-service/.../service/dto/mapper/BookMapper.java` |
| 4 — use them | `library-web-service/.../service/BookController.java` |
| 5 — PATCH | `library-web-service/.../service/BookController.java` (`patchBook`) |
| 6 — clean the entities | `library-domain-model/.../domain/Book.java`, `Category.java`, `library-domain-model/pom.xml` |
| 7 — CORS | `library-web-service/.../service/WebConfig.java` |
| 8 — the page | `library-web-front/src/main/resources/static/library.html` |

## It goes further than the eight steps

The session only needs `Book`. This repo also has DTOs, mappers and controllers
for **Category**, **Member** and **Transaction**. They are here to show that the
pattern repeats and is not a one-off — useful if the class finishes early, or as
reading afterwards.

`TransactionDTO` is the most interesting of the three: it flattens **two**
relationships at once, so a client can render "Malee borrowed Spring in Action"
from a single response.

## Running it

Needs JDK 11+, Maven, and MySQL with:

```sql
CREATE DATABASE spring_2026;
CREATE USER 'springuser_2026'@'localhost' IDENTIFIED BY '1234abcd';
GRANT ALL PRIVILEGES ON spring_2026.* TO 'springuser_2026'@'localhost';
FLUSH PRIVILEGES;
```

```bash
mvn install -DskipTests                      # from THIS folder, always

mvn -pl library-web-service spring-boot:run  # terminal 1 -> port 8080
mvn -pl library-web-front   spring-boot:run  # terminal 2 -> port 8081
```

Then open <http://localhost:8081/library.html>, or import
`postman/library-3tier.postman_collection.json`.

```bash
mvn verify      # BookResourceIT against the running service
```

`mvn verify` starts the service itself, so stop any `spring-boot:run` on 8080
first.

> The credentials above are local-development only and are meant to be shared
> with the class.

## Two things that were fixed here

**1. `MemberMapper` was ignoring `name`.**

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "name", ignore = true)      // <- wrong
void updateMemberFromDto(MemberDTO dto, @MappingTarget Member entity);
```

MapStruct generated an `updateMemberFromDto` that set only `email` and
`joinDate`, so `POST /api/members` always stored a member with **no name**, and
`PATCH` could never correct one. The rule the other two mappers follow is:

> **ignore the id and the relationships, never a plain data field.**

`id` because the controller decides it, and the relationships (`transactions`,
`books`, `category`) because they have to be looked up as real rows. `name`,
`email` and `joinDate` are ordinary fields and must be copied. Worth showing the
generated `MemberMapperImpl` to the class — the bug is visible in it.

**2. Paths and database now match the starting repo.**

This code used to answer on `/books` with database `spring_db`, while
`sample-boot-3tier` uses `/api/books` and `spring_2026`. A student copying a
controller across got a 404 and a connection error for no good reason. Both are
aligned now, so anything here can be pasted into the starting repo unchanged.

## The patterns, and where they ended up

| Pattern | Where |
| --- | --- |
| Layers (Presentation / Domain / Data Source) | the three Maven modules |
| Data Transfer Object | `service/dto/*DTO.java` |
| Assembler | `service/dto/mapper/*Mapper.java` (MapStruct) |
| Partial update | `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` |
| Loose coupling | `library-domain-model` depends on nothing of ours |
| Integration test | `BookResourceIT`, run by `mvn verify` |
