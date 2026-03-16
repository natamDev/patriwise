Structure utilisée :

Epic

Feature

User Story

Context

User Flow

Functional Requirements

Acceptance Criteria

Edge Cases

Data Model

Technical Notes

# **EPIC 1 — Financial Profile & Budget Understanding**

## **Objectif**

Permettre à l’utilisateur de :

- comprendre sa situation financière actuelle

- identifier ses dépenses

- calculer sa capacité d’épargne

- obtenir une vision claire de son budget.

Ce module constitue **la base de toutes les analyses futures** :

- objectifs d’épargne

- projections financières

- coaching IA.

---

# **FEATURE 1.1 — User Financial Profile**

## **User Story**

En tant qu’utilisateur  
 Je veux créer mon profil financier  
 Afin que l’application comprenne ma situation économique.

---

## **Context**

Les utilisateurs de 18-25 ans :

- ont rarement une vision claire de leurs finances

- ne savent pas combien ils peuvent épargner

- ne suivent pas leurs revenus.

Le profil financier permet :

- de calculer la capacité d’épargne

- de personnaliser les recommandations.

---

## **User Flow**

1. L’utilisateur ouvre l’application pour la première fois

2. L’onboarding démarre

3. L’utilisateur renseigne ses informations financières

4. Les données sont validées

5. Le profil est enregistré

6. L’utilisateur est redirigé vers le **dashboard financier**

---

## **Functional Requirements**

Le formulaire doit collecter :

monthly_income

employment_status

age

financial_experience_level

currency

employment_status options :

student

employee

freelancer

unemployed

financial_experience_level options :

beginner

intermediate

advanced

---

## **Acceptance Criteria**

Le profil doit :

- être enregistré en base de données

- être modifiable depuis les paramètres

- être accessible via API

- être utilisé par :
  - le calcul du score financier

  - les projections financières

  - les recommandations IA

---

## **Edge Cases**

- revenu \= 0

- revenu variable

- utilisateur étudiant

- devise différente

---

## **Data Model**

users

financial_profiles

\- id

\- user_id

\- monthly_income

\- employment_status

\- age

\- financial_experience_level

\- currency

\- created_at

\- updated_at

---

## **Technical Notes**

Endpoint API :

POST /api/profile

GET /api/profile

PATCH /api/profile

Frontend components :

ProfileForm

OnboardingFlow

ProfileSettings

---

# **FEATURE 1.2 — Expense Tracking**

## **User Story**

En tant qu’utilisateur  
 Je veux enregistrer mes dépenses  
 Afin de comprendre où part mon argent.

---

## **Context**

Les jeunes :

- sous-estiment leurs dépenses

- oublient les petits achats

- ignorent leurs abonnements.

Le suivi des dépenses permet :

- d’identifier les postes de dépenses

- de calculer le reste à vivre.

---

## **User Flow**

1. L’utilisateur ouvre la page **Budget**

2. Il clique sur **Add Expense**

3. Il saisit une dépense

4. Il choisit une catégorie

5. La dépense est enregistrée

6. Les statistiques sont recalculées

---

## **Functional Requirements**

Une dépense doit contenir :

amount

category

description

date

Catégories disponibles :

housing

transport

food

subscriptions

entertainment

shopping

health

other

Fonctions :

- ajouter dépense

- modifier dépense

- supprimer dépense

- filtrer par mois

---

## **Acceptance Criteria**

L’utilisateur peut :

- créer une dépense

- modifier une dépense

- supprimer une dépense

- voir les dépenses par catégorie

Le système doit :

- recalculer le budget automatiquement.

---

## **Edge Cases**

- montant négatif

- montant très élevé

- catégorie inexistante

- date future

---

## **Data Model**

expenses

\- id

\- user_id

\- amount

\- category

\- description

\- expense_date

\- created_at

---

## **Technical Notes**

Endpoints API :

POST /api/expenses

GET /api/expenses

PATCH /api/expenses/{id}

DELETE /api/expenses/{id}

Frontend components :

ExpenseForm

ExpenseList

ExpenseFilter

ExpenseCategoryChart

---

# **FEATURE 1.3 — Budget Dashboard**

## **User Story**

En tant qu’utilisateur  
 Je veux voir un résumé de mon budget  
 Afin de comprendre ma situation financière.

---

## **Context**

Les utilisateurs doivent comprendre rapidement :

- combien ils gagnent

- combien ils dépensent

- combien ils peuvent épargner.

Le dashboard doit être **simple et mobile-first**.

---

## **User Flow**

1. L’utilisateur ouvre la page **Home**

2. Le dashboard est chargé

3. Les données financières sont affichées

4. Les graphiques sont générés

---

## **Functional Requirements**

Calculs :

total_expenses \= SUM(expenses)

remaining_income \= monthly_income \- total_expenses

savings_rate \= remaining_income / monthly_income

Le dashboard doit afficher :

income card

expense card

remaining income

savings rate

expense chart

monthly trend

---

## **Acceptance Criteria**

Le dashboard doit :

- afficher les données en temps réel

- être responsive

- afficher les graphiques correctement

---

## **Edge Cases**

- aucune dépense enregistrée

- revenu nul

- dépenses supérieures au revenu

---

## **Data Model**

Utilise :

financial_profiles

expenses

---

## **Technical Notes**

Components :

BudgetDashboard

ExpensePieChart

IncomeExpenseCard

SavingsRateIndicator

---

# FEATURE 1.4 — Financial Health Score

## **User Story**

En tant qu’utilisateur  
 Je veux voir un score financier  
 Afin d’évaluer ma santé financière.

---

## **Context**

Un score simple permet :

- de comprendre sa situation

- de motiver l’amélioration.

---

## **User Flow**

1. L’utilisateur ouvre le dashboard

2. Le score est calculé

3. Une explication apparaît

---

## **Functional Requirements**

Score calculé à partir de :

savings_rate

expense_ratio

goal_progress

financial_stability

Formule exemple :

score \=

(savings_rate \* 50\)

\+ (expense_control \* 20\)

\+ (goal_progress \* 20\)

\+ (financial_stability \* 10\)

Score affiché :

0 – 40 : poor

40 – 70 : improving

70 – 100: healthy

---

## **Acceptance Criteria**

Le score doit :

- être recalculé automatiquement

- être visible sur le dashboard

- inclure une explication.

---

## **Edge Cases**

- revenu nul

- aucune dépense

- aucun objectif financier

---

## **Data Model**

financial_scores

\- id

\- user_id

\- score

\- calculated_at

---

## **Technical Notes**

Service :

financial_score_engine

Calcul exécuté :

- à chaque modification de budget

- à chaque modification d’objectif.

---

# **EPIC 2 — Savings Discipline**

## **Objectif**

Créer une **habitude d’épargne régulière**.

Le module doit :

- encourager l’épargne

- montrer la progression

- renforcer la motivation.

---

# **FEATURE 2.1 — Savings Goals**

## **User Story**

En tant qu’utilisateur  
 Je veux créer un objectif financier  
 Afin d’épargner pour un projet.

---

## **Context**

Les objectifs rendent l’épargne concrète.

Exemples :

- voyage

- fonds d’urgence

- investissement.

---

## **User Flow**

1. L’utilisateur ouvre **Goals**

2. Il clique sur **Create Goal**

3. Il renseigne les informations

4. L’objectif est créé

---

## **Functional Requirements**

Un objectif contient :

goal_name

goal_type

target_amount

target_date

monthly_contribution

goal_type :

travel

emergency_fund

investment

purchase

other

---

## **Acceptance Criteria**

L’utilisateur peut :

- créer

- modifier

- supprimer un objectif.

---

## **Edge Cases**

- montant cible \= 0

- date passée

- contribution trop élevée

---

## **Data Model**

goals

\- id

\- user_id

\- goal_name

\- goal_type

\- target_amount

\- target_date

\- monthly_contribution

\- created_at

---

# **FEATURE 2.2 — Goal Progress Tracking**

## **User Story**

En tant qu’utilisateur  
 Je veux voir la progression de mon objectif  
 Afin de rester motivé.

---

## **Context**

La progression visuelle motive l’utilisateur.

---

## **User Flow**

1. L’utilisateur ouvre **Goals**

2. Les objectifs sont affichés

3. La progression est visible

---

## **Functional Requirements**

Calcul :

progress \= saved_amount / target_amount

Affichage :

progress bar

remaining amount

completion percentage

---

## **Acceptance Criteria**

Le système doit :

- mettre à jour la progression

- afficher les données correctement.

---

## **Data Model**

goal_contributions

\- id

\- goal_id

\- amount

\- contribution_date

---

# **FEATURE 2.3 — Monthly Saving Plan**

## **User Story**

En tant qu’utilisateur  
 Je veux définir une épargne mensuelle  
 Afin d’atteindre mon objectif.

---

## **Functional Requirements**

Inputs :

monthly_contribution

target_amount

target_date

Calcul :

months_needed \= target_amount / monthly_contribution

---

## **Acceptance Criteria**

L’utilisateur doit voir :

- durée nécessaire

- date estimée de réussite.

---

# **FEATURE 2.4 — Saving Streak**

## **User Story**

En tant qu’utilisateur  
 Je veux voir combien de mois j’ai épargné consécutivement  
 Afin de maintenir ma discipline.

