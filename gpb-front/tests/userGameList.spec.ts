import { test, expect } from '@playwright/test';
import { login, searchAndOpenGame } from './helpers';

const GAME_NAME = 'Minecraft';

test.describe('User game list E2E', () => {

    test('should subscribe to the game successfully', async ({ page }) => {
        await login(page);
        await searchAndOpenGame(page, GAME_NAME);

        const subscribeButton = page.locator('#subscribe-button');
        const currentText = await subscribeButton.innerText();

        if (currentText === 'Unsubscribe') {
            await subscribeButton.click();
            await subscribeButton.waitFor();
        }
        await subscribeButton.click();
        await expect(subscribeButton).toHaveText('Unsubscribe');
    });

    test('should unsubscribe from the game successfully', async ({ page }) => {
        await login(page);
        await searchAndOpenGame(page, GAME_NAME);

        const subscribeButton = page.locator('#subscribe-button');
        const currentText = await subscribeButton.innerText();

        if (currentText === 'Subscribe') {
            await subscribeButton.click();
            await subscribeButton.waitFor();
        }
        await subscribeButton.click();
        await expect(subscribeButton).toHaveText('Subscribe');
    });

    test('should show game in user game list after subscription', async ({ page }) => {
        await login(page);
        await searchAndOpenGame(page, GAME_NAME);

        const gameTitle = await page.locator('.app-game__title').innerText();
        const subscribeButton = page.locator('#subscribe-button');
        if ((await subscribeButton.innerText()) === 'Unsubscribe') {
            await subscribeButton.click();
            await subscribeButton.waitFor();
        }
        await subscribeButton.click();

        await page.click('#profile-dropdown-button');
        await page.click('#user-gameList-button');

        await expect(page.locator('.app-list__game-title').first()).toBeVisible();

        const gameTitles = await page.locator('.app-list__game-title');
        const count = await gameTitles.count();
        let found = false;

        for (let i = 0; i < count; i++) {
            const text = await gameTitles.nth(i).innerText();
            if (text.trim() === gameTitle.trim()) {
                found = true;
                break;
            }
        }

        expect(found).toBe(true);
    });
})