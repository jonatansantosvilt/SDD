import styled from 'styled-components';
import type { Line } from '../api/types';
import { LineCard } from './LineCard';

const Section = styled.section`
  margin: 18px 0 8px;
`;

const Header = styled.h2`
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: ${({ theme }) => theme.colors.muted};
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 8px;
`;

const Logo = styled.img`
  height: 16px;
  width: auto;
`;

export interface OperatorGroupData {
  /** Unique key for this rendered group (operator may appear in both the disrupted and normal blocks). */
  key: string;
  operatorUid: string;
  operatorName: string;
  logoUrl: string | null;
  lines: Line[];
}

export function OperatorGroup({ group }: { group: OperatorGroupData }) {
  return (
    <Section data-testid="operator-group" data-operator={group.operatorUid}>
      <Header>
        {group.logoUrl && <Logo src={group.logoUrl} alt="" />}
        {group.operatorName}
      </Header>
      {group.lines.map((line) => (
        <LineCard key={line.uid} line={line} />
      ))}
    </Section>
  );
}
