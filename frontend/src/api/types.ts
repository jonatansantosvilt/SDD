// Types mirroring the backend REST contract
// (specs/001-sp-line-status/contracts/line-status-api.yaml).

export type StatusCategory = 'NORMAL' | 'DISRUPTED' | 'UNKNOWN';

export interface LineStatus {
  code: string;
  label: string;
  description: string | null;
  category: StatusCategory;
}

export interface Line {
  uid: string;
  number: string;
  name: string;
  colorRgb: string | null;
  iconUrl: string | null;
  operatorUid: string;
  status: LineStatus;
}

export interface Operator {
  uid: string;
  name: string;
  state: string;
  logoUrl: string | null;
  lines: Line[];
}

export interface StatusSnapshot {
  lastUpdated: string;
  fetchedAt: string;
  stale: boolean;
  available: boolean;
  partial: boolean;
  operators: Operator[];
  orderedLines: Line[];
}

/** Body returned with HTTP 503 when no snapshot has ever loaded. */
export interface Unavailable {
  available: false;
  message: string;
}
