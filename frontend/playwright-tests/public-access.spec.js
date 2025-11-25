const { test, expect } = require('@playwright/test');

const BASE_URL = 'http://localhost:3000';

test.describe('Public Access and Survey Functionality', () => {
  test('Homepage loads for unauthenticated user', async ({ page }) => {
    await page.goto(BASE_URL);
    await expect(page).toHaveTitle('Chung Tay Phòng Ngừa');
    await expect(page.locator('text=Đăng nhập')).toBeVisible();
  });

  test('Blog page loads for unauthenticated user', async ({ page }) => {
    await page.goto(`${BASE_URL}/blog`);
    await expect(page.locator('text=Blog Chung Tay Phòng Ngừa')).toBeVisible(); // Adjust as needed
  });

  test('Public surveys page loads and lists surveys', async ({ page }) => {
    await page.goto(`${BASE_URL}/surveys`);
    await expect(page.locator('text=Khảo sát cộng đồng')).toBeVisible();
    const count = await page.locator('button:has-text("Tham gia khảo sát")').count();
    expect(count).toBeGreaterThan(0);
  });

  test('Can view and submit a public survey', async ({ page }) => {
    await page.goto(`${BASE_URL}/surveys`);
    // Click the first "Tham gia khảo sát" button
    await page.locator('button:has-text("Tham gia khảo sát")').first().click();
    // Loop through questions
    while (await page.locator('button:has-text("Câu tiếp theo")').isVisible()) {
      // Try to select the first radio/checkbox/textarea
      const radios = await page.$$('input[type="radio"]');
      if (radios.length > 0) {
        await radios[0].check();
      } else {
        const checkboxes = await page.$$('input[type="checkbox"]');
        if (checkboxes.length > 0) {
          await checkboxes[0].check();
        } else {
          const textarea = await page.$('textarea');
          if (textarea) await textarea.fill('Test answer');
        }
      }
      await page.click('button:has-text("Câu tiếp theo")');
    }
    // On last question, click "Hoàn thành"
    if (await page.locator('button:has-text("Hoàn thành")').isVisible()) {
      await page.click('button:has-text("Hoàn thành")');
    }
    // Expect completion message
    await expect(page.locator('text=Cảm ơn bạn đã tham gia!')).toBeVisible();
  });
}); 