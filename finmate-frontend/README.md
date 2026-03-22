# finmate-frontend

Frontend de l'application FinMate — Vue.js + TypeScript + SCSS, mobile-first.

## Stack

- **Vue 3** + `<script setup lang="ts">`
- **Vite** — bundler
- **Axios** — appels API
- **SCSS** — styles mobile-first

## Démarrage local

```bash
npm install
npm run dev
```

L'app tourne sur http://localhost:5173 (ou port suivant si occupé).

### Variable d'environnement

Créer un fichier `.env.local` à la racine :

```
VITE_API_URL=http://localhost:8080
```

Sans ce fichier, l'URL par défaut est `http://localhost:8080`.

## Build production

```bash
npm run build
```

Le résultat est dans `dist/`.

## Déploiement

Déployé automatiquement sur **Netlify** à chaque push sur `main`.

- URL : https://patriwise.netlify.app
- Config : `netlify.toml`
- Variable Netlify : `VITE_API_URL=https://finmate-backend-cua2.onrender.com`

## Structure

```
src/
  api/           # un fichier par ressource (auth, budget, goals...)
  components/    # composants partagés
  features/      # dossiers par feature
  composables/
  router/
  types/
  styles/
  views/
```
