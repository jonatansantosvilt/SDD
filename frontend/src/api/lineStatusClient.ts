import type { StatusSnapshot, Unavailable } from './types';

const ENDPOINT = '/api/v1/line-status';

/** Raised when the backend reports that no status data is available yet (HTTP 503). */
export class UnavailableError extends Error {
  constructor(public readonly body: Unavailable) {
    super(body.message);
    this.name = 'UnavailableError';
  }
}

/**
 * Fetches the current line-status snapshot from the backend.
 * - 200 → returns the snapshot (which may be flagged `stale`).
 * - 503 → throws {@link UnavailableError} with the friendly message.
 * - other failures → throws a generic Error.
 */
export async function getLineStatus(signal?: AbortSignal): Promise<StatusSnapshot> {
  const response = await fetch(ENDPOINT, {
    headers: { Accept: 'application/json' },
    signal,
  });

  if (response.status === 503) {
    const body = (await response.json()) as Unavailable;
    throw new UnavailableError(body);
  }
  if (!response.ok) {
    throw new Error(`Falha ao carregar o status das linhas (HTTP ${response.status}).`);
  }
  return (await response.json()) as StatusSnapshot;
}
