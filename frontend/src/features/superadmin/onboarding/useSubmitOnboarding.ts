import { useMutation } from '@tanstack/react-query';
import { onboardClinic } from '../../../services/superAdminOnboardingApi';
import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

export function useSubmitOnboarding() {
  return useMutation({
    mutationFn: (payload: ClinicOnboardingPayload) => onboardClinic(payload, crypto.randomUUID())
  });
}
