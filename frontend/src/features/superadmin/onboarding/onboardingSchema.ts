import { z } from 'zod';

const slug = z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/);
const time = z.string().regex(/^([01][0-9]|2[0-3]):[0-5][0-9]$/);

export const onboardingSchema = z.object({
  clinic: z.object({
    name: z.string().min(2),
    slug,
    address: z.string().optional(),
    country: z.string().min(2),
    timezone: z.string().min(1),
    defaultLanguage: z.string().min(2),
    primaryPhone: z.string().min(8),
    email: z.string().email()
  }),
  settings: z.object({
    slotDurationMinutes: z.union([z.literal(15), z.literal(20), z.literal(30), z.literal(45), z.literal(60)]),
    maxAdvanceBookingDays: z.number().min(7).max(90),
    minBookingNoticeMinutes: z.number().min(15).max(1440),
    cancellationCutoffMinutes: z.number().min(0).max(1440),
    rescheduleCutoffMinutes: z.number().min(0).max(1440)
  }),
  operatingHours: z.array(z.object({
    dayOfWeek: z.string(),
    closed: z.boolean(),
    openTime: time.optional(),
    closeTime: time.optional()
  })).min(1),
  reminders: z.object({
    morningOfEnabled: z.boolean(),
    morningOfTimeLocal: time,
    beforeAppointmentEnabled: z.boolean(),
    beforeAppointmentOffsetMinutes: z.number().min(15).max(1440)
  }),
  publicWebsite: z.object({ slug, enabled: z.boolean() }),
  whatsapp: z.object({
    wabaId: z.string().min(1),
    phoneNumberId: z.string().min(1),
    displayPhoneNumber: z.string().min(8),
    templateNamespace: z.string().min(1),
    accessTokenPlaceholder: z.string().optional(),
    appSecretPlaceholder: z.string().optional()
  }),
  firstAdmin: z.object({
    fullName: z.string().min(2),
    email: z.string().email(),
    phone: z.string().optional()
  })
});
