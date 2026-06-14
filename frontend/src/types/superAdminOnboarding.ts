export type UserRole = 'SUPER_ADMIN' | 'CLINIC_ADMIN' | 'DOCTOR' | 'RECEPTIONIST';

export interface AuthUser {
  userId: string;
  email: string;
  role: UserRole;
  clinicId?: string | null;
}

export interface ApiMeta {
  requestId: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string>;
}

export interface ApiEnvelope<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  meta: ApiMeta;
}

export interface ClinicOnboardingPayload {
  clinic: {
    name: string;
    slug: string;
    address?: string;
    country: string;
    timezone: string;
    defaultLanguage: string;
    primaryPhone: string;
    email: string;
  };
  settings: {
    slotDurationMinutes: number;
    maxAdvanceBookingDays: number;
    minBookingNoticeMinutes: number;
    cancellationCutoffMinutes: number;
    rescheduleCutoffMinutes: number;
  };
  operatingHours: Array<{ dayOfWeek: string; closed: boolean; openTime?: string; closeTime?: string }>;
  reminders: {
    morningOfEnabled: boolean;
    morningOfTimeLocal: string;
    beforeAppointmentEnabled: boolean;
    beforeAppointmentOffsetMinutes: number;
  };
  publicWebsite: { slug: string; enabled: boolean };
  whatsapp: {
    wabaId: string;
    phoneNumberId: string;
    displayPhoneNumber: string;
    templateNamespace: string;
    accessTokenPlaceholder?: string;
    appSecretPlaceholder?: string;
  };
  firstAdmin: { fullName: string; email: string; phone?: string };
}

export interface ClinicOnboardingResult {
  clinicId: string;
  clinicSlug: string;
  publicWebsiteSlug: string;
  firstAdminUserId: string;
  firstAdminEmail: string;
  temporaryPassword?: string;
  forcePasswordReset: true;
  oneTimeCredential: true;
}
