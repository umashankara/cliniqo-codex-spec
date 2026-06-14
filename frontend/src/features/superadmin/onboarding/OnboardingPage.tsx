import { ClinicProfileSection } from './ClinicProfileSection';
import { FirstAdminSection } from './FirstAdminSection';
import { OperatingDefaultsSection } from './OperatingDefaultsSection';
import { PublicWebsiteSection } from './PublicWebsiteSection';
import { WhatsAppMetadataSection } from './WhatsAppMetadataSection';

export function OnboardingPage() {
  return (
    <main>
      <h1>Clinic onboarding</h1>
      <form>
        <ClinicProfileSection />
        <OperatingDefaultsSection />
        <PublicWebsiteSection />
        <WhatsAppMetadataSection />
        <FirstAdminSection />
        <button type="submit">Create clinic</button>
      </form>
    </main>
  );
}
