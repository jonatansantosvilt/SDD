import styled from 'styled-components';

const Box = styled.div`
  background: ${({ theme }) => theme.colors.surface};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius};
  padding: 24px 16px;
  text-align: center;
  color: ${({ theme }) => theme.colors.muted};
`;

/** Shown when no status data is available at all (HTTP 503 — FR-009a). */
export function Unavailable({ message }: { message: string }) {
  return (
    <Box data-testid="unavailable" role="alert">
      {message}
    </Box>
  );
}
