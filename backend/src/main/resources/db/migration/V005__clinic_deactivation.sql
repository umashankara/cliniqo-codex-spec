CREATE INDEX IF NOT EXISTS idx_refresh_sessions_unrevoked_user ON refresh_sessions(user_id) WHERE revoked_at IS NULL;