---

## **Functional Requirements**

Algorithme :

if contribution_month \== previous_month \+ 1

streak \+= 1

else

streak \= 1

---

## **Data Model**

saving_streaks

\- user_id

\- current_streak

\- longest_streak

---

# **EPIC 3 — Passive Investing Education**

Inspiré de **A Random Walk Down Wall Street**

## **Objectif**

Apprendre aux utilisateurs **les bases de l’investissement passif** :

- ETF

- diversification

- investissement long terme

- régularité d’investissement

Le module doit être **pédagogique et non spéculatif**.

---

# **FEATURE 3.1 — ETF Education**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre ce qu’est un ETF  
 Afin de savoir comment investir simplement.

---

## **Context**

Les jeunes investisseurs :

- confondent ETF et actions

- sont influencés par la spéculation

- ignorent la diversification.

Le module doit expliquer **les ETF de manière simple et pédagogique**.

---

## **User Flow**

1. L’utilisateur ouvre la page **Learn**

2. Il sélectionne la leçon **What is an ETF**

3. Le contenu éducatif est affiché

4. L’utilisateur peut passer à la leçon suivante ou faire un quiz

---

## **Functional Requirements**

Le module doit afficher :

title

definition

example

advantages

risks

summary

Structure du contenu :

Definition

Example

Why it matters

Risks

Exemple :

ETF \= panier d’actions permettant d’investir dans plusieurs entreprises à la fois.

---

## **Acceptance Criteria**

La leçon doit :

- être accessible depuis la page Learn

- contenir un texte pédagogique

- inclure une illustration ou graphique

- inclure un bouton **Start Quiz**

---

## **Edge Cases**

- utilisateur hors ligne

- contenu manquant

- chargement lent

---

## **Data Model**

education_lessons

\- id

\- title

\- content

\- category

\- created_at

---

## **Technical Notes**

Component :

LessonCard

LessonViewer

---

# **FEATURE 3.2 — Simple Portfolio Example**

## **User Story**

En tant qu’utilisateur  
 Je veux voir un exemple de portefeuille simple  
 Afin de comprendre comment investir.

---

## **Context**

Les débutants pensent qu’il faut :

- choisir des actions

- battre le marché.

Le produit doit montrer qu’un **portefeuille simple est souvent plus efficace**.

---

## **User Flow**

1. L’utilisateur ouvre la leçon **Simple Portfolio**

2. L’écran affiche une allocation

3. Chaque élément est expliqué

---

## **Functional Requirements**

Exemple de portefeuille pédagogique :

60% Global ETF

30% Bond ETF

10% Cash

Chaque allocation doit afficher :

asset_name

percentage

description

risk_level

---

## **Acceptance Criteria**

L’utilisateur doit pouvoir :

- voir l’allocation

- cliquer sur chaque élément

- lire une explication

---

## **Edge Cases**

- données ETF manquantes

- affichage mobile trop dense

---

## **Data Model**

portfolio_examples

\- id

\- name

\- allocation

\- description

---

## **Technical Notes**

Component :

PortfolioAllocationChart

---

# **FEATURE 3.3 — Monthly Investment Plan**

## **User Story**

En tant qu’utilisateur  
 Je veux créer un plan d’investissement mensuel  
 Afin d’investir régulièrement.

---

## **Context**

Le **Dollar Cost Averaging (DCA)** permet d’investir progressivement.

Le module doit montrer :

- combien investir

- pendant combien de temps.

---

## **User Flow**

1. L’utilisateur ouvre **Investment Plan**

2. Il saisit un montant mensuel

3. Il choisit un horizon

4. Le plan est calculé

---

## **Functional Requirements**

Inputs :

monthly_investment

investment_horizon

expected_return

Outputs :

total_invested

future_value

total_gain

---

## **Acceptance Criteria**

Le plan doit :

- afficher la projection

- montrer le capital investi

- montrer les gains estimés

---

## **Edge Cases**

- montant \= 0

- horizon très long

- rendement négatif

---

## **Data Model**

investment_plans

\- id

\- user_id

\- monthly_investment

\- expected_return

\- investment_horizon

---

## **Technical Notes**

Component :

InvestmentPlanCalculator

---

# **FEATURE 3.4 — Investment Simulator**

## **User Story**

En tant qu’utilisateur  
 Je veux simuler un investissement  
 Afin de comprendre l’impact du temps.

---

## **Context**

Les intérêts composés sont difficiles à visualiser.

Le simulateur doit rendre cela **visuel et concret**.

---

## **User Flow**

1. L’utilisateur ouvre le simulateur

2. Il configure les paramètres

3. Le graphique se met à jour

---

## **Functional Requirements**

Inputs :

monthly_investment

interest_rate

years

Calcul :

future_value \= P \* ((1+r)^n \- 1\) / r

---

## **Acceptance Criteria**

Le simulateur doit afficher :

- capital final

- capital investi

- intérêts générés

- graphique croissance

---

## **Edge Cases**

- investissement unique

- rendement nul

---

## **Technical Notes**

Component :

CompoundInterestChart

---

# **EPIC 4 — Risk Awareness**

Inspiré de **Fooled by Randomness**

## **Objectif**

Faire comprendre que :

- les marchés sont incertains

- la volatilité est normale

- la diversification réduit les risques.

---

# **FEATURE 4.1 — Volatility Education**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre la volatilité  
 Afin de ne pas paniquer lors des fluctuations.

---

## **Context**

Les débutants pensent que les marchés montent en ligne droite.

---

## **User Flow**

1. L’utilisateur ouvre la leçon

2. Une animation montre les fluctuations

3. Une explication pédagogique apparaît

---

## **Functional Requirements**

La leçon doit expliquer :

volatility_definition

market_fluctuations

long_term_trend

---

## **Acceptance Criteria**

La leçon doit inclure :

- graphique volatilité

- exemple historique

- explication simple

---

## **Edge Cases**

- utilisateur sans expérience financière

---

## **Data Model**

Réutilise :

education_lessons

---

# **FEATURE 4.2 — Market Crash Simulation**

## **User Story**

En tant qu’utilisateur  
 Je veux simuler une crise financière  
 Afin de comprendre le risque.

---

## **Context**

Les investisseurs paniquent souvent lors des baisses.

---

## **User Flow**

1. L’utilisateur lance la simulation

2. Le marché chute

3. La récupération est affichée

---

## **Functional Requirements**

Paramètres :

market_drop \= \-30%

recovery_period \= 3 years

---

## **Acceptance Criteria**

Le graphique doit afficher :

- baisse

- récupération

- horizon long terme

---

## **Edge Cases**

- chute plus forte

- récupération lente

---

## **Technical Notes**

Component :

MarketCrashChart

---

# **FEATURE 4.3 — Diversification Module**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre la diversification  
 Afin de réduire mon risque.

---

## **Context**

Diversifier permet de :

- réduire la volatilité

- protéger le portefeuille.

---

## **User Flow**

1. L’utilisateur ouvre la leçon

2. Une comparaison apparaît

3. L’impact de la diversification est expliqué

---

## **Functional Requirements**

Comparer :

single_asset_portfolio

diversified_portfolio

---

## **Acceptance Criteria**

Afficher :

- volatilité

- performance

- risque

---

## **Technical Notes**

Component :

DiversificationComparisonChart

---

# **EPIC 5 — Behavioral Finance Coaching**

Inspiré de **Thinking Fast and Slow**

## **Objectif**

Aider l’utilisateur à **éviter les erreurs psychologiques**.

---

# **FEATURE 5.1 — FOMO Detection**

## **User Story**

En tant qu’utilisateur  
 Je veux être averti si je prends une décision impulsive  
 Afin d’éviter une erreur.

---

## **Context**

Le **Fear Of Missing Out** pousse les investisseurs à acheter au mauvais moment.

---

## **User Flow**

1. L’utilisateur demande un conseil

2. L’assistant détecte un biais

3. Une alerte pédagogique apparaît

---

## **Functional Requirements**

Détection :

rapid_price_increase

social_media_hype

short_term_intent

---

## **Acceptance Criteria**

L’assistant doit :

- expliquer le biais

- proposer une alternative

---

## **Technical Notes**

Module :

bias_detection_engine

---

# **FEATURE 5.2 — Cognitive Bias Education**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre les biais cognitifs  
 Afin d’améliorer mes décisions.

---

## **Context**

Les principaux biais :

- FOMO

- confirmation bias

- overconfidence

- loss aversion

---

## **Functional Requirements**

Chaque biais contient :

definition

example

impact

how_to_avoid

---

## **Data Model**

behavioral_lessons

\- id

\- bias_name

\- explanation

---

# **FEATURE 5.3 — Decision Coaching**

## **User Story**

En tant qu’utilisateur  
 Je veux être guidé avant une décision financière  
 Afin de réfléchir.

---

## **Context**

Avant une décision importante, l’assistant pose des questions.

---

## **User Flow**

1. L’utilisateur demande un conseil

2. L’assistant lance un questionnaire

3. La réponse est analysée

4. Une recommandation apparaît

---

## **Functional Requirements**

Questions :

why_are_you_investing

investment_horizon

risk_tolerance

financial_goal

---

## **Acceptance Criteria**

Le coaching doit :

- ralentir la décision

- expliquer les risques

