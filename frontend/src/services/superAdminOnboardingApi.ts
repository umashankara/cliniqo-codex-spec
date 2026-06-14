import axios from 'axios';
import type { ApiEnvelope, ClinicOnboardingPayload, ClinicOnboardingResult } from '../types/superAdminOnboarding';

export async function onboardClinic(payload: ClinicOnboardingPayload, idempotencyKey: string) {
  const response = await axios.post<ApiEnvelope<ClinicOnboardingResult>>('/api/v1/super-admin/onboarding/clinics', payload, {
    headers: { 'Idempotency-Key': idempotencyKey }
  });
  return response.data;
}
