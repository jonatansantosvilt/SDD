import styled from 'styled-components';
import type { StatusSnapshot } from '../api/types';
import { useLineStatus, DEFAULT_REFRESH_MS } from '../hooks/useLineStatus';
import { OperatorGroup, type OperatorGroupData } from '../components/OperatorGroup';
import { FreshnessIndicator } from '../components/FreshnessIndicator';
import { StalenessBanner } from '../components/StalenessBanner';
import { Unavailable } from '../components/Unavailable';

const Page = styled.main`
  max-width: ${({ theme }) => theme.maxWidth};
  margin: 0 auto;
  padding: 16px;
`;

const Title = styled.h1`
  font-size: 1.4rem;
  margin: 8px 0 2px;
`;

const Subtitle = styled.p`
  color: ${({ theme }) => theme.colors.muted};
  margin: 0 0 16px;
  font-size: 0.85rem;
`;

const Message = styled.p`
  color: ${({ theme }) => theme.colors.muted};
`;

const PartialNote = styled.p`
  color: ${({ theme }) => theme.colors.status.UNKNOWN};
  font-size: 0.8rem;
  margin: 0 0 8px;
`;

/**
 * Builds operator sub-groups from the disrupted-first `orderedLines` (FR-014). Walking the ordered
 * list and starting a new group whenever the operator changes keeps status as the primary ordering
 * while still showing which operator runs each line.
 */
function buildGroups(snapshot: StatusSnapshot): OperatorGroupData[] {
  const operatorByUid = new Map(snapshot.operators.map((op) => [op.uid, op]));
  const groups: OperatorGroupData[] = [];
  let current: OperatorGroupData | null = null;
  let index = 0;

  for (const line of snapshot.orderedLines) {
    if (!current || current.operatorUid !== line.operatorUid) {
      const op = operatorByUid.get(line.operatorUid);
      current = {
        key: `${line.operatorUid}-${index++}`,
        operatorUid: line.operatorUid,
        operatorName: op?.name ?? line.operatorUid,
        logoUrl: op?.logoUrl ?? null,
        lines: [],
      };
      groups.push(current);
    }
    current.lines.push(line);
  }
  return groups;
}

// Refresh cadence is 60s by default; a `?refreshMs=` query param allows tuning and deterministic tests.
function resolveInterval(): number {
  const raw = Number(new URLSearchParams(window.location.search).get('refreshMs'));
  return Number.isFinite(raw) && raw > 0 ? raw : DEFAULT_REFRESH_MS;
}

export function StatusPage() {
  const { snapshot, loading, error, unavailable, refreshing, refresh } = useLineStatus(resolveInterval());

  return (
    <Page data-testid="status-page">
      <Title>Status Metrô</Title>
      <Subtitle>Situação das linhas — Metrô &amp; CPTM · São Paulo</Subtitle>

      {snapshot && (
        <FreshnessIndicator
          lastUpdated={snapshot.lastUpdated}
          refreshing={refreshing}
          onRefresh={refresh}
        />
      )}

      {snapshot?.stale && <StalenessBanner lastUpdated={snapshot.lastUpdated} />}

      {loading && !snapshot && <Message data-testid="loading">Carregando…</Message>}

      {unavailable && !snapshot && <Unavailable message={unavailable} />}

      {error && !snapshot && !unavailable && (
        <Message data-testid="error-message">{error}</Message>
      )}

      {snapshot && snapshot.orderedLines.length === 0 && (
        <Message data-testid="empty-message">Nenhuma linha disponível no momento.</Message>
      )}

      {snapshot && snapshot.partial && (
        <PartialNote data-testid="partial-note">
          A lista pode estar incompleta.
        </PartialNote>
      )}

      {snapshot && snapshot.orderedLines.length > 0 && (
        <div data-testid="line-list">
          {buildGroups(snapshot).map((group) => (
            <OperatorGroup key={group.key} group={group} />
          ))}
        </div>
      )}
    </Page>
  );
}
