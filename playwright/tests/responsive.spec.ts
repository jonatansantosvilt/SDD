import { test, expect } from '@playwright/test';
import { mockSnapshot } from './fixtures/lineStatus';

// SC-006: usable on a phone-sized screen with no horizontal overflow.
test('has no horizontal overflow at the test viewport', async ({ page }) => {
  await mockSnapshot(page);
  await page.goto('/');
  await expect(page.getByTestId('line-list')).toBeVisible();

  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth > document.documentElement.clientWidth,
  );
  expect(overflow).toBe(false);
});
