import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright config for Status Metrô UI tests.
 *
 * Tests target the frontend dev server. The frontend's /api requests are mocked
 * per-test with page.route() (see tests/fixtures/lineStatus.ts) so UI behavior can be
 * verified deterministically without a live backend. Mobile-first: default project is
 * a phone viewport, matching SC-006.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: 'html',
  use: {
    baseURL: process.env.BASE_URL ?? 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'mobile-chrome',
      use: { ...devices['Pixel 7'] },
    },
    {
      name: 'desktop-chrome',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'npm --prefix ../frontend run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
    timeout: 120_000,
  },
});
