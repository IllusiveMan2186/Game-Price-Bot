// gameSearch.spec.ts
import { test, expect } from '@playwright/test';

const GAME_NAME = 'Minecraft';

test.describe('User game search E2E', () => {

    test('should find the needed game by search', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.fill('#game-search-input-field', GAME_NAME);
        await page.click('#game-search-button');

        const gameTitle = await page.locator('.app-list__game-title').first().innerText();
        expect(gameTitle).toContain(GAME_NAME);
    });

    test('should verify all sections of the game info page', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.fill('#game-search-input-field', GAME_NAME);
        await page.click('#game-search-button');
        await page.locator('.app-list__game').first().click();

        await expect(page.locator('.app-game-page')).toBeVisible();
        await expect(page.locator('.app-game-page-image')).toBeVisible();
        await expect(page.locator('.app-game__info')).not.toHaveText('');
        await expect(page.locator('.app-game__details-price')).not.toHaveText('');
        await expect(page.locator('.app-game-type')).not.toHaveText('');
        await expect(page.locator('.app-game-available').first()).not.toHaveText('');
        await expect(page.locator('.app-game-price')).not.toHaveText('');

        await expect(page.locator('.app-game__subscribe')).toBeVisible();
        await expect(page.locator('.app-game__store-list')).toBeVisible();
    });

    test('should redirect to store page when user clicks on game in store', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.fill('#game-search-input-field', GAME_NAME);
        await page.click('#game-search-button');
        await page.locator('.app-list__game').first().click();

        const storeItem = page.locator('.app-store__item').first();
        await expect(storeItem).toBeVisible();

        await storeItem.click();
    });
})