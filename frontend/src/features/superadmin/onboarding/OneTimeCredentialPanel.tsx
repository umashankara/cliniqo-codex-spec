export function OneTimeCredentialPanel({ temporaryPassword, onDismiss }: { temporaryPassword?: string; onDismiss?: () => void }) {
  if (!temporaryPassword) {
    return <div role="status">Temporary password is not recoverable. Use reset flow if it is lost.</div>;
  }
  return (
    <section aria-label="one-time temporary password">
      <h2>First admin credential</h2>
      <code>{temporaryPassword}</code>
      <button type="button" onClick={onDismiss}>Dismiss</button>
    </section>
  );
}
