import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

interface Props {
  value: ClinicOnboardingPayload['firstAdmin'];
  fieldErrors: Record<string, string>;
  onChange: <K extends keyof ClinicOnboardingPayload['firstAdmin']>(field: K, value: ClinicOnboardingPayload['firstAdmin'][K]) => void;
}

export function FirstAdminSection({ value, fieldErrors, onChange }: Props) {
  return (
    <fieldset className="panel">
      <legend>First Clinic Admin</legend>
      <div className="field-grid">
        <label>
          Full name
          <input value={value.fullName} onChange={(event) => onChange('fullName', event.target.value)} aria-label="First admin full name" />
        </label>
        <label>
          Email
          <input value={value.email} onChange={(event) => onChange('email', event.target.value)} aria-label="First admin email" />
          {fieldErrors['firstAdmin.email'] ? <span className="field-error">{fieldErrors['firstAdmin.email']}</span> : null}
        </label>
        <label>
          Phone
          <input value={value.phone ?? ''} onChange={(event) => onChange('phone', event.target.value)} aria-label="First admin phone" />
        </label>
      </div>
    </fieldset>
  );
}
