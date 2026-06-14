import { useMemo, useState, type FormEvent } from 'react';
import { ClinicProfileSection } from './ClinicProfileSection';
import { FirstAdminSection } from './FirstAdminSection';
import { OneTimeCredentialPanel } from './OneTimeCredentialPanel';
import { OperatingDefaultsSection } from './OperatingDefaultsSection';
import { UnexpectedErrorState } from './OnboardingStates';
import { PublicWebsiteSection } from './PublicWebsiteSection';
import { WhatsAppMetadataSection } from './WhatsAppMetadataSection';
import { mapOnboardingError } from './onboardingErrorMapper';
import { onboardingSchema } from './onboardingSchema';
import { useSubmitOnboarding } from './useSubmitOnboarding';
import type { ApiEnvelope, ClinicOnboardingPayload, ClinicOnboardingResult } from '../../../types/superAdminOnboarding';

const defaultPayload: ClinicOnboardingPayload = {
  clinic: {
    name: 'Care Clinic',
    slug: 'care-clinic',
    address: '12 Wellness Street',
    country: 'IN',
    timezone: 'Asia/Kolkata',
    defaultLanguage: 'en',
    primaryPhone: '+919999999999',
    email: 'clinic@example.com'
  },
  settings: {
    slotDurationMinutes: 30,
    maxAdvanceBookingDays: 30,
    minBookingNoticeMinutes: 60,
    cancellationCutoffMinutes: 120,
    rescheduleCutoffMinutes: 120
  },
  operatingHours: [
    { dayOfWeek: 'MONDAY', closed: false, openTime: '09:00', closeTime: '17:00' },
    { dayOfWeek: 'TUESDAY', closed: false, openTime: '09:00', closeTime: '17:00' },
    { dayOfWeek: 'WEDNESDAY', closed: false, openTime: '09:00', closeTime: '17:00' },
    { dayOfWeek: 'THURSDAY', closed: false, openTime: '09:00', closeTime: '17:00' },
    { dayOfWeek: 'FRIDAY', closed: false, openTime: '09:00', closeTime: '17:00' }
  ],
  reminders: {
    morningOfEnabled: true,
    morningOfTimeLocal: '08:00',
    beforeAppointmentEnabled: true,
    beforeAppointmentOffsetMinutes: 120
  },
  publicWebsite: { slug: 'care-clinic', enabled: false },
  whatsapp: {
    wabaId: 'waba-123',
    phoneNumberId: 'phone-number-123',
    displayPhoneNumber: '+919999999999',
    templateNamespace: 'care_clinic',
    accessTokenPlaceholder: 'configured-outside-demo',
    appSecretPlaceholder: 'configured-outside-demo'
  },
  firstAdmin: {
    fullName: 'Admin User',
    email: 'admin@example.com',
    phone: '+919888888888'
  }
};

export function OnboardingPage() {
  const [payload, setPayload] = useState<ClinicOnboardingPayload>(defaultPayload);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [credentialVisible, setCredentialVisible] = useState(true);
  const mutation = useSubmitOnboarding();
  const apiEnvelope = mutation.data as ApiEnvelope<ClinicOnboardingResult> | undefined;
  const mappedError = useMemo(() => mapOnboardingError(apiEnvelope?.error), [apiEnvelope?.error]);

  const updateClinic = <K extends keyof ClinicOnboardingPayload['clinic']>(field: K, value: ClinicOnboardingPayload['clinic'][K]) => {
    setPayload((current) => ({
      ...current,
      clinic: { ...current.clinic, [field]: value },
      publicWebsite: field === 'slug' ? { ...current.publicWebsite, slug: String(value) } : current.publicWebsite
    }));
  };

  const updateSettings = <K extends keyof ClinicOnboardingPayload['settings']>(field: K, value: ClinicOnboardingPayload['settings'][K]) => {
    setPayload((current) => ({ ...current, settings: { ...current.settings, [field]: value } }));
  };

  const updateReminders = <K extends keyof ClinicOnboardingPayload['reminders']>(field: K, value: ClinicOnboardingPayload['reminders'][K]) => {
    setPayload((current) => ({ ...current, reminders: { ...current.reminders, [field]: value } }));
  };

  const updatePublicWebsite = <K extends keyof ClinicOnboardingPayload['publicWebsite']>(field: K, value: ClinicOnboardingPayload['publicWebsite'][K]) => {
    setPayload((current) => ({ ...current, publicWebsite: { ...current.publicWebsite, [field]: value } }));
  };

  const updateWhatsApp = <K extends keyof ClinicOnboardingPayload['whatsapp']>(field: K, value: ClinicOnboardingPayload['whatsapp'][K]) => {
    setPayload((current) => ({ ...current, whatsapp: { ...current.whatsapp, [field]: value } }));
  };

  const updateFirstAdmin = <K extends keyof ClinicOnboardingPayload['firstAdmin']>(field: K, value: ClinicOnboardingPayload['firstAdmin'][K]) => {
    setPayload((current) => ({ ...current, firstAdmin: { ...current.firstAdmin, [field]: value } }));
  };

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setValidationError(null);
    setCredentialVisible(true);
    const parsed = onboardingSchema.safeParse(payload);
    if (!parsed.success) {
      setValidationError('Please correct the highlighted onboarding values before creating the clinic.');
      return;
    }
    await mutation.mutateAsync(parsed.data);
  }

  return (
    <main className="app-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Super Admin</p>
          <h1>Clinic onboarding</h1>
        </div>
        <span className="status-pill">F02</span>
      </header>

      <form className="onboarding-form" onSubmit={handleSubmit}>
        {validationError ? <div role="alert" className="error-banner">{validationError}</div> : null}
        {mutation.isError ? <UnexpectedErrorState /> : null}
        {apiEnvelope?.success === false ? <div role="alert" className="error-banner">{mappedError.message}</div> : null}

        <ClinicProfileSection value={payload.clinic} fieldErrors={mappedError.fieldErrors} onChange={updateClinic} />
        <OperatingDefaultsSection settings={payload.settings} reminders={payload.reminders} onSettingsChange={updateSettings} onRemindersChange={updateReminders} />
        <PublicWebsiteSection value={payload.publicWebsite} fieldErrors={mappedError.fieldErrors} onChange={updatePublicWebsite} />
        <WhatsAppMetadataSection value={payload.whatsapp} fieldErrors={mappedError.fieldErrors} onChange={updateWhatsApp} />
        <FirstAdminSection value={payload.firstAdmin} fieldErrors={mappedError.fieldErrors} onChange={updateFirstAdmin} />

        {apiEnvelope?.success && apiEnvelope.data ? (
          <OneTimeCredentialPanel
            temporaryPassword={credentialVisible ? apiEnvelope.data.temporaryPassword : undefined}
            onDismiss={() => setCredentialVisible(false)}
          />
        ) : null}

        <div className="action-bar">
          <button type="submit" disabled={mutation.isPending}>{mutation.isPending ? 'Creating clinic...' : 'Create clinic'}</button>
        </div>
      </form>
    </main>
  );
}