- encourager long terme

# **EPIC 6 — AI Financial Assistant**

## **Objectif**

Permettre à l’utilisateur d’interagir avec un **assistant financier intelligent** qui :

- répond aux questions financières

- explique les concepts

- donne un coaching comportemental

- s’appuie sur la situation financière de l’utilisateur.

L’assistant doit être **éducatif et non spéculatif**.

---

# **FEATURE 6.1 — Ask Financial Questions**

## **User Story**

En tant qu’utilisateur  
 Je veux poser une question financière à l’assistant  
 Afin de comprendre un concept financier ou une décision d’épargne/investissement.

---

## **Context**

Les utilisateurs 18-25 :

- ont peu de connaissances financières

- sont influencés par les réseaux sociaux

- ont besoin d’explications simples.

L’assistant doit répondre avec :

- pédagogie

- simplicité

- neutralité.

---

## **User Flow**

1. L’utilisateur ouvre l’onglet **Assistant**

2. Une interface chat apparaît

3. L’utilisateur tape une question

4. La question est envoyée à l’API IA

5. La réponse est générée

6. La réponse est affichée dans le chat

---

## **Functional Requirements**

Interface chat contenant :

user_input

assistant_response

timestamp

conversation_history

Fonctions principales :

- envoyer message

- recevoir réponse

- afficher historique conversation

Le système doit :

- conserver les 20 derniers messages

- afficher indicateur "assistant typing"

---

## **Acceptance Criteria**

L’utilisateur peut :

- envoyer un message

- recevoir une réponse

- voir l’historique

La réponse doit :

- être pédagogique

- éviter les conseils spéculatifs

- encourager la discipline financière

---

## **Edge Cases**

- message vide

- message trop long

- erreur API IA

- perte de connexion

---

## **Data Model**

assistant_conversations

\- id

\- user_id

\- created_at

assistant_messages

\- id

\- conversation_id

\- role (user / assistant)

\- content

\- created_at

---

## **Technical Notes**

Service :

ai_assistant_service

Utilise :

Claude API

Prompt système :

- finance éducative

- long terme

- éviter trading

---

# **FEATURE 6.2 — Educational Explanation**

## **User Story**

En tant qu’utilisateur  
 Je veux recevoir des explications financières simples  
 Afin d’apprendre les bases de la finance.

---

## **Context**

Les concepts financiers doivent être **traduits en langage simple**.

Exemples :

ETF  
 volatilité  
 diversification  
 intérêts composés

---

## **User Flow**

1. L’utilisateur pose une question

2. L’assistant détecte un concept financier

3. L’assistant génère une explication pédagogique

4. Une carte éducative peut apparaître

---

## **Functional Requirements**

Les explications doivent contenir :

definition

example

risk

simple_summary

Exemple :

ETF :

Definition

Example

Why it matters

Risks

---

## **Acceptance Criteria**

Chaque explication doit :

- contenir une définition

- contenir un exemple

- expliquer les risques

---

## **Edge Cases**

- concept trop complexe

- réponse trop longue

- sujet non financier

---

## **Data Model**

Optionnel :

financial_concepts

\- id

\- concept_name

\- description

\- example

---

## **Technical Notes**

Possibilité de créer une **base de concepts financiers internes**.

---

# **FEATURE 6.3 — Personalized Coaching**

## **User Story**

En tant qu’utilisateur  
 Je veux recevoir des conseils personnalisés  
 Afin d’améliorer ma gestion financière.

---

## **Context**

Le coaching doit utiliser :

- profil financier

- dépenses

- objectifs

---

## **User Flow**

1. L’assistant analyse les données utilisateur

2. Il détecte des opportunités d’amélioration

3. Il propose une recommandation

Exemples :

- augmenter épargne

- réduire dépenses

- investir régulièrement

---

## **Functional Requirements**

Analyse :

income

expenses

savings_rate

goals

Le coaching peut générer :

recommendation_type

explanation

suggested_action

---

## **Acceptance Criteria**

Les recommandations doivent :

- être personnalisées

- être simples

- éviter les conseils spéculatifs

---

## **Edge Cases**

- données utilisateur insuffisantes

- utilisateur sans revenu

- utilisateur sans objectif

---

## **Data Model**

assistant_recommendations

\- id

\- user_id

\- recommendation_type

\- message

\- created_at

---

# **EPIC 7 — Financial Projections**

## **Objectif**

Permettre à l’utilisateur de **visualiser l’impact futur de ses décisions financières**.

Les projections doivent rendre **le long terme concret**.

---

# **FEATURE 7.1 — Future Wealth Simulation**

## **User Story**

En tant qu’utilisateur  
 Je veux simuler mon patrimoine futur  
 Afin de comprendre l’impact de mon épargne et de mes investissements.

---

## **Context**

Les jeunes ont du mal à visualiser :

- la croissance du capital

- les intérêts composés.

---

## **User Flow**

1. L’utilisateur ouvre la page **Projection**

2. Il renseigne les paramètres

3. Le simulateur calcule la projection

4. Le graphique s’affiche

---

## **Functional Requirements**

Inputs :

monthly_saving

monthly_investment

expected_return

investment_horizon

Calcul :

future_value \= P \* ((1+r)^n \- 1\) / r

---

## **Acceptance Criteria**

La simulation doit afficher :

- capital final

- capital investi

- intérêts générés

- graphique évolution

---

## **Edge Cases**

- rendement négatif

- horizon très long

- investissement \= 0

---

## **Data Model**

Pas obligatoire (simulation calculée côté client).

---

## **Technical Notes**

Component :

WealthProjectionChart

---

# **FEATURE 7.2 — Scenario Comparison**

## **User Story**

En tant qu’utilisateur  
 Je veux comparer plusieurs scénarios financiers  
 Afin de comprendre l’impact de mes décisions.

---

## **Context**

Comparer :

- épargne simple

- investissement

- combinaison.

---

## **User Flow**

1. L’utilisateur configure scénario A

2. L’utilisateur configure scénario B

3. Les résultats sont comparés

---

## **Functional Requirements**

Scénarios :

Scenario A

Scenario B

Affichage :

capital_final

capital_invested

difference

---

## **Acceptance Criteria**

L’utilisateur doit pouvoir :

- modifier les paramètres

- voir l’impact immédiatement

---

## **Edge Cases**

- scénarios identiques

- valeurs incohérentes

---

## **Technical Notes**

Component :

ScenarioComparisonChart

---

# **EPIC 8 — Gamification**

## **Objectif**

Créer de l’engagement et encourager les bonnes habitudes financières.

---

# **FEATURE 8.1 — Financial Score Progression**

## **User Story**

En tant qu’utilisateur  
 Je veux améliorer mon score financier  
 Afin de mesurer mes progrès.

---

## **Context**

Un score simple rend la progression visible.

---

## **User Flow**

1. L’utilisateur consulte son score

2. Il voit comment l’améliorer

3. Le score évolue avec ses actions

---

## **Functional Requirements**

Score basé sur :

savings_rate

goal_progress

budget_control

learning_progress

---

## **Acceptance Criteria**

Le score doit :

- évoluer automatiquement

- être expliqué

---

## **Edge Cases**

- score maximum atteint

- score très bas

---

## **Data Model**

financial_scores

\- user_id

\- score

\- updated_at

---

# **FEATURE 8.2 — Educational Badges**

## **User Story**

En tant qu’utilisateur  
 Je veux débloquer des badges  
 Afin de rester motivé.

---

## **Context**

Les badges récompensent les comportements positifs.

---

## **Functional Requirements**

Badges :

first_goal

first_100_saved

first_investment_plan

first_quiz_completed

---

## **Acceptance Criteria**

Un badge doit être attribué :

- automatiquement

- une seule fois

---

## **Data Model**

badges

\- id

\- name

\- description

user_badges

\- user_id

\- badge_id

\- unlocked_at

---

# **FEATURE 8.3 — Financial Quiz**

## **User Story**

En tant qu’utilisateur  
 Je veux tester mes connaissances financières  
 Afin d’apprendre la finance.

---

## **Context**

Les quiz renforcent l’apprentissage.

---

## **Functional Requirements**

Chaque quiz contient :

question

options

correct_answer

explanation

---

## **Acceptance Criteria**

Après un quiz :

- score affiché

- explication réponse

---

## **Data Model**

quizzes

\- id

\- question

\- options

\- correct_answer

quiz_results

\- user_id

\- quiz_id

\- score

---

#

# **EPIC 9 — AI Financial Coaching Assistant**

## **Objectif**

Créer un assistant financier intelligent capable d’aider l’utilisateur à :

- comprendre ses finances

- améliorer sa discipline d’épargne

- apprendre l’investissement passif

- éviter les biais comportementaux

- prendre des décisions financières rationnelles.

L’assistant agit comme un **coach éducatif** inspiré de :

- Random Walk Down Wall Street

- Fooled by Randomness

- Thinking Fast and Slow.

---

# **FEATURE 9.1 — Financial Situation Analysis**

## **User Story**

En tant qu’utilisateur  
 Je veux que l’assistant analyse ma situation financière  
 Afin de comprendre mon budget et ma capacité d’épargne.

---

## **Context**

Les utilisateurs ont rarement une vision claire de leur situation financière.

L’assistant doit analyser :

- revenu

