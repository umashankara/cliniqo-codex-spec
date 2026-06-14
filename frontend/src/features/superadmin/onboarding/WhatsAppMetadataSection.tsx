import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

interface Props {
  value: ClinicOnboardingPayload['whatsapp'];
  fieldErrors: Record<string, string>;
  onChange: <K extends keyof ClinicOnboardingPayload['whatsapp']>(field: K, value: ClinicOnboardingPayload['whatsapp'][K]) => void;
}

export function WhatsAppMetadataSection({ value, fieldErrors, onChange }: Props) {
  return (
    <fieldset className="panel">
      <legend>WhatsApp metadata</legend>
      <div className="field-grid">
        <label>
          WABA ID
          <input value={value.wabaId} onChange={(event) => onChange('wabaId', event.target.value)} aria-label="WABA ID" />
        </label>
        <label>
          Phone number ID
          <input value={value.phoneNumberId} onChange={(event) => onChange('phoneNumberId', event.target.value)} aria-label="Phone number ID" />
          {fieldErrors['whatsapp.phoneNumberId'] ? <span className="field-error">{fieldErrors['whatsapp.phoneNumberId']}</span> : null}
        </label>
        <label>
          Display phone
          <input value={value.displayPhoneNumber} onChange={(event) => onChange('displayPhoneNumber', event.target.value)} aria-label="Display phone number" />
          {fieldErrors['whatsapp.displayPhoneNumber'] ? <span className="field-error">{fieldErrors['whatsapp.displayPhoneNumber']}</span> : null}
        </label>
        <label>
          Template namespace
          <input value={value.templateNamespace} onChange={(event) => onChange('templateNamespace', event.target.value)} aria-label="Template namespace" />
        </label>
        <label>
          Token placeholder
          <input value={value.accessTokenPlaceholder ?? ''} onChange={(event) => onChange('accessTokenPlaceholder', event.target.value)} aria-label="Access token placeholder" />
        </label>
        <label>
          App secret placeholder
          <input value={value.appSecretPlaceholder ?? ''} onChange={(event) => onChange('appSecretPlaceholder', event.target.value)} aria-label="App secret placeholder" />
        </label>
      </div>
    </fieldset>
  );
}
