import type { ReactNode } from 'react';
import type { AuthUser } from '../types/superAdminOnboarding';

export function SuperAdminRoute({ user, children }: { user?: AuthUser | null; children: ReactNode }) {
  if (!user) {
    return <div role="status">Checking access</div>;
  }
  if (user.role !== 'SUPER_ADMIN') {
    return <div role="alert">Permission denied</div>;
  }
  return <>{children}</>;
}