- dépenses

- objectifs

- score financier.

---

## **User Flow**

1. L’utilisateur ouvre l’interface assistant

2. Il demande une analyse financière

3. L’assistant récupère les données utilisateur

4. L’assistant génère une analyse claire

5. L’utilisateur peut poser des questions supplémentaires.

---

## **Functional Requirements**

L’assistant doit analyser :

`monthly_income`

`expenses`

`goals`

`financial_score`

Produire :

`budget_summary`

`expense_breakdown`

`saving_capacity`

`spending_alerts`

Exemples de réponse :

"Tu dépenses 40% de ton revenu en logement."

---

## **Acceptance Criteria**

L’assistant doit :

- analyser les dépenses par catégorie

- identifier les postes de dépenses élevés

- expliquer la capacité d’épargne.

---

## **Edge Cases**

- utilisateur sans revenu

- aucune dépense enregistrée

- données financières incomplètes.

---

## **Data Model**

Utilise les tables :

`financial_profiles`

`expenses`

`goals`

`financial_scores`

---

## **Technical Notes**

Service :

`financial_analysis_service`

Le bot utilise :

`get_user_financial_profile()`

`get_user_expenses()`

`get_user_goals()`

`get_financial_score()`

---

# **FEATURE 9.2 — Savings Coaching**

## **User Story**

En tant qu’utilisateur  
 Je veux recevoir des conseils d’épargne  
 Afin d’atteindre mes objectifs plus rapidement.

---

## **Context**

Les utilisateurs ont du mal à maintenir une discipline d’épargne.

L’assistant doit agir comme un **coach d’épargne**.

---

## **User Flow**

1. L’utilisateur demande un conseil

2. L’assistant analyse les objectifs

3. L’assistant calcule les progrès

4. L’assistant propose des optimisations.

---

## **Functional Requirements**

Le bot doit analyser :

`goal_target`

`goal_progress`

`monthly_saving`

`saving_rate`

Produire :

`goal_completion_estimate`

`saving_optimization`

`progress_summary`

---

## **Acceptance Criteria**

Le bot doit :

- calculer le temps restant pour atteindre l’objectif

- proposer des ajustements d’épargne

- expliquer les résultats.

---

## **Edge Cases**

- objectif déjà atteint

- contribution mensuelle nulle

- objectif trop ambitieux.

---

## **Data Model**

Utilise :

`goals`

`goal_contributions`

---

## **Technical Notes**

Service :

`saving_coaching_service`

Fonctions :

`calculate_goal_progress()`

`estimate_goal_completion()`

`optimize_monthly_saving()`

---

# **FEATURE 9.3 — Investment Education Assistant**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre les bases de l’investissement  
 Afin d’investir simplement.

---

## **Context**

Les jeunes investisseurs sont exposés à :

- trading

- hype investing

- crypto spéculative.

L’assistant doit enseigner :

- ETF

- diversification

- long terme.

---

## **User Flow**

1. L’utilisateur pose une question

2. L’assistant identifie le concept financier

3. L’assistant génère une explication pédagogique.

---

## **Functional Requirements**

Le bot doit expliquer :

`ETF`

`diversification`

`compound_interest`

`long_term_investing`

Chaque explication contient :

`definition`

`example`

`risk`

`summary`

---

## **Acceptance Criteria**

Les réponses doivent être :

- pédagogiques

- simples

- neutres.

---

## **Edge Cases**

- question trop technique

- sujet non financier.

---

## **Data Model**

Optionnel :

`financial_concepts`

`- id`

`- concept_name`

`- explanation`

---

## **Technical Notes**

Prompt IA spécialisé :

`investment_education_prompt`

---

# **FEATURE 9.4 — Investment Simulation**

## **User Story**

En tant qu’utilisateur  
 Je veux simuler un investissement  
 Afin de comprendre l’impact du temps.

---

## **Context**

Les intérêts composés sont difficiles à visualiser.

Le simulateur permet de rendre cela concret.

---

## **User Flow**

1. L’utilisateur demande une simulation

2. Il fournit les paramètres

3. Le système calcule la projection

4. L’assistant explique les résultats.

---

## **Functional Requirements**

Inputs :

`monthly_investment`

`expected_return`

`investment_horizon`

Outputs :

`capital_invested`

`capital_final`

`interest_gain`

Calcul :

`future_value = P * ((1+r)^n - 1) / r`

---

## **Acceptance Criteria**

L’assistant doit :

- exécuter la simulation

- expliquer les résultats.

---

## **Edge Cases**

- rendement négatif

- horizon très long

- investissement nul.

---

## **Data Model**

Pas nécessaire (calcul dynamique).

---

## **Technical Notes**

Service :

`investment_simulator_service`

---

# **FEATURE 9.5 — Risk Education**

## **User Story**

En tant qu’utilisateur  
 Je veux comprendre les risques financiers  
 Afin de ne pas paniquer lors des fluctuations.

---

## **Context**

Inspiré de :

Fooled by Randomness.

---

## **User Flow**

1. L’utilisateur pose une question sur le risque

2. L’assistant explique la volatilité

3. L’assistant montre des exemples.

---

## **Functional Requirements**

L’assistant doit expliquer :

`market_volatility`

`market_cycles`

`market_crashes`

`diversification`

---

## **Acceptance Criteria**

Les explications doivent :

- être simples

- inclure des exemples.

---

## **Edge Cases**

- utilisateur anxieux

- question sur spéculation.

---

## **Data Model**

Réutilise :

`financial_concepts`

---

## **Technical Notes**

Module :

`risk_education_prompt`

---

# **FEATURE 9.6 — Behavioral Bias Detection**

## **User Story**

En tant qu’utilisateur  
 Je veux être averti si je prends une décision impulsive  
 Afin d’éviter les erreurs financières.

---

## **Context**

Inspiré de :

Thinking Fast and Slow.

---

## **User Flow**

1. L’utilisateur exprime une intention d’investissement

2. L’assistant analyse le message

3. Un biais potentiel est détecté

4. L’assistant explique le biais.

---

## **Functional Requirements**

Détection :

`FOMO`

`overconfidence`

`confirmation_bias`

`loss_aversion`

---

## **Acceptance Criteria**

Lorsqu’un biais est détecté :

- l’assistant explique le biais

- propose une alternative rationnelle.

---

## **Edge Cases**

- détection erronée

- message ambigu.

---

## **Data Model**

`behavioral_events`

`- id`

`- user_id`

`- bias_type`

`- detected_at`

---

## **Technical Notes**

Module :

`bias_detection_engine`

---

# **FEATURE 9.7 — Decision Coaching**

## **User Story**

En tant qu’utilisateur  
 Je veux être guidé avant une décision financière  
 Afin de réfléchir rationnellement.

---

## **Context**

Avant une décision importante, l’assistant agit comme un **frein cognitif**.

---

## **User Flow**

1. L’utilisateur demande un conseil

2. L’assistant pose des questions

3. Les réponses sont analysées

4. Une recommandation apparaît.

---

## **Functional Requirements**

Questions :

`why_are_you_investing`

`investment_horizon`

`risk_tolerance`

`financial_goal`

---

## **Acceptance Criteria**

Le coaching doit :

- encourager la réflexion

- éviter les décisions impulsives.

---

## **Edge Cases**

- utilisateur impatient

- horizon incohérent.

---

## **Data Model**

`decision_sessions`

`- user_id`

`- decision_context`

`- answers`

---

## **Technical Notes**

Service :

`decision_coaching_service`

---

# **FEATURE 9.8 — Financial Projections**

## **User Story**

En tant qu’utilisateur  
 Je veux voir des projections financières  
 Afin de comprendre mon futur financier.

---

## **Context**

Les projections rendent le long terme concret.

---

## **User Flow**

1. L’utilisateur demande une projection

2. Le système récupère les données

3. Une simulation est calculée

4. L’assistant explique les résultats.

---

## **Functional Requirements**

Le bot peut :

`simulate_future_wealth`

`compare_scenarios`

---

## **Acceptance Criteria**

La projection doit afficher :

- capital final

- capital investi

- gains.

---

## **Edge Cases**

- horizon très long

- rendement faible.

---

## **Data Model**

Utilise :

`investment_plans`

`financial_profiles`

---

## **Technical Notes**

Service :

`financial_projection_service`

---

# **FEATURE 9.9 — Financial Motivation**

## **User Story**

En tant qu’utilisateur  
 Je veux recevoir des encouragements  
 Afin de maintenir mes bonnes habitudes financières.

---

## **Context**

La motivation aide à maintenir la discipline financière.

---

## **User Flow**

1. L’assistant analyse la progression

2. Il détecte un progrès

3. Il envoie un message motivant.

---

## **Functional Requirements**

Analyse :

`saving_streak`

`goal_progress`

`financial_score`

---

## **Acceptance Criteria**

L’assistant doit :

- encourager les progrès

- valoriser la discipline.

---

## **Edge Cases**

- progrès minimes

- utilisateur inactif.

---

## **Data Model**

Utilise :

`saving_streaks`

`goals`

`financial_scores`

---

## **Technical Notes**

Service :

`motivation_engine`

# **FEATURE 9.10 — Goal Creation Assistant**

## **User Story**

