import styled from 'styled-components';

const Bar = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: ${({ theme }) => theme.colors.surface};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius};
  padding: 10px 14px;
  margin-bottom: 8px;
  font-size: 0.85rem;
`;

const Muted = styled.span`
  color: ${({ theme }) => theme.colors.muted};
`;

const RefreshButton = styled.button`
  background: ${({ theme }) => theme.colors.surface2};
  color: ${({ theme }) => theme.colors.text};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: 999px;
  padding: 6px 14px;
  font-size: 0.8rem;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: default;
  }
`;

/** Formats an ISO-8601 local timestamp (no offset) to HH:MM. */
export function formatTime(iso: string | null | undefined): string {
  if (!iso) return '—';
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return '—';
  const hh = String(date.getHours()).padStart(2, '0');
  const mm = String(date.getMinutes()).padStart(2, '0');
  return `${hh}:${mm}`;
}

export function FreshnessIndicator({
  lastUpdated,
  refreshing,
  onRefresh,
}: {
  lastUpdated: string | null;
  refreshing: boolean;
  onRefresh: () => void;
}) {
  return (
    <Bar data-testid="freshness">
      <Muted>
        Atualizado às <strong data-testid="last-updated">{formatTime(lastUpdated)}</strong>
      </Muted>
      <RefreshButton
        type="button"
        onClick={onRefresh}
        disabled={refreshing}
        data-testid="refresh-button"
        aria-label="Atualizar"
      >
        ↻ Atualizar
      </RefreshButton>
    </Bar>
  );
}
