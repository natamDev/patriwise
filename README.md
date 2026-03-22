# FinMate

Application mobile-first d'éducation financière pour les 18-25 ans.

## Stack

- **Frontend** : Vue.js + TypeScript + SCSS → [patriwise.netlify.app](https://patriwise.netlify.app)
- **Backend** : Quarkus (Java 21) → [finmate-backend-cua2.onrender.com](https://finmate-backend-cua2.onrender.com)
- **Base de données** : PostgreSQL via [Neon.tech](https://neon.tech)

## Déploiement

### Infrastructure

| Service | Plateforme | URL |
|---|---|---|
| Frontend | Netlify (gratuit) | https://patriwise.netlify.app |
| Backend | Render (gratuit) | https://finmate-backend-cua2.onrender.com |
| Base de données | Neon (gratuit) | PostgreSQL managé |

### Variables d'environnement (Render)

```
DATABASE_URL       jdbc:postgresql://<host>/neondb?sslmode=require&channel_binding=require
DATABASE_USER      neondb_owner
DATABASE_PASSWORD  <secret>
AI_PROVIDER        huggingface
HUGGINGFACE_API_KEY <secret>
```

### Déploiement frontend (Netlify)

Le déploiement est automatique sur chaque push sur `main`.

Configuration dans `finmate-frontend/netlify.toml` :
- Build command : `npm run build`
- Publish directory : `dist`
- Variable : `VITE_API_URL=https://finmate-backend-cua2.onrender.com`

### Déploiement backend (Render)

Le déploiement est automatique sur chaque push sur `main`.

Configuration :
- Dockerfile : `finmate-backend/Dockerfile`
- Docker build context : `finmate-backend`
- Port : `8080`

## Développement local

### Backend

```bash
cd finmate-backend
./mvnw quarkus:dev
```

Profil `dev` : H2 in-memory, schéma recréé au démarrage.

### Frontend

```bash
cd finmate-frontend
npm install
npm run dev
```

Variable d'env : `VITE_API_URL=http://localhost:8080` (défaut si non défini).