En tant qu’utilisateur  
 Je veux que l’assistant m’aide à créer un objectif financier  
 Afin de transformer une intention vague en objectif clair, réaliste et suivi dans l’application.

---

## **Context**

Beaucoup d’utilisateurs savent qu’ils veulent :

- voyager

- se créer une épargne de sécurité

- acheter quelque chose

- commencer à investir

mais ne savent pas :

- quel montant viser

- sur quelle durée

- combien épargner par mois

- si l’objectif est réaliste.

L’assistant doit donc jouer un rôle de **traducteur d’intention → objectif structuré**.

Cette feature doit être utilisable :

- dans le **tool assistant**

- dans la **vue Goals**

- dans les flows d’onboarding ou de coaching.

---

## **User Flow**

user opens assistant or goals view  
↓  
user expresses a financial intention  
↓  
assistant asks clarification questions  
↓  
assistant builds a structured goal proposal  
↓  
user validates or edits the goal  
↓  
goal is saved in the Goals module  
↓  
goal becomes available for tracking and coaching

Exemples de déclencheurs :

- “Je veux partir au Japon l’année prochaine”

- “Je veux me constituer une épargne de secours”

- “Je veux économiser pour un ordinateur”

- “Je veux commencer à investir”

---

## **Functional Requirements**

L’assistant doit pouvoir :

### **1\. détecter l’intention de créer un objectif**

Entrées possibles :

free_text_goal_intent  
goal_type_hint  
target_date_hint  
budget_hint

Exemples :

- “Je veux économiser pour un voyage”

- “Je veux 3000€ d’ici 10 mois”

- “Je veux commencer un fonds d’urgence”

---

### **2\. poser des questions de clarification**

Le bot doit pouvoir demander :

goal_name  
goal_type  
target_amount  
target_date  
priority  
monthly_contribution_optional

Questions typiques :

- Quel est ton objectif exact ?

- Pour quand souhaites-tu l’atteindre ?

- As-tu déjà un montant en tête ?

- Est-ce un objectif prioritaire ?

- Veux-tu une estimation de l’épargne mensuelle nécessaire ?

---

### **3\. proposer un objectif structuré**

Le système doit être capable de générer un objet objectif contenant :

goal_name  
goal_type  
target_amount  
target_date  
recommended_monthly_contribution  
goal_feasibility  
assistant_summary

Exemple :

goal_name: Voyage au Japon  
goal_type: travel  
target_amount: 3000  
target_date: 2027-06-01  
recommended_monthly_contribution: 250  
goal_feasibility: realistic  
assistant_summary: Avec 250€ par mois, tu peux atteindre cet objectif en 12 mois.

---

### **4\. vérifier la faisabilité de l’objectif**

Le bot doit comparer :

monthly_income  
monthly_expenses  
saving_capacity  
existing_goals  
requested_target_amount  
requested_target_date

Le système doit pouvoir qualifier l’objectif :

realistic  
challenging  
unrealistic

---

### **5\. proposer des ajustements si nécessaire**

Si l’objectif n’est pas réaliste, l’assistant doit proposer :

- une date plus réaliste

- une contribution mensuelle plus élevée

- un montant cible révisé

- une priorité différente.

---

### **6\. sauvegarder l’objectif dans la vue Goals**

Une fois validé, l’objectif doit être :

- créé dans le module Goals

- visible dans la liste des objectifs

- utilisable pour le suivi, les projections et la motivation.

---

### **7\. permettre l’édition depuis la vue Goals**

Depuis la vue Goals, l’utilisateur doit pouvoir :

- accepter la proposition IA

- modifier les champs

- supprimer l’objectif

- demander une reformulation ou optimisation.

---

## **Acceptance Criteria**

- l’utilisateur peut exprimer un objectif en langage naturel

- l’assistant pose des questions si les informations sont incomplètes

- l’assistant propose un objectif structuré

- le système calcule la contribution mensuelle recommandée

- l’objectif est marqué comme réaliste / ambitieux / irréaliste

- l’utilisateur peut confirmer ou modifier avant sauvegarde

- l’objectif sauvegardé apparaît dans la vue Goals

- l’objectif créé devient exploitable par les autres modules.

---

## **Edge Cases**

- utilisateur sans revenu

- utilisateur sans capacité d’épargne positive

- objectif sans date

- objectif sans montant

- objectif très ambitieux par rapport au budget

- plusieurs objectifs concurrents déjà existants

- saisie vague du type “je veux mettre de l’argent de côté”

- date cible déjà passée

- montant cible nul ou incohérent.

---

## **Data Model**

Réutilise principalement :

goals  
\- id  
\- user_id  
\- goal_name  
\- goal_type  
\- target_amount  
\- target_date  
\- monthly_contribution  
\- created_at  
\- updated_at

Ajouts recommandés si besoin :

goal_assistant_sessions  
\- id  
\- user_id  
\- raw_intent  
\- proposed_goal_name  
\- proposed_goal_type  
\- proposed_target_amount  
\- proposed_target_date  
\- recommended_monthly_contribution  
\- feasibility_status  
\- created_at

goal_assistant_messages  
\- id  
\- session_id  
\- role  
\- content  
\- created_at

# **FEATURE 9.11 — Expense Creation Assistant**

## **User Story**

En tant qu’utilisateur  
 Je veux que l’assistant m’aide à enregistrer une dépense  
 Afin de suivre facilement mes dépenses sans devoir remplir un formulaire manuel.

---

# **Context**

Beaucoup d’utilisateurs :

- oublient d’enregistrer leurs dépenses

- trouvent les formulaires trop longs

- ne savent pas toujours catégoriser leurs achats.

L’assistant doit permettre :

- une **saisie naturelle**

- une **catégorisation automatique**

- un **enregistrement rapide dans le budget**.

Exemples de messages utilisateur :

- “J’ai payé 12€ pour un kebab”

- “J’ai pris un abonnement Netflix à 15€”

- “J’ai payé 80€ d’essence”

- “Restaurant hier soir 32€”

L’assistant agit comme un **parser intelligent de dépenses**.

---

# **User Flow**

user opens assistant  
 ↓  
 user writes expense in natural language  
 ↓  
 assistant extracts expense data  
 ↓  
 assistant proposes structured expense  
 ↓  
 user confirms or edits  
 ↓  
 expense is saved in Expense module  
 ↓  
 budget dashboard is updated

---

# **Functional Requirements**

L’assistant doit pouvoir :

---

## **1 — détecter une intention de création de dépense**

Entrées possibles :

free_text_expense  
amount_hint  
category_hint  
date_hint  
description_hint

Exemples :

- “J’ai dépensé 20€ en nourriture”

- “Uber 14€”

- “Netflix 15€ par mois”

- “Courses 60€ aujourd’hui”

---

## **2 — extraire automatiquement les informations**

L’IA doit tenter d’extraire :

amount  
category  
description  
expense_date

Exemple :

Input :

“J’ai payé 12€ pour un kebab”

Extraction :

amount: 12  
category: food  
description: kebab  
expense_date: today

---

## **3 — proposer une dépense structurée**

Le système doit générer un objet :

amount  
category  
description  
expense_date  
assistant_summary

Exemple :

amount: 12  
category: food  
description: kebab  
expense_date: 2026-03-15  
assistant_summary:  
"J’ai détecté une dépense de 12€ en nourriture."

---

## **4 — catégorisation automatique**

L’assistant doit mapper la dépense vers les catégories existantes :

housing  
transport  
food  
subscriptions  
entertainment  
shopping  
health  
other

Exemples :

Uber → transport  
 Netflix → subscriptions  
 Restaurant → food  
 Amazon → shopping

---

## **5 — demander des précisions si nécessaire**

Si les données sont incomplètes, le bot doit poser des questions :

Exemples :

- “Quel est le montant de la dépense ?”

- “Quelle catégorie correspond le mieux ?”

- “Quand cette dépense a-t-elle eu lieu ?”

---

## **6 — confirmer avant sauvegarde**

Avant enregistrement :

assistant:  
"Tu veux enregistrer une dépense de 15€ pour Netflix aujourd’hui ?"

Options :

confirm  
edit  
cancel

---

## **7 — sauvegarder la dépense**

Une fois confirmée :

- la dépense est créée dans `expenses`

- les statistiques sont recalculées

- le dashboard est mis à jour.

---

# **Acceptance Criteria**

- l’utilisateur peut saisir une dépense en langage naturel

- l’assistant détecte le montant automatiquement

- l’assistant propose une catégorie

- l’utilisateur peut corriger les informations

- la dépense est enregistrée dans `expenses`

- le budget est recalculé immédiatement.

---

# **Edge Cases**

- montant absent

- montant incohérent

- catégorie inconnue

- message ambigu

- plusieurs dépenses dans une phrase

- dépense future

- devise différente

- message du type :

“J’ai dépensé un peu d’argent”

---

# **Data Model**

Réutilise la table existante :

expenses

id  
user_id  
amount  
category  
description  
expense_date  
created_at

Tables optionnelles :

expense_assistant_sessions

id  
user_id  
raw_message  
detected_amount  
detected_category  
detected_description  
detected_date  
created_at  
expense_assistant_messages

id  
session_id  
role  
content  
created_at

---

# **Technical Notes**

Service principal :

expense_creation_assistant_service

Fonctions :

