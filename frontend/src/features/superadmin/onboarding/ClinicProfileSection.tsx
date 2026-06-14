import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

interface Props {
  value: ClinicOnboardingPayload['clinic'];
  fieldErrors: Record<string, string>;
  onChange: <K extends keyof ClinicOnboardingPayload['clinic']>(field: K, value: ClinicOnboardingPayload['clinic'][K]) => void;
}

export function ClinicProfileSection({ value, fieldErrors, onChange }: Props) {
  return (
    <fieldset className="panel">
      <legend>Clinic profile</legend>
      <div className="field-grid">
        <label>
          Clinic name
          <input value={value.name} onChange={(event) => onChange('name', event.target.value)} aria-label="Clinic name" />
          {fieldErrors['clinic.name'] ? <span className="field-error">{fieldErrors['clinic.name']}</span> : null}
        </label>
        <label>
          Clinic slug
          <input value={value.slug} onChange={(event) => onChange('slug', event.target.value)} aria-label="Clinic slug" />
          {fieldErrors['clinic.slug'] ? <span className="field-error">{fieldErrors['clinic.slug']}</span> : null}
        </label>
        <label>
          Timezone
          <input value={value.timezone} onChange={(event) => onChange('timezone', event.target.value)} aria-label="Timezone" />
        </label>
        <label>
          Default language
          <input value={value.defaultLanguage} onChange={(event) => onChange('defaultLanguage', event.target.value)} aria-label="Default language" />
        </label>
        <label>
          Country
          <input value={value.country} onChange={(event) => onChange('country', event.target.value)} aria-label="Country" />
        </label>
        <label>
          Clinic email
          <input value={value.email} onChange={(event) => onChange('email', event.target.value)} aria-label="Clinic email" />
        </label>
        <label>
          Primary phone
          <input value={value.primaryPhone} onChange={(event) => onChange('primaryPhone', event.target.value)} aria-label="Primary phone" />
        </label>
        <label className="wide">
          Address
          <input value={value.address ?? ''} onChange={(event) => onChange('address', event.target.value)} aria-label="Address" />
        </label>
      </div>
    </fieldset>
  );
}
