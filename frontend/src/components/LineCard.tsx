import styled from 'styled-components';
import type { Line } from '../api/types';
import { StatusBadge } from './StatusBadge';

const Card = styled.article<{ $disrupted: boolean }>`
  display: flex;
  align-items: center;
  gap: 12px;
  background: ${({ theme }) => theme.colors.surface};
  border: 1px solid ${({ theme, $disrupted }) => ($disrupted ? theme.colors.status.DISRUPTED : theme.colors.border)};
  border-radius: ${({ theme }) => theme.radius};
  padding: 12px 14px;
  margin-bottom: 8px;
`;

const Num = styled.span<{ $color: string | null }>`
  width: 34px;
  height: 34px;
  flex: 0 0 34px;
  border-radius: 9px;
  display: grid;
  place-items: center;
  font-weight: 700;
  color: #0b0e12;
  background: ${({ $color, theme }) => $color ?? theme.colors.surface2};
`;

const Meta = styled.div`
  flex: 1;
  min-width: 0;
`;

const Name = styled.div`
  font-weight: 600;
`;

const StatusText = styled.div`
  font-size: 0.82rem;
  color: ${({ theme }) => theme.colors.muted};
`;

export function LineCard({ line }: { line: Line }) {
  const disrupted = line.status.category === 'DISRUPTED';
  return (
    <Card $disrupted={disrupted} data-testid="line-card" data-category={line.status.category}>
      <Num
        $color={line.colorRgb}
        data-testid="line-color"
        data-color={line.colorRgb ?? ''}
        aria-hidden="true"
      >
        {line.number}
      </Num>
      <Meta>
        <Name>{line.name}</Name>
        <StatusText>{line.status.label}</StatusText>
      </Meta>
      <StatusBadge category={line.status.category} />
    </Card>
  );
}