detect_expense_intent()  
extract_expense_entities()  
categorize_expense()  
generate_expense_proposal()  
save_expense()

Pipeline :

user_message  
 ↓  
intent_detection  
 ↓  
entity_extraction  
 ↓  
category_mapping  
 ↓  
expense_proposal  
 ↓  
user_confirmation  
 ↓  
expense_saved

Technologies possibles :

- LLM extraction

- regex fallback pour montants

- dictionnaire catégories.

# **FEATURE 9.12 — Personal Financial Projection Assistant**

## **User Story**

En tant qu’utilisateur  
 Je veux que l’assistant projette ma situation financière future  
 Afin de comprendre l’impact de mon épargne et de mes investissements dans le temps.

---

# **Context**

Les utilisateurs ont du mal à comprendre :

- l’impact du temps

- les intérêts composés

- l’effet d’une épargne régulière.

L’assistant doit permettre de poser des questions comme :

- “Combien j’aurai dans 10 ans si j’épargne 200€ par mois ?”

- “Si j’investis 300€ par mois à 7%, combien j’aurai à 40 ans ?”

- “Que se passe-t-il si j’augmente mon épargne de 50€ ?”

L’objectif est de transformer **des concepts abstraits en projections concrètes**.

---

# **User Flow**

user opens assistant  
 ↓  
 user asks a projection question  
 ↓  
 assistant extracts parameters  
 ↓  
 assistant runs financial projection  
 ↓  
 assistant explains results  
 ↓  
 assistant proposes alternative scenarios

---

# **Functional Requirements**

L’assistant doit pouvoir :

---

## **1 — détecter une intention de projection financière**

Entrées possibles :

projection_intent  
investment_question  
future_wealth_question  
retirement_projection

Exemples :

- “Combien j’aurai dans 15 ans ?”

- “Si j’investis 200€ par mois à 6%”

- “Que se passe-t-il si j’épargne plus ?”

---

## **2 — extraire les paramètres financiers**

L’assistant doit identifier :

monthly_saving  
monthly_investment  
interest_rate  
investment_horizon  
initial_capital

Exemple :

Input :

“Si j’investis 300€ par mois pendant 20 ans à 7%”

Extraction :

monthly_investment: 300  
interest_rate: 0.07  
investment_horizon: 20  
initial_capital: 0

---

## **3 — récupérer les données utilisateur si nécessaires**

Si l’utilisateur ne donne pas tous les paramètres :

le système peut utiliser :

financial_profile  
monthly_income  
expenses  
saving_rate  
goals

Exemple :

“Si je continue comme aujourd’hui”

---

## **4 — exécuter la simulation financière**

Calcul principal :

future_value \= P \* ((1+r)^n \- 1\) / r

Variables :

P \= monthly contribution  
r \= monthly interest rate  
n \= number of months

Le système doit calculer :

total_invested  
future_value  
interest_generated

---

## **5 — générer une explication pédagogique**

L’assistant doit expliquer :

- capital investi

- intérêts générés

- importance du temps.

Exemple :

“Si tu investis 300€ par mois pendant 20 ans avec un rendement moyen de 7%, tu investiras 72 000€ et ton capital pourrait atteindre environ 156 000€.  
 Les intérêts composés représentent donc plus de 84 000€.”

---

## **6 — proposer des scénarios alternatifs**

Le système doit pouvoir générer :

scenario_increase_saving  
scenario_longer_horizon  
scenario_higher_return

Exemple :

“Si tu augmentes ton investissement à 400€ par mois, ton capital pourrait atteindre 208 000€.”

---

## **7 — afficher une projection graphique**

L’assistant peut envoyer un payload pour afficher :

projection_chart  
capital_curve  
interest_curve

Utilisé par le composant :

## WealthProjectionChart

# **Acceptance Criteria**

- l’utilisateur peut poser une question de projection en langage naturel

- l’assistant extrait automatiquement les paramètres

- la projection est calculée correctement

- l’assistant explique les résultats

- l’utilisateur peut tester d’autres scénarios

- un graphique peut être affiché.

---

# **Edge Cases**

- rendement négatif

- horizon très long (\> 50 ans)

- montant mensuel nul

- paramètres incohérents

- utilisateur sans revenu

- utilisateur sans capacité d’épargne

- question vague :

“Combien je serai riche ?”

---

# **Data Model**

Pas obligatoire (simulation dynamique).

Optionnel :

projection_sessions

id  
user_id  
monthly_investment  
interest_rate  
investment_horizon  
initial_capital  
future_value  
created_at

---

# **Technical Notes**

Service :

financial_projection_assistant_service

Fonctions :

detect_projection_intent()  
extract_projection_parameters()  
calculate_future_value()  
generate_projection_explanation()  
generate_scenario_variants()

# **EPIC 10 — Security & Access Control**

## **Objectif**

Sécuriser l’application pour un usage réel :

- authentification

- gestion des rôles

- protection API

- protection données.

---

# **FEATURE 10.1 — Authentication System**

### **User Story**

En tant qu’utilisateur  
 Je veux me connecter de manière sécurisée  
 Afin d’accéder à mon compte.

### **Context**

L’application manipule des données financières personnelles.

### **User Flow**

signup  
↓  
email verification  
↓  
login  
↓  
session active

### **Functional Requirements**

- signup

- login

- logout

- password hashing

- session token.

### **Acceptance Criteria**

- mot de passe hashé

- token sécurisé

- session expirée automatiquement.

### **Data Model**

users  
\- id  
\- email  
\- password_hash  
\- role  
\- created_at

### **Technical Notes**

JWT  
bcrypt  
Quarkus security

---

# **FEATURE 10.2 — Role Based Access Control**

### **User Story**

En tant que système  
 Je veux gérer des rôles  
 Afin de restreindre certaines actions.

### **Roles**

USER  
ADMIN

### **Permissions**

USER

- utiliser l’app

- créer profil

- simulations

- IA.

ADMIN

- accès back-office

- gestion utilisateurs

- gestion contenu.

### **Acceptance Criteria**

- endpoints admin protégés

- accès refusé si rôle incorrect.

---

# **FEATURE 10.3 — API Security**

### **User Story**

En tant que système  
 Je veux sécuriser les endpoints  
 Afin d’éviter les accès non autorisés.

### **Functional Requirements**

- validation token JWT

- middleware auth

- protection endpoints sensibles.

### **Edge Cases**

- token expiré

- token invalide.

---

# **FEATURE 10.4 — Rate Limiting**

### **User Story**

En tant que système  
 Je veux limiter certaines requêtes  
 Afin d’éviter les abus.

### **Limites**

login attempts  
AI requests  
API requests

### **Technical Notes**

Quarkus rate limiter  
Redis

---

# **FEATURE 10.5 — Data Protection**

### **User Story**

En tant qu’utilisateur  
 Je veux que mes données soient protégées.

### **Functional Requirements**

- encryption données sensibles

- suppression compte

- anonymisation analytics.

---

# **EPIC 11 — Observability & Monitoring**

## **Objectif**

Surveiller l’application en production.

---

# **FEATURE 11.1 — Error Tracking**

### **User Story**

En tant que développeur  
 Je veux suivre les erreurs  
 Afin de corriger les bugs rapidement.

### **Functional Requirements**

Capturer :

frontend errors  
backend errors  
stack traces

### **Technical Notes**

## Sentry

# **FEATURE 11.2 — Application Logging**

### **User Story**

En tant que développeur  
 Je veux accéder aux logs applicatifs.

### **Logs**

auth logs  
API calls  
AI calls  
simulation  
errors

### **Tools**

ELK stack  
Loki

---

# **FEATURE 11.3 — System Metrics**

### **User Story**

En tant que développeur  
 Je veux surveiller les performances.

### **Metrics**

API latency  
request count  
error rate  
CPU usage  
memory usage

### **Tools**

Prometheus  
Grafana

---

# **FEATURE 11.4 — Product Analytics**

### **User Story**

En tant que product manager  
 Je veux analyser l’usage du produit.

### **Events**

user_signup  
profile_created  
goal_created  
simulation_run  
ai_question_asked

### **Tool**

## Posthog

# **FEATURE 11.5 — AI Usage Monitoring**

### **User Story**

En tant que développeur  
 Je veux suivre l’usage de l’IA.

### **Metrics**

AI calls  
tokens usage  
AI latency  
AI errors

---

# **EPIC 12 — Admin Back-Office**

## **Objectif**

Permettre aux admins de superviser l’application.

---

# **FEATURE 12.1 — Admin Authentication**

### **User Story**

En tant qu’administrateur  
 Je veux accéder au back-office sécurisé.

### **Functional Requirements**

- login admin

- session admin

- logout.

---

# **FEATURE 12.2 — User Management**

### **User Story**

En tant qu’administrateur  
 Je veux gérer les utilisateurs.

### **Actions**

Admin peut :

voir utilisateurs  
suspendre compte  
supprimer compte  
réinitialiser accès

---

# **FEATURE 12.3 — User Activity Dashboard**

### **User Story**

En tant qu’administrateur  
 Je veux voir l’activité des utilisateurs.

### **Metrics**

nombre utilisateurs  
DAU  
simulations  
questions IA

---

# **FEATURE 12.4 — Content Management**

### **User Story**

En tant qu’administrateur  
 Je veux modifier les contenus pédagogiques.

