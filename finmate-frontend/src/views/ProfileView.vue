<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { profileApi } from '@/api/profile.api'
import OnboardingFlow from '@/features/financial-profile/OnboardingFlow.vue'
import ProfileSettings from '@/features/financial-profile/ProfileSettings.vue'

const hasProfile = ref<boolean | null>(null)

onMounted(async () => {
  try {
    await profileApi.get()
    hasProfile.value = true
  } catch {
    hasProfile.value = false
  }
})
</script>

<template>
  <div class="page profile-view">
    <div v-if="hasProfile === null" class="profile-view__loading">Chargement...</div>
    <OnboardingFlow v-else-if="hasProfile === false" />
    <ProfileSettings v-else />
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.profile-view {
  &__loading {
    padding: $spacing-xl $spacing-lg;
    color: $color-text-muted;
    font-size: $font-size-sm;
  }
}
</style>
