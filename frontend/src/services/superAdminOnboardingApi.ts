import axios from 'axios';
import type { ApiEnvelope, ClinicOnboardingPayload, ClinicOnboardingResult } from '../types/superAdminOnboarding';

interface LocalAuthResult {
  accessToken: string;
}

let localAccessToken: string | null = null;

async function getLocalSuperAdminAccessToken() {
  if (localAccessToken) {
    return localAccessToken;
  }
  const response = await axios.post<ApiEnvelope<LocalAuthResult>>('/api/v1/auth/login', {
    email: 'superadmin@cliniqo.test',
    password: 'Password123!'
  });
  localAccessToken = response.data.data?.accessToken ?? null;
  if (!localAccessToken) {
    throw new Error('Local SUPER_ADMIN login failed');
  }
  return localAccessToken;
}

export async function onboardClinic(payload: ClinicOnboardingPayload, idempotencyKey: string) {
  const accessToken = await getLocalSuperAdminAccessToken();
  const response = await axios.post<ApiEnvelope<ClinicOnboardingResult>>('/api/v1/super-admin/onboarding/clinics', payload, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Idempotency-Key': idempotencyKey
    },
    validateStatus: () => true
  });
  return response.data;
}
