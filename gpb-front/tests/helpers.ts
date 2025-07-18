import { BrowserContext, chromium, expect, Page } from '@playwright/test';

const BASE_URL = process.env.BASE_URL || 'http://localhost:3000'
const EMAIL = process.env.E2E_EMAIL || 'admin@example.com';
const PASSWORD = process.env.E2E_PASSWORD || 'password';

export async function login(page: Page) {
    await page.goto(BASE_URL);
    await page.click('#login-button');
    await page.fill('input[name="email"]', EMAIL);
    await page.fill('input[name="password"]', PASSWORD);
    await page.click('#pills-login .btn');

    const errorLocator = page.locator('.Error');
    await expect(errorLocator).toHaveCount(0);

    const profileDropdown = page.locator('#profile-dropdown-button');
    await expect(profileDropdown).toBeVisible({ timeout: 15000 });
}

export async function searchAndOpenGame(page: Page, gameName: string) {
    await page.goto('http://localhost:3000');
    await page.fill('#game-search-input-field', gameName);
    await page.click('#game-search-button');
    await page.locator('.app-list__game').first().click();
}