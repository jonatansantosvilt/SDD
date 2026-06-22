import styled from 'styled-components';
import { formatTime } from './FreshnessIndicator';

const Banner = styled.div`
  background: ${({ theme }) => theme.colors.status.UNKNOWN}1f; /* ~12% alpha */
  border: 1px solid ${({ theme }) => theme.colors.status.UNKNOWN};
  color: ${({ theme }) => theme.colors.status.UNKNOWN};
  border-radius: ${({ theme }) => theme.radius};
  padding: 10px 14px;
  margin-bottom: 12px;
  font-size: 0.85rem;
`;

/** Shown when the API serves last-known-good data after a failed refresh (FR-009). */
export function StalenessBanner({ lastUpdated }: { lastUpdated: string | null }) {
  return (
    <Banner data-testid="stale-banner" role="status">
      ⚠ Dados podem estar desatualizados — última atualização às {formatTime(lastUpdated)}.
    </Banner>
  );
}
