import { describe, expect, it } from 'vitest';
import { onboardingSchema } from './onboardingSchema';

const validPayload = {
  clinic: { name: 'Care Clinic', slug: 'care-clinic', country: 'IN', timezone: 'Asia/Kolkata', defaultLanguage: 'en', primaryPhone: '+919999999999', email: 'clinic@example.com' },
  settings: { slotDurationMinutes: 30, maxAdvanceBookingDays: 30, minBookingNoticeMinutes: 60, cancellationCutoffMinutes: 120, rescheduleCutoffMinutes: 120 },
  operatingHours: [{ dayOfWeek: 'MONDAY', closed: false, openTime: '09:00', closeTime: '17:00' }],
  reminders: { morningOfEnabled: true, morningOfTimeLocal: '08:00', beforeAppointmentEnabled: true, beforeAppointmentOffsetMinutes: 120 },
  publicWebsite: { slug: 'care-clinic', enabled: false },
  whatsapp: { wabaId: 'waba', phoneNumberId: 'phone-id', displayPhoneNumber: '+919999999999', templateNamespace: 'care' },
  firstAdmin: { fullName: 'Admin User', email: 'admin@example.com' }
};

describe('onboardingSchema', () => {
  it('accepts valid timezone/default language and ranges', () => {
    expect(onboardingSchema.safeParse(validPayload).success).toBe(true);
  });

  it('rejects non-canonical slugs', () => {
    const result = onboardingSchema.safeParse({ ...validPayload, clinic: { ...validPayload.clinic, slug: 'Care Clinic' } });
    expect(result.success).toBe(false);
  });
});
