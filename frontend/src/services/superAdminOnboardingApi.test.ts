import axios from 'axios';
import { describe, expect, it, vi } from 'vitest';
import { onboardClinic } from './superAdminOnboardingApi';

vi.mock('axios');

describe('superAdminOnboardingApi', () => {
  it('posts onboarding payload with idempotency key', async () => {
    vi.mocked(axios.post).mockResolvedValueOnce({ data: { success: true, data: { temporaryPassword: 'once' }, meta: { requestId: 'r1' } } });
    const result = await onboardClinic({} as never, 'key-1');
    expect(axios.post).toHaveBeenCalledWith('/api/v1/super-admin/onboarding/clinics', {}, { headers: { 'Idempotency-Key': 'key-1' } });
    expect(result.data?.temporaryPassword).toBe('once');
  });
});
