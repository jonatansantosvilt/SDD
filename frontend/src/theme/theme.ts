import type { StatusCategory } from '../api/types';

export const theme = {
  colors: {
    bg: '#0f1419',
    surface: '#1a212b',
    surface2: '#222c38',
    text: '#e8edf2',
    muted: '#9aa7b4',
    border: '#2c3744',
    status: {
      NORMAL: '#2ec27e',
      DISRUPTED: '#e5484d',
      UNKNOWN: '#f5a623',
    } as Record<StatusCategory, string>,
  },
  radius: '14px',
  maxWidth: '480px',
  breakpoints: {
    phone: '480px',
  },
};

export type AppTheme = typeof theme;

/** Portuguese short label shown on the status badge for each category. */
export const categoryBadgeLabel: Record<StatusCategory, string> = {
  NORMAL: 'Normal',
  DISRUPTED: 'Ocorrência',
  UNKNOWN: 'Desconhecido',
};
