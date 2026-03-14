<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { expensesApi } from '@/api/expenses.api'
import { budgetApi } from '@/api/budget.api'
import { scoreApi } from '@/api/score.api'
import type { CreateExpensePayload, Expense } from '@/types/expense.types'
import type { BudgetSummary, MonthTrend } from '@/types/budget.types'
import type { FinancialScore } from '@/types/score.types'
import ExpenseForm from '@/features/budget/ExpenseForm.vue'
import ExpenseList from '@/features/budget/ExpenseList.vue'
import IncomeExpenseCard from '@/features/budget/IncomeExpenseCard.vue'
import SavingsRateIndicator from '@/features/budget/SavingsRateIndicator.vue'
import ExpenseCategoryChart from '@/features/budget/ExpenseCategoryChart.vue'
import MonthlyTrendChart from '@/features/budget/MonthlyTrendChart.vue'
import HealthScoreCard from '@/features/budget/HealthScoreCard.vue'

const expenses = ref<Expense[]>([])
const summary = ref<BudgetSummary | null>(null)
const trend = ref<MonthTrend[]>([])
const score = ref<FinancialScore | null>(null)
const loading = ref(false)
const adding = ref(false)
const deleting = ref<string | null>(null)
const error = ref<string | null>(null)
const showForm = ref(false)

const currentMonth = ref(new Date().toISOString().slice(0, 7))

async function loadData() {
  loading.value = true
  error.value = null
  try {
    const [expenseList, budgetSummary, trendData, scoreData] = await Promise.all([
      expensesApi.list(currentMonth.value),
      budgetApi.getSummary(currentMonth.value),
      budgetApi.getTrend(6),
      scoreApi.getScore(),
    ])
    expenses.value = expenseList
    summary.value = budgetSummary
    trend.value = trendData
    score.value = scoreData
  } catch {
    error.value = 'Impossible de charger les données.'
  } finally {
    loading.value = false
  }
}

async function handleAdd(payload: CreateExpensePayload) {
  adding.value = true
  error.value = null
  try {
    await expensesApi.create(payload)
    showForm.value = false
    await loadData()
  } catch {
    error.value = "Erreur lors de l'ajout."
  } finally {
    adding.value = false
  }
}

async function handleDelete(id: string) {
  deleting.value = id
  try {
    await expensesApi.delete(id)
    await loadData()
  } catch {
    error.value = 'Erreur lors de la suppression.'
  } finally {
    deleting.value = null
  }
}

function previousMonth() {
  const d = new Date(currentMonth.value + '-01')
  d.setMonth(d.getMonth() - 1)
  currentMonth.value = d.toISOString().slice(0, 7)
}

function nextMonth() {
  const d = new Date(currentMonth.value + '-01')
  d.setMonth(d.getMonth() + 1)
  currentMonth.value = d.toISOString().slice(0, 7)
}

const monthLabel = computed(() => {
  const d = new Date(currentMonth.value + '-01')
  return d.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
})

watch(currentMonth, loadData)
onMounted(loadData)
</script>

<template>
  <div class="page budget">
    <h1 class="budget__title">Budget</h1>

    <div class="budget__month-nav">
      <button class="budget__month-btn" type="button" @click="previousMonth">←</button>
      <span class="budget__month-label">{{ monthLabel }}</span>
      <button class="budget__month-btn" type="button" @click="nextMonth">→</button>
    </div>

    <div v-if="error" class="budget__error">{{ error }}</div>

    <template v-if="summary">
      <div class="budget__section">
        <p class="budget__section-title">Score de santé financière</p>
        <HealthScoreCard v-if="score" :data="score" />
      </div>

      <div class="budget__cards">
        <IncomeExpenseCard label="Revenus" :amount="summary.monthlyIncome" color="#0EA5A4" />
        <IncomeExpenseCard label="Dépenses" :amount="summary.totalExpenses" color="#EF4444" />
        <IncomeExpenseCard label="Restant" :amount="summary.remainingIncome" />
      </div>

      <SavingsRateIndicator :rate="summary.savingsRate" />

      <div class="budget__section">
        <p class="budget__section-title">Par catégorie</p>
        <ExpenseCategoryChart :data="summary.expensesByCategory" />
      </div>

      <div class="budget__section">
        <p class="budget__section-title">Tendance 6 mois</p>
        <MonthlyTrendChart :data="trend" />
      </div>
    </template>

    <div class="budget__section">
      <p class="budget__section-title">Dépenses</p>

      <button
        v-if="!showForm"
        class="budget__add-btn"
        type="button"
        @click="showForm = true"
      >
        + Ajouter une dépense
      </button>

      <ExpenseForm v-if="showForm" :loading="adding" @submit="handleAdd" />

      <div v-if="loading" class="budget__loading">Chargement...</div>
      <ExpenseList v-else :expenses="expenses" :deleting="deleting" @delete="handleDelete" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.budget {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__month-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__month-btn {
    background: none;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    padding: $spacing-xs $spacing-sm;
    font-size: $font-size-base;
    cursor: pointer;
    color: $color-text;
  }

  &__month-label {
    font-size: $font-size-base;
    font-weight: 500;
    text-transform: capitalize;
  }

  &__cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-sm;
  }

  &__section {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__section-title {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__error {
    padding: $spacing-sm $spacing-md;
    background-color: #fef2f2;
    color: $color-danger;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
  }

  &__add-btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-primary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
  }

  &__loading {
    color: $color-text-muted;
    font-size: $font-size-sm;
    text-align: center;
    padding: $spacing-lg 0;
  }
}
</style>
