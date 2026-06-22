import styled from 'styled-components';
import type { StatusCategory } from '../api/types';
import { categoryBadgeLabel } from '../theme/theme';

const Badge = styled.span<{ $category: StatusCategory }>`
  font-size: 0.72rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 999px;
  white-space: nowrap;
  color: ${({ theme, $category }) => theme.colors.status[$category]};
  background: ${({ theme, $category }) => theme.colors.status[$category]}26; /* ~15% alpha */
`;

export function StatusBadge({ category }: { category: StatusCategory }) {
  return (
    <Badge $category={category} data-testid="status-badge" data-category={category}>
      {categoryBadgeLabel[category]}
    </Badge>
  );
}
