# finmate-backend

Backend de l'application FinMate — Quarkus + Java 21 + Architecture hexagonale.

## Stack

- **Quarkus 3** — framework Java
- **Hibernate ORM + Panache** — persistance
- **SmallRye JWT** — authentification
- **SmallRye OpenAPI** — documentation REST
- **H2** (dev/test) / **PostgreSQL** (prod)

## Démarrage local

```bash
./mvnw quarkus:dev
```

L'API tourne sur http://localhost:8080.

- Swagger UI : http://localhost:8080/q/swagger-ui
- Dev UI : http://localhost:8080/q/dev

Profil `dev` : base H2 in-memory, schéma recréé au démarrage.

## Build

```bash
./mvnw package -DskipTests
```

Produit `target/quarkus-app/quarkus-run.jar`.

## Déploiement

Déployé automatiquement sur **Render** à chaque push sur `main` via Docker.

- URL : https://finmate-backend-cua2.onrender.com
- Dockerfile : `Dockerfile`
- Base de données : Neon (PostgreSQL managé)

### Variables d'environnement requises (Render)

```
DATABASE_URL        jdbc:postgresql://<host>/neondb?sslmode=require&channel_binding=require
DATABASE_USER       neondb_owner
DATABASE_PASSWORD   <secret>
AI_PROVIDER         huggingface
HUGGINGFACE_API_KEY <secret>
```

## Architecture

```
src/main/java/com/finmate/
  domain/
    model/       # entités métier
    port/        # interfaces (in/out)
    service/     # logique métier
  application/
    resource/    # endpoints REST
    dto/         # objets de transfert
  infrastructure/
    postgres/    # implémentations JPA
```

## Modules

`profile` · `budget` · `goals` · `education` · `risk` · `behavior` · `assistant` · `projections` · `gamification`
