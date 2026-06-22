import { test, expect } from '@playwright/test';
import { mockSnapshot } from './fixtures/lineStatus';

// US2: identify lines (name, number, color) and group them by operator; disrupted lines first.
test.describe('US2 — identity and operator grouping', () => {
  test('groups lines by operator and shows line identity', async ({ page }) => {
    await mockSnapshot(page);
    await page.goto('/');

    await expect(page.getByTestId('operator-group').first()).toBeVisible();
    await expect(page.getByText('Metro SP').first()).toBeVisible();
    await expect(page.getByText('CPTM').first()).toBeVisible();

    const vermelha = page.locator('[data-testid=line-card][data-category=DISRUPTED]');
    await expect(vermelha.getByText('Vermelha')).toBeVisible();
    await expect(vermelha.getByText('3')).toBeVisible();
    await expect(vermelha.getByTestId('line-color')).toHaveAttribute('data-color', '#EE3D23');
  });

  test('orders disrupted lines before normal lines (FR-014)', async ({ page }) => {
    await mockSnapshot(page);
    await page.goto('/');

    await expect(page.getByTestId('line-card').first()).toHaveAttribute('data-category', 'DISRUPTED');
    await expect(page.getByTestId('line-card').last()).toHaveAttribute('data-category', 'NORMAL');
  });
});