### **Actions**

modifier textes  
modifier modules éducatifs  
modifier prompts IA

---

# **FEATURE 12.5 — System Health Dashboard**

### **User Story**

En tant qu’administrateur  
 Je veux voir l’état du système.

### **Dashboard**

system status  
error rate  
AI usage  
API latency

---

# **EPIC 13 — Plans, Entitlements & Usage Control**

## **Objectif**

Permettre à l’application de :

- définir des plans d’utilisation

- limiter l’usage des fonctionnalités selon l’offre

- suivre la consommation utilisateur

- bloquer ou restreindre l’accès lorsque les quotas sont atteints.

Ce système permet de :

- contrôler les coûts (notamment IA)

- préparer une monétisation future

- gérer les accès aux fonctionnalités premium.

---

# **FEATURE 13.1 — Plan Management**

### **User Story**

En tant que système  
 Je veux associer un plan à chaque utilisateur  
 Afin de déterminer les fonctionnalités auxquelles il a accès.

---

### **Context**

L’application proposera plusieurs niveaux d’accès :

- plan gratuit

- plan premium

- plan administrateur.

Chaque plan définit :

- les fonctionnalités accessibles

- les quotas d’utilisation.

---

### **User Flow**

user signup

↓

default plan assigned (FREE)

↓

user accesses application

↓

system checks plan permissions

↓

feature allowed / restricted

---

### **Functional Requirements**

Chaque utilisateur doit avoir :

- un plan actif

- une date de début du plan

- une date de fin optionnelle.

Plans possibles :

FREE

PREMIUM

ADMIN

Chaque plan définit :

- accès aux fonctionnalités

- quotas d’usage.

---

### **Acceptance Criteria**

- un utilisateur possède un plan actif

- le plan peut être modifié

- le plan détermine les permissions.

---

### **Edge Cases**

- utilisateur sans plan

- plan expiré

- plan modifié pendant une session.

---

### **Data Model**

plans

\- id

\- name

\- description

user_plans

\- id

\- user_id

\- plan_id

\- start_date

\- end_date

---

### **Technical Notes**

Plan service

Plan validation middleware

---

# **FEATURE 13.2 — Feature Entitlements**

### **User Story**

En tant que système  
 Je veux vérifier si un utilisateur a accès à une fonctionnalité  
 Afin d’empêcher l’accès aux fonctionnalités non incluses dans son plan.

---

### **Context**

Certaines fonctionnalités peuvent être :

- gratuites

- limitées

- premium.

Exemples :

- simulation financière avancée

- assistant IA illimité

- projections avancées.

---

### **User Flow**

user performs action

↓

system identifies feature

↓

system checks user plan

↓

feature allowed / denied

---

### **Functional Requirements**

Chaque fonctionnalité doit être associée à :

- une permission

- un plan minimum requis.

Exemples :

AI_ASSISTANT

INVESTMENT_SIMULATION

ADVANCED_PROJECTION

---

### **Acceptance Criteria**

- les fonctionnalités premium sont bloquées pour les utilisateurs FREE

- les fonctionnalités autorisées sont accessibles normalement.

---

### **Edge Cases**

- feature mal configurée

- plan supprimé.

---

### **Data Model**

features

\- id

\- name

plan_features

\- plan_id

\- feature_id

---

### **Technical Notes**

Feature guard middleware

---

# **FEATURE 13.3 — Usage Quotas**

### **User Story**

En tant que système  
 Je veux limiter l’utilisation de certaines fonctionnalités  
 Afin de contrôler les ressources consommées.

---

### **Context**

Certaines fonctionnalités sont coûteuses :

- appels IA

- simulations

- projections.

Chaque plan doit définir un quota.

Exemple :

FREE

- 5 questions IA / jour

- 10 simulations / jour.

PREMIUM

- 100 questions IA / jour

- simulations illimitées.

---

### **User Flow**

user triggers feature

↓

system checks usage counter

↓

if quota available → execute

if quota exceeded → block

---

### **Functional Requirements**

Le système doit :

- compter l’usage

- comparer au quota

- bloquer si quota dépassé.

---

### **Acceptance Criteria**

- compteur d’usage mis à jour

- blocage si limite atteinte

- message utilisateur clair.

---

### **Edge Cases**

- compteur corrompu

- reset quota incorrect

- appels simultanés.

---

### **Data Model**

usage_records

\- id

\- user_id

\- feature

\- usage_count

\- period_start

\- period_end

---

### **Technical Notes**

usage service

atomic counters

---

# **FEATURE 13.4 — Quota Reset System**

### **User Story**

En tant que système  
 Je veux réinitialiser les quotas d’utilisation  
 Afin de permettre aux utilisateurs de continuer à utiliser les fonctionnalités.

---

### **Context**

Les quotas peuvent être :

- journaliers

- mensuels.

---

### **User Flow**

quota period ends

↓

system resets usage counters

↓

user regains usage allowance

---

### **Functional Requirements**

Le système doit :

- définir la période du quota

- réinitialiser automatiquement les compteurs.

---

### **Acceptance Criteria**

- reset automatique

- reset correct par période.

---

### **Edge Cases**

- reset multiple

- timezone utilisateurs.

---

### **Data Model**

quota_policies

\- feature

\- plan

\- limit

\- period

---

### **Technical Notes**

scheduled job

cron task

---

# **FEATURE 13.5 — Upgrade & Restriction Messaging**

### **User Story**

En tant qu’utilisateur  
 Je veux comprendre pourquoi une fonctionnalité est bloquée  
 Afin de savoir comment continuer à utiliser l’application.

---

### **Context**

Lorsque les quotas sont atteints ou la fonctionnalité est premium, l’utilisateur doit recevoir un message clair.

---

### **User Flow**

user triggers feature

↓

system detects restriction

↓

restriction message displayed

---

### **Functional Requirements**

Le système doit afficher :

- quota atteint

- fonctionnalité premium

- suggestion d’upgrade.

---

### **Acceptance Criteria**

- message clair

- aucune erreur système visible.

---

### **Edge Cases**

- message incorrect

- feature non reconnue.

---

### **Technical Notes**

frontend guard

API response codes

---

# **EPIC 14 — Billing & Payments with Stripe**

## **Objectif**

Permettre à l’application de :

- proposer des abonnements payants

- gérer les upgrades et downgrades

- encaisser les paiements de façon sécurisée

- synchroniser l’état des abonnements avec les plans utilisateur

- offrir un portail de gestion de facturation.

Cette epic complète l’EPIC 13 :

- **EPIC 13** \= logique produit d’accès, quotas, entitlements

- **EPIC 14** \= logique paiement, abonnement, facturation via Stripe

---

# **FEATURE 14.1 — Stripe Customer Creation**

### **User Story**

En tant que système  
 Je veux créer un client Stripe pour chaque utilisateur éligible  
 Afin de lier son compte FinMate à sa facturation.

---

### **Context**

Pour gérer les abonnements, moyens de paiement et factures, chaque utilisateur payant doit être associé à un **customer Stripe**.

---

### **User Flow**

user creates account

↓

user starts subscription flow

↓

system creates Stripe customer

↓

Stripe customer id is stored

↓

user can continue checkout

---

### **Functional Requirements**

Le système doit :

- créer un customer Stripe à la première entrée dans le parcours payant

- stocker le `stripe_customer_id`

- réutiliser ce customer pour les paiements futurs.

---

### **Acceptance Criteria**

- un customer Stripe est créé une seule fois par utilisateur

- le customer est lié au bon utilisateur

- le `stripe_customer_id` est stocké en base.

---

### **Edge Cases**

- customer déjà existant

- erreur API Stripe

- utilisateur supprimé localement mais présent dans Stripe.

---

### **Data Model**

billing_customers

\- id

\- user_id

\- stripe_customer_id

\- created_at

\- updated_at

---

### **Technical Notes**

- création côté backend uniquement

- ne jamais créer le customer depuis le frontend

- utiliser l’email utilisateur comme attribut Stripe principal.

---

# **FEATURE 14.2 — Subscription Checkout**

### **User Story**

En tant qu’utilisateur  
 Je veux souscrire à une offre payante  
 Afin d’accéder aux fonctionnalités premium.

---

### **Context**

L’utilisateur doit pouvoir passer d’un plan gratuit à un plan payant via un checkout simple et sécurisé.

---

### **User Flow**

user clicks upgrade

↓

frontend requests checkout session

↓

backend creates Stripe Checkout Session

↓

user is redirected to Stripe Checkout

↓

payment success

↓

user returns to app

---

### **Functional Requirements**

Le système doit permettre :

- sélection d’un plan

- création d’une Stripe Checkout Session

- redirection vers Stripe

- retour succès / annulation.

Plans ciblés :

FREE

PREMIUM

PRO (optional future)

---

### **Acceptance Criteria**

- l’utilisateur peut lancer un checkout

- le checkout correspond au bon plan

- un paiement réussi active l’abonnement

- un paiement annulé ne modifie pas le plan.

---

### **Edge Cases**

- session Stripe expirée

- paiement refusé

- double clic sur upgrade

- plan inexistant.

---

### **Data Model**

Réutilise :

plans

user_plans

billing_customers

Ajout :

stripe_checkout_sessions

\- id

\- user_id

\- stripe_session_id

