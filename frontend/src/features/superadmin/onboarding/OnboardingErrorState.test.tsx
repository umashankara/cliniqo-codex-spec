import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../../test/test-utils';
import { UnexpectedErrorState } from './OnboardingStates';

describe('OnboardingStates', () => {
  it('shows safe request id for unexpected errors', () => {
    renderWithProviders(<UnexpectedErrorState requestId="req-123" />);
    expect(screen.getByRole('alert')).toHaveTextContent('req-123');
  });
});
