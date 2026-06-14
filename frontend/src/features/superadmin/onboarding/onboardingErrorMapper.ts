import type { ApiError } from '../../../types/superAdminOnboarding';

export function mapOnboardingError(error?: ApiError) {
  if (!error) return { message: 'Unexpected error', fieldErrors: {} as Record<string, string> };
  return {
    message: error.message,
    fieldErrors: error.details ?? {}
  };
}
