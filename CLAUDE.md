# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**FinMate** — A mobile-first financial education application for users aged 18–25. The product encourages financial discipline, long-term investing, and risk awareness. It must **never** encourage trading, speculation, short-term investing, or hype investing.

## Tech Stack

**Frontend:** Vue.js + TypeScript + SCSS (mobile-first)
**Backend:** Quarkus + Hexagonal Architecture

## Working Rules

- Always explain important technical decisions and rejected alternatives
- Always end each modification with a recap: what was done, and why
- Work incrementally, feature by feature — do not generate large amounts of code blindly

## Frontend Conventions

- TypeScript only — no JavaScript
- Use `<script setup lang="ts">` and `<style lang="scss">` in all components
- Self-close tags without children (`<MyComp />`)
- Never target raw HTML tags inside scoped styles — always use classes
- Structure components by feature folders (e.g. `src/features/budget/`)
- Use `api/` folder instead of `services/` — one API file per resource
- `config.ts` exports the Axios instance
- Extract repeated strings into variables; do not create variables for values used only once

**Frontend folder structure:**

```
src/
  api/           # one file per resource
  components/    # shared components
  features/      # feature-based folders
  composables/
  router/
  types/
  styles/
  views/
```

**Feature modules:** `financial-profile`, `budget`, `savings-goals`, `investment-education`, `risk-awareness`, `behavioral-coaching`, `ai-assistant`, `financial-projections`, `gamification`

## Backend Conventions

Strict hexagonal architecture — no `adapter/in/out` naming, no `persistence/` folder:

```
domain/
  model/
  port/
  service/
application/
  resource/    # REST resources
  dto/
infrastructure/
  postgres/    # named by technology
```

- Document all REST endpoints with OpenAPI
- Lombok usage:
  - Domain entities with id: `@Getter @Setter @AllArgsConstructor @NoArgsConstructor`
  - Simple DTOs: `@Data @NoArgsConstructor`
  - Do NOT use `@Data` on domain entities

**Backend modules:** `profile`, `budget`, `goals`, `education`, `risk`, `behavior`, `assistant`, `projections`, `gamification`

## Development Order (Feature-by-Feature)

Implement features in this order, completing each before moving to the next:

1. Financial Profile & Budget Understanding (EPIC 1)
2. Savings Discipline (EPIC 2)
3. Passive Investing Education (EPIC 3)
4. Risk Awareness (EPIC 4)
5. Behavioral Finance Coaching (EPIC 5)
6. AI Financial Assistant (EPIC 6)
7. Financial Projections (EPIC 7)
8. Gamification (EPIC 8)
9. AI Financial Coaching Assistant (EPIC 9)

For each feature, follow this order:

1. Restate scope → 2. Backend domain model → 3. DTOs → 4. REST resources + OpenAPI → 5. Frontend API layer (`src/api/`) → 6. Vue components + views → 7. Explain decisions → 8. Recap

## AI Assistant (EPIC 6 / EPIC 9)

Uses the Claude API. The system prompt must enforce:

- Educational, non-speculative responses
- Long-term investing philosophy
- No trading or market timing advice

Key data models: `assistant_conversations`, `assistant_messages`, `assistant_recommendations`

## Key Data Models

| Model                      | Key Fields                                                                              |
| -------------------------- | --------------------------------------------------------------------------------------- |
| `financial_profiles`       | `user_id, monthly_income, employment_status, age, financial_experience_level, currency` |
| `expenses`                 | `user_id, amount, category, description, expense_date`                                  |
| `goals`                    | `user_id, goal_name, goal_type, target_amount, target_date, monthly_contribution`       |
| `goal_contributions`       | `goal_id, amount, contribution_date`                                                    |
| `education_lessons`        | `title, content, category`                                                              |
| `financial_scores`         | `user_id, score, calculated_at`                                                         |
| `badges` / `user_badges`   | `name, description` / `user_id, badge_id, unlocked_at`                                  |
| `quizzes` / `quiz_results` | `question, options, correct_answer` / `user_id, quiz_id, score`                         |

## Financial Score Formula

```
score = (savings_rate * 50) + (expense_control * 20) + (goal_progress * 20) + (financial_stability * 10)
```

Ranges: 0–40 = poor, 40–70 = improving, 70–100 = healthy. Recalculate on every budget or goal change.

## Compound Interest Formula (used in simulators)

```
future_value = P * ((1 + r)^n - 1) / r
```
