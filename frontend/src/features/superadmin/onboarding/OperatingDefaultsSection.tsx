import type { ClinicOnboardingPayload } from '../../../types/superAdminOnboarding';

interface Props {
  settings: ClinicOnboardingPayload['settings'];
  reminders: ClinicOnboardingPayload['reminders'];
  onSettingsChange: <K extends keyof ClinicOnboardingPayload['settings']>(field: K, value: ClinicOnboardingPayload['settings'][K]) => void;
  onRemindersChange: <K extends keyof ClinicOnboardingPayload['reminders']>(field: K, value: ClinicOnboardingPayload['reminders'][K]) => void;
}

export function OperatingDefaultsSection({ settings, reminders, onSettingsChange, onRemindersChange }: Props) {
  return (
    <fieldset className="panel">
      <legend>Operating defaults</legend>
      <div className="field-grid">
        <label>
          Slot duration
          <select value={settings.slotDurationMinutes} onChange={(event) => onSettingsChange('slotDurationMinutes', Number(event.target.value))}>
            {[15, 20, 30, 45, 60].map((minutes) => <option key={minutes} value={minutes}>{minutes} minutes</option>)}
          </select>
        </label>
        <label>
          Advance booking
          <input type="number" value={settings.maxAdvanceBookingDays} onChange={(event) => onSettingsChange('maxAdvanceBookingDays', Number(event.target.value))} />
        </label>
        <label>
          Min notice
          <input type="number" value={settings.minBookingNoticeMinutes} onChange={(event) => onSettingsChange('minBookingNoticeMinutes', Number(event.target.value))} />
        </label>
        <label>
          Cancel cutoff
          <input type="number" value={settings.cancellationCutoffMinutes} onChange={(event) => onSettingsChange('cancellationCutoffMinutes', Number(event.target.value))} />
        </label>
        <label>
          Reschedule cutoff
          <input type="number" value={settings.rescheduleCutoffMinutes} onChange={(event) => onSettingsChange('rescheduleCutoffMinutes', Number(event.target.value))} />
        </label>
        <label>
          Morning reminder
          <input value={reminders.morningOfTimeLocal} onChange={(event) => onRemindersChange('morningOfTimeLocal', event.target.value)} />
        </label>
        <label className="checkbox-row">
          <input type="checkbox" checked={reminders.morningOfEnabled} onChange={(event) => onRemindersChange('morningOfEnabled', event.target.checked)} />
          Send morning reminder
        </label>
        <label>
          Before appointment
          <input type="number" value={reminders.beforeAppointmentOffsetMinutes} onChange={(event) => onRemindersChange('beforeAppointmentOffsetMinutes', Number(event.target.value))} />
        </label>
      </div>
    </fieldset>
  );
}
