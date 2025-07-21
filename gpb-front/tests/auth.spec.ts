import { test, expect } from '@playwright/test';
import { login, searchAndOpenGame } from './helpers';

const BASE_URL = process.env.BASE_URL || 'http://localhost:3000'

test.describe('User authentication E2E', () => {

    test('should show profile button after login', async ({ page }) => {
        await login(page);

        const profileDropdown = page.locator('#profile-dropdown-button');
        await expect(profileDropdown).toBeVisible();

        const text = await profileDropdown.textContent();
        if (text !== 'Profile') {
            await page.click(`#en-locale`);
        }
    })

    test('should show error after login wrong credentials', async ({ page }) => {
        await page.goto(BASE_URL);

        await expect(page.locator('#profile-dropdown-button')).toHaveCount(0);

        await page.click('#login-button');
        await page.fill('input[name="email"]', 'wrong@email.com');
        await page.fill('input[name="password"]', 'wrongpassword');
        await page.click('#pills-login .btn');

        const error = page.locator('#pills-login .Error');
        await expect(error).toBeVisible();
        await expect(error).not.toHaveText('');
    });

    test('should return to login state after user logout', async ({ page }) => {
        await login(page);

        await page.click('#profile-dropdown-button');
        await page.click('#logout-button');

        await expect(page.locator('#login-button')).toBeVisible();
        await expect(page.locator('#profile-dropdown-button')).toHaveCount(0);
    });
})