import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '../../../test/test-utils';
import { OneTimeCredentialPanel } from './OneTimeCredentialPanel';

describe('OneTimeCredentialPanel', () => {
  it('shows temporary password exactly when supplied', () => {
    renderWithProviders(<OneTimeCredentialPanel temporaryPassword="Temp123!" />);
    expect(screen.getByText('Temp123!')).toBeInTheDocument();
  });

  it('does not recover missing temporary password', () => {
    renderWithProviders(<OneTimeCredentialPanel />);
    expect(screen.getByRole('status')).toHaveTextContent('not recoverable');
  });

  it('allows dismissal without persistence', () => {
    const onDismiss = vi.fn();
    renderWithProviders(<OneTimeCredentialPanel temporaryPassword="Temp123!" onDismiss={onDismiss} />);
    fireEvent.click(screen.getByRole('button', { name: /dismiss/i }));
    expect(onDismiss).toHaveBeenCalled();
  });
});
