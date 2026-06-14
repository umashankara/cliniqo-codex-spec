import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { OnboardingPage } from '../features/superadmin/onboarding/OnboardingPage';
import { SuperAdminRoute } from '../routes/SuperAdminRoute';

const localUser = { userId: 'local', email: 'superadmin@cliniqo.local', role: 'SUPER_ADMIN' as const, clinicId: null };

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/super-admin/onboarding" element={<SuperAdminRoute user={localUser}><OnboardingPage /></SuperAdminRoute>} />
        <Route path="*" element={<OnboardingPage />} />
      </Routes>
    </BrowserRouter>
  );
}