\- plan_code

\- status

\- created_at

---

### **Technical Notes**

- Stripe Checkout recommandé pour MVP

- backend responsable de créer la session

- frontend ne manipule jamais les secrets Stripe.

---

# **FEATURE 14.3 — Subscription Synchronization**

### **User Story**

En tant que système  
 Je veux synchroniser les abonnements Stripe avec les plans FinMate  
 Afin que l’accès produit reflète l’état réel du paiement.

---

### **Context**

Le plan utilisateur dans FinMate doit toujours être aligné avec l’état d’abonnement Stripe :

- actif

- annulé

- impayé

- expiré.

---

### **User Flow**

Stripe event occurs

↓

webhook received by backend

↓

event validated

↓

subscription status updated

↓

user plan updated in FinMate

---

### **Functional Requirements**

Le système doit écouter les événements Stripe pertinents :

checkout.session.completed

customer.subscription.created

customer.subscription.updated

customer.subscription.deleted

invoice.paid

invoice.payment_failed

Le backend doit :

- valider la signature du webhook

- mettre à jour le statut d’abonnement

- mettre à jour le plan utilisateur.

---

### **Acceptance Criteria**

- un abonnement actif active le plan premium

- un abonnement annulé ou expiré retire les accès premium

- un paiement échoué met à jour le statut correctement.

---

### **Edge Cases**

- webhook reçu plusieurs fois

- webhook reçu dans le désordre

- événement Stripe inconnu

- erreur temporaire backend.

---

### **Data Model**

subscriptions

\- id

\- user_id

\- stripe_subscription_id

\- stripe_customer_id

\- stripe_price_id

\- status

\- current_period_start

\- current_period_end

\- cancel_at_period_end

\- created_at

\- updated_at

billing_events

\- id

\- stripe_event_id

\- event_type

\- processed

\- processed_at

\- created_at

---

### **Technical Notes**

- webhook idempotent obligatoire

- vérifier la signature Stripe

- ne jamais se baser uniquement sur le retour frontend après paiement.

---

# **FEATURE 14.4 — Billing Portal Access**

### **User Story**

En tant qu’utilisateur  
 Je veux gérer mon abonnement et mes moyens de paiement  
 Afin de modifier mon offre ou consulter ma facturation.

---

### **Context**

L’utilisateur doit pouvoir :

- voir ses factures

- mettre à jour sa carte

- annuler son abonnement

- reprendre son abonnement.

---

### **User Flow**

user opens billing settings

↓

user clicks manage subscription

↓

backend creates Stripe Billing Portal session

↓

user is redirected to Stripe portal

↓

user manages subscription

↓

user returns to app

---

### **Functional Requirements**

Le système doit permettre :

- accès au portail Stripe

- retour sécurisé dans l’application

- consultation de l’état courant d’abonnement.

---

### **Acceptance Criteria**

- l’utilisateur peut ouvrir son portail de facturation

- l’utilisateur peut modifier son moyen de paiement

- l’utilisateur peut annuler ou gérer son abonnement.

---

### **Edge Cases**

- customer Stripe manquant

- abonnement inexistant

- session portail expirée.

---

### **Data Model**

Réutilise :

billing_customers

subscriptions

---

### **Technical Notes**

- utiliser Stripe Billing Portal pour gagner du temps

- parfait pour MVP et début de production.

---

# **FEATURE 14.5 — Upgrade / Downgrade Management**

### **User Story**

En tant qu’utilisateur  
 Je veux changer d’offre  
 Afin d’adapter mon abonnement à mes besoins.

---

### **Context**

L’utilisateur peut vouloir :

- passer de FREE à PREMIUM

- revenir de PREMIUM à FREE

- changer d’offre future.

---

### **User Flow**

user selects new plan

↓

system evaluates current subscription

↓

Stripe subscription is updated

↓

FinMate plan is synchronized

---

### **Functional Requirements**

Le système doit gérer :

- upgrade immédiat

- downgrade en fin de période

- annulation d’abonnement

- synchronisation des droits.

---

### **Acceptance Criteria**

- un upgrade active rapidement les nouveaux droits

- un downgrade retire les droits au bon moment

- l’état affiché dans l’app est cohérent.

---

### **Edge Cases**

- changement pendant une facture impayée

- changement vers un plan supprimé

- désynchronisation Stripe / app.

---

### **Data Model**

Réutilise :

subscriptions

user_plans

plans

---

### **Technical Notes**

- la logique produit d’accès reste dans EPIC 13

- Stripe gère la facturation

- FinMate gère les entitlements.

---

# **FEATURE 14.6 — Billing Status in App**

### **User Story**

En tant qu’utilisateur  
 Je veux voir l’état de mon abonnement dans l’application  
 Afin de comprendre mes accès et ma facturation.

---

### **Context**

L’utilisateur doit pouvoir voir clairement :

- son plan actuel

- le statut de son abonnement

- sa date de renouvellement

- son accès premium.

---

### **User Flow**

user opens account / billing page

↓

app fetches billing summary

↓

subscription status is displayed

---

### **Functional Requirements**

Afficher :

current_plan

subscription_status

renewal_date

cancel_at_period_end

payment_issue_flag

---

### **Acceptance Criteria**

- la vue billing affiche l’offre active

- la date de renouvellement est visible

- un problème de paiement est visible.

---

### **Edge Cases**

- utilisateur gratuit sans abonnement

- période d’essai

- paiement échoué.

---

### **Data Model**

Réutilise :

subscriptions

user_plans

---

### **Technical Notes**

- page dédiée recommandée : `Account > Billing`

- affichage clair et non technique.

---

# **FEATURE 14.7 — Payment Failure Handling**

### **User Story**

En tant que système  
 Je veux gérer les échecs de paiement  
 Afin d’éviter une incohérence entre la facturation et les accès produit.

---

### **Context**

Un paiement peut échouer pour plusieurs raisons :

- carte expirée

- fonds insuffisants

- moyen de paiement invalide.

L’utilisateur doit être informé et l’accès doit être ajusté correctement.

---

### **User Flow**

invoice payment fails

↓

Stripe sends webhook

↓

subscription status updated

↓

app marks billing issue

↓

user sees payment problem message

---

### **Functional Requirements**

Le système doit :

- détecter les paiements échoués

- enregistrer l’incident

- afficher un message clair à l’utilisateur

- ajuster les droits selon la politique produit.

---

### **Acceptance Criteria**

- un échec de paiement est visible

- l’utilisateur peut corriger son moyen de paiement

- l’état produit reste cohérent.

---

### **Edge Cases**

- multiples échecs de paiement

- webhook manquant

- paiement régularisé plus tard.

---

### **Data Model**

billing_issues

\- id

\- user_id

\- stripe_invoice_id

\- issue_type

\- status

\- created_at

\- resolved_at

---

### **Technical Notes**

- important pour la cohérence avec EPIC 13

- prévoir messages UX clairs :
  - paiement échoué

  - mettre à jour la carte

  - accès premium en attente.

---

# **FEATURE 14.8 — Trial Management**

### **User Story**

En tant qu’utilisateur  
 Je veux pouvoir bénéficier d’une période d’essai  
 Afin de découvrir l’offre premium avant de payer.

---

### **Context**

Une période d’essai peut améliorer la conversion vers le plan payant.

---

### **User Flow**

user starts premium trial

↓

trial status created

↓

premium access enabled

↓

trial ends

↓

subscription continues or premium access stops

---

### **Functional Requirements**

Le système doit gérer :

- durée d’essai

- activation des droits premium pendant l’essai

- conversion automatique ou fin d’essai.

---

### **Acceptance Criteria**

- un utilisateur en essai a accès aux fonctionnalités premium

- la fin d’essai est correctement gérée

- la transition trial → paid ou trial → free est cohérente.

---

### **Edge Cases**

- utilisateur qui tente plusieurs trials

- trial interrompu

- paiement absent à fin d’essai.

---

### **Data Model**

Ajout possible :

subscription_trials

\- id

\- user_id

\- stripe_subscription_id

\- trial_start

\- trial_end

\- status

---

### **Technical Notes**

- optionnelle pour MVP

- très utile si tu veux lancer une bêta payante douce.

---

# **Backlog**

1\. Financial Profile & Budget Understanding (EPIC 1\) DONE  
2\. Savings Discipline (EPIC 2\) DONE  
3\. Passive Investing Education (EPIC 3\) DONE  
4\. Risk Awareness (EPIC 4\) DONE  
5\. Behavioral Finance Coaching (EPIC 5\) DONE  
6\. AI Financial Assistant (EPIC 6\) DONE  
7\. Financial Projections (EPIC 7\) DONE  
8\. Gamification (EPIC 8\) DONE  
9\. AI Financial Coaching Assistant (EPIC 9\) DONE WHITOUT 9.11 & 9.12  
10\. Security & Access Control (EPIC 10\) WIP  
11\. Observality & Monitoring (EPIC 11\) READY  
12\. Admin backoffice (EPIC 12\) READY  
13\. Plans, Entitlements & Usage Control (EPIC 13\) READY  
14\. Billing & Payments with Stripe (EPIC 14\) READY

# **Résumé backlog final**

14 EPICS

X USER STORIES

Modules :

profile

budget

goals

education

risk

assistant

projections

gamification
