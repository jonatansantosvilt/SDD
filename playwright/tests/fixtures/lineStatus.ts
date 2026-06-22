import type { Page } from '@playwright/test';

const ENDPOINT = '**/api/v1/line-status';

// A representative snapshot: one disrupted line, one unknown, several normal, two operators.
export const sampleSnapshot = {
  lastUpdated: '2026-06-22T18:47:59',
  fetchedAt: '2026-06-22T18:48:01',
  stale: false,
  available: true,
  partial: false,
  operators: [
    {
      uid: 'METRO',
      name: 'Metro SP',
      state: 'SP',
      logoUrl: null,
      lines: [
        line('METRO-L1', '1', 'Azul', '#0A3D91', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
        line('METRO-L2', '2', 'Verde', '#007A33', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
        line('METRO-L3', '3', 'Vermelha', '#EE3D23', 'VelocidadeReduzida', 'Velocidade Reduzida', 'DISRUPTED'),
      ],
    },
    {
      uid: 'CPTM',
      name: 'CPTM',
      state: 'SP',
      logoUrl: null,
      lines: [
        line('CPTM-L8', '8', 'Diamante', '#00A4A7', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
        line('CPTM-L9', '9', 'Esmeralda', '#9B59B6', 'SituacaoNova', 'Situação Não Informada', 'UNKNOWN'),
      ],
    },
  ],
  orderedLines: [
    line('METRO-L3', '3', 'Vermelha', '#EE3D23', 'VelocidadeReduzida', 'Velocidade Reduzida', 'DISRUPTED'),
    line('CPTM-L9', '9', 'Esmeralda', '#9B59B6', 'SituacaoNova', 'Situação Não Informada', 'UNKNOWN'),
    line('METRO-L1', '1', 'Azul', '#0A3D91', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
    line('METRO-L2', '2', 'Verde', '#007A33', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
    line('CPTM-L8', '8', 'Diamante', '#00A4A7', 'OperacaoNormal', 'Operação Normal', 'NORMAL'),
  ],
};

export const staleSnapshot = {
  ...sampleSnapshot,
  stale: true,
  lastUpdated: '2026-06-22T18:30:00',
};

export const unavailableBody = {
  available: false,
  message: 'Status temporariamente indisponível. Tente novamente em instantes.',
};

function line(
  uid: string, number: string, name: string, colorRgb: string,
  code: string, label: string, category: 'NORMAL' | 'DISRUPTED' | 'UNKNOWN',
) {
  return {
    uid, number, name, colorRgb, iconUrl: null, operatorUid: uid.split('-')[0],
    status: { code, label, description: null, category },
  };
}

/** Mocks GET /api/v1/line-status with a 200 snapshot. */
export async function mockSnapshot(page: Page, body: unknown = sampleSnapshot) {
  await page.route(ENDPOINT, (route) =>
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(body) }),
  );
}

/** Mocks GET /api/v1/line-status with a 503 unavailable response. */
export async function mockUnavailable(page: Page) {
  await page.route(ENDPOINT, (route) =>
    route.fulfill({ status: 503, contentType: 'application/json', body: JSON.stringify(unavailableBody) }),
  );
}

/** Mocks the endpoint with a sequence of responses (one per call) for refresh testing. */
export async function mockSequence(page: Page, bodies: unknown[]) {
  let i = 0;
  await page.route(ENDPOINT, (route) => {
    const body = bodies[Math.min(i, bodies.length - 1)];
    i += 1;
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(body) });
  });
}
