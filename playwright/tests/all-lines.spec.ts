import { test, expect } from '@playwright/test';
import { mockSnapshot } from './fixtures/lineStatus';

// US1: A commuter sees every Metrô and CPTM line with its current status.
test.describe('US1 — current status of all lines', () => {
  test('lists every line with a human-readable status', async ({ page }) => {
    await mockSnapshot(page);
    await page.goto('/');

    await expect(page.getByTestId('line-card')).toHaveCount(5);
    await expect(page.getByText('Azul')).toBeVisible();
    await expect(page.getByText('Vermelha')).toBeVisible();
    await expect(page.getByText('Velocidade Reduzida')).toBeVisible();
    await expect(page.getByText('Operação Normal').first()).toBeVisible();
  });

  test('visually distinguishes disrupted, unknown, and normal lines', async ({ page }) => {
    await mockSnapshot(page);
    await page.goto('/');

    await expect(page.locator('[data-testid=line-card][data-category=DISRUPTED]')).toHaveCount(1);
    await expect(page.locator('[data-testid=line-card][data-category=UNKNOWN]')).toHaveCount(1);
    await expect(page.locator('[data-testid=line-card][data-category=NORMAL]')).toHaveCount(3);
  });
});
