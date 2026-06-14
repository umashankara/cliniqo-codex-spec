import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

interface Props {
  value: ClinicOnboardingPayload['publicWebsite'];
  fieldErrors: Record<string, string>;
  onChange: <K extends keyof ClinicOnboardingPayload['publicWebsite']>(field: K, value: ClinicOnboardingPayload['publicWebsite'][K]) => void;
}

export function PublicWebsiteSection({ value, fieldErrors, onChange }: Props) {
  return (
    <fieldset className="panel">
      <legend>Public website</legend>
      <div className="field-grid">
        <label>
          Public slug
          <input value={value.slug} onChange={(event) => onChange('slug', event.target.value)} aria-label="Public website slug" />
          {fieldErrors['publicWebsite.slug'] ? <span className="field-error">{fieldErrors['publicWebsite.slug']}</span> : null}
        </label>
        <label className="checkbox-row">
          <input type="checkbox" checked={value.enabled} onChange={(event) => onChange('enabled', event.target.checked)} />
          Reserve and enable public website slug
        </label>
      </div>
    </fieldset>
  );
}
