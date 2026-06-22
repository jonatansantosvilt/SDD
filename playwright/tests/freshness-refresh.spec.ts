import { test, expect } from '@playwright/test';
import {
  mockSnapshot,
  mockSequence,
  mockUnavailable,
  sampleSnapshot,
  staleSnapshot,
} from './fixtures/lineStatus';

// US3: data freshness, manual + automatic refresh, staleness, and unavailability.
test.describe('US3 — freshness, refresh, and resilience', () => {
  test('shows the last-updated time', async ({ page }) => {
    await mockSnapshot(page);
    await page.goto('/');
    // sampleSnapshot.lastUpdated = 2026-06-22T18:47:59 → 18:47
    await expect(page.getByTestId('last-updated')).toHaveText('18:47');
  });

  test('manual refresh re-fetches and updates the time', async ({ page }) => {
    const updated = { ...sampleSnapshot, lastUpdated: '2026-06-22T18:50:00' };
    await mockSequence(page, [sampleSnapshot, updated]);
    await page.goto('/');

    await expect(page.getByTestId('last-updated')).toHaveText('18:47');
    await page.getByTestId('refresh-button').click();
    await expect(page.getByTestId('last-updated')).toHaveText('18:50');
  });

  test('auto-refreshes on the configured interval', async ({ page }) => {
    const updated = { ...sampleSnapshot, lastUpdated: '2026-06-22T18:50:00' };
    await mockSequence(page, [sampleSnapshot, updated]);
    await page.goto('/?refreshMs=500');

    await expect(page.getByTestId('last-updated')).toHaveText('18:47');
    // Without any user action, the next poll (~500ms) updates the time.
    await expect(page.getByTestId('last-updated')).toHaveText('18:50', { timeout: 5000 });
  });

  test('shows a staleness banner when serving last-known-good', async ({ page }) => {
    await mockSnapshot(page, staleSnapshot);
    await page.goto('/');
    await expect(page.getByTestId('stale-banner')).toBeVisible();
    // still shows the lines
    await expect(page.getByTestId('line-card').first()).toBeVisible();
  });

  test('shows a friendly message when no data is available', async ({ page }) => {
    await mockUnavailable(page);
    await page.goto('/');
    await expect(page.getByTestId('unavailable')).toBeVisible();
    await expect(page.getByTestId('line-card')).toHaveCount(0);
  });
});
