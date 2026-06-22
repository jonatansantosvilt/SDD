import { useCallback, useEffect, useRef, useState } from 'react';
import { getLineStatus, UnavailableError } from '../api/lineStatusClient';
import type { StatusSnapshot } from '../api/types';

export const DEFAULT_REFRESH_MS = 60_000;

export interface UseLineStatus {
  snapshot: StatusSnapshot | null;
  loading: boolean;
  /** Generic load error (network/server) when no snapshot could be shown. */
  error: string | null;
  /** Friendly message when the backend reports no data has ever loaded (HTTP 503). */
  unavailable: string | null;
  /** True while a fetch (initial or refresh) is in flight. */
  refreshing: boolean;
  /** Manually re-fetch the snapshot now. */
  refresh: () => void;
}

/**
 * Fetches the line-status snapshot on mount, refreshes automatically every {@code intervalMs}
 * (default 60s — Spec FR-008), and exposes a manual {@link UseLineStatus.refresh} (FR-007).
 */
export function useLineStatus(intervalMs: number = DEFAULT_REFRESH_MS): UseLineStatus {
  const [snapshot, setSnapshot] = useState<StatusSnapshot | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [unavailable, setUnavailable] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const inFlight = useRef<AbortController | null>(null);

  const load = useCallback(async () => {
    inFlight.current?.abort();
    const controller = new AbortController();
    inFlight.current = controller;
    setRefreshing(true);
    try {
      const data = await getLineStatus(controller.signal);
      setSnapshot(data);
      setError(null);
      setUnavailable(null);
    } catch (err: unknown) {
      if (err instanceof DOMException && err.name === 'AbortError') return;
      if (err instanceof UnavailableError) {
        setUnavailable(err.message);
      } else {
        setError('Não foi possível carregar o status das linhas.');
      }
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void load();
    const id = window.setInterval(() => void load(), intervalMs);
    return () => {
      window.clearInterval(id);
      inFlight.current?.abort();
    };
  }, [load, intervalMs]);

  return { snapshot, loading, error, unavailable, refreshing, refresh: load };
}
