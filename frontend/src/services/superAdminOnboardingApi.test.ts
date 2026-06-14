import axios from 'axios';
import { describe, expect, it, vi } from 'vitest';
import { onboardClinic } from './superAdminOnboardingApi';

vi.mock('axios');

describe('superAdminOnboardingApi', () => {
  it('posts onboarding payload with idempotency key', async () => {
    vi.mocked(axios.post)
      .mockResolvedValueOnce({ data: { success: true, data: { accessToken: 'token-1' }, meta: { requestId: 'login' } } })
      .mockResolvedValueOnce({ data: { success: true, data: { temporaryPassword: 'once' }, meta: { requestId: 'r1' } } });
    const result = await onboardClinic({} as never, 'key-1');
    expect(axios.post).toHaveBeenNthCalledWith(1, '/api/v1/auth/login', {
      email: 'superadmin@cliniqo.test',
      password: 'Password123!'
    });
    expect(axios.post).toHaveBeenNthCalledWith(2, '/api/v1/super-admin/onboarding/clinics', {}, {
      headers: {
        Authorization: 'Bearer token-1',
        'Idempotency-Key': 'key-1'
      },
      validateStatus: expect.any(Function)
    });
    expect(result.data?.temporaryPassword).toBe('once');
  });
});
