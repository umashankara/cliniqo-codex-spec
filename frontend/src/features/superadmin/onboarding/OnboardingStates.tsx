export function LoadingState() { return <div role="status">Loading</div>; }
export function PermissionDeniedState() { return <div role="alert">Permission denied</div>; }
export function UnexpectedErrorState({ requestId }: { requestId?: string }) {
  return <div role="alert">Unable to complete onboarding{requestId ? ` (${requestId})` : ''}</div>;
}
