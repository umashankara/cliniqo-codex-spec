import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { SuperAdminRoute } from '../../../routes/SuperAdminRoute';
import { renderWithProviders } from '../../../test/test-utils';

describe('SuperAdminRoute', () => {
  it('shows loading for unauthenticated users', () => {
    renderWithProviders(<SuperAdminRoute user={null}>Secret</SuperAdminRoute>);
    expect(screen.getByRole('status')).toHaveTextContent('Checking access');
  });

  it('blocks non-SUPER_ADMIN users', () => {
    renderWithProviders(<SuperAdminRoute user={{ userId: '1', email: 'a@b.com', role: 'CLINIC_ADMIN', clinicId: 'c' }}>Secret</SuperAdminRoute>);
    expect(screen.getByRole('alert')).toHaveTextContent('Permission denied');
  });

  it('renders SUPER_ADMIN users', () => {
    renderWithProviders(<SuperAdminRoute user={{ userId: '1', email: 'a@b.com', role: 'SUPER_ADMIN', clinicId: null }}>Secret</SuperAdminRoute>);
    expect(screen.getByText('Secret')).toBeInTheDocument();
  });
});
